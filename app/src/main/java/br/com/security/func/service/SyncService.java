package br.com.security.func.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.security.func.config.Session;
import br.com.security.func.dao.CheckinDAO;
import br.com.security.func.dao.ClienteDAO;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.orm.Cliente;
import br.com.security.func.models.stub.LocalizacaoCliente;
import br.com.security.func.models.stub.SyncCheckins;
import br.com.security.func.models.stub.SyncClientes;
import br.com.security.func.net.CheckinService;
import br.com.security.func.net.ClienteService;
import br.com.security.func.utils.Constants;
import br.com.security.func.utils.GsonClienteAdapter;
import br.com.security.func.utils.GsonUTCDateAdapter;

/**
 * Created by mariomartins on 14/09/17.
 */

public class SyncService extends IntentService {

    public static final String ARG_SYNC_TYPE = "sync_type";

    public static final String ARG_SYNC_CHECKINS_DOWNLOAD = "sync_checkins_download";
    public static final String ARG_SYNC_CLIENTES_DOWNLOAD = "sync_clientes_download";
    public static final String ARG_SYNC_CHECKINS_UPLOAD = "sync_checkins_upload";
    public static final String ARG_SYNC_CLIENTES_VISITADOS = "sync_clientes_visitados";
    public static final String ARG_SYNC_CHECKINS_CLIENTES_DOWNLOAD = "sync_checkins_clientes_download";
    public static final String ARG_SYNC_BOTH = "sync_both";

    private ClienteDAO clienteDAO;
    private CheckinDAO checkinDAO;

    private boolean syncClienteDone = false;
    private boolean syncClienteNaoVisitados = false;
    private boolean syncCheckinDone = false;
    private boolean syncCheckinUpDone = false;

    private OnDoneAsyncRequest onDoneAsyncRequest = new OnDoneAsyncRequest() {

        @Override
        public void onDone() {

            if (syncClienteDone && syncCheckinDone && syncCheckinUpDone && syncClienteNaoVisitados)
                done();

        }
    };

    public SyncService() {
        super("SyncService");
    }

    public SyncService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        clienteDAO = ClienteDAO.getInstance(getApplicationContext());
        checkinDAO = CheckinDAO.getInstance(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String syncType = intent.getExtras().getString(ARG_SYNC_TYPE);

        try {

            if (syncType.equals(ARG_SYNC_CLIENTES_DOWNLOAD)) {
                syncronizeClientes();
            } else if (syncType.equals(ARG_SYNC_CHECKINS_DOWNLOAD) || syncType.equals(ARG_SYNC_CHECKINS_UPLOAD)) {
                syncronizeCheckins(syncType);
            } else if (syncType.equals(ARG_SYNC_BOTH) || syncType.equals(ARG_SYNC_CHECKINS_CLIENTES_DOWNLOAD)) {
                syncronizeClienteLocalizacoes();
                syncronizeClientes();
                syncronizeCheckins(syncType);
            } else if (syncType.equals(ARG_SYNC_CLIENTES_VISITADOS)) {
                syncronizeClienteLocalizacoes();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void syncronizeCheckinsUpload() {

        try {

            final List<Checkin> desincronizados = checkinDAO.buscarDesincronizados();

            Log.v(getClass().getName(), "Desincronizados: " + desincronizados.size());

            CheckinService.postSyncCheckins(getApplicationContext(), desincronizados, new Response.Listener() {

                @Override
                public void onResponse(Object response) {

                    // Data da sincronização retornada pelo servidor
                    if (Long.parseLong(response.toString()) != -1) {

                        for (Checkin checkin : desincronizados) {

                            try {
                                checkinDAO.marcarComoSincronizado(checkin.getId());
                                Log.v(getClass().getName(), "Marcou como sincronizado: " + checkin.getId());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        // salva a data de sincronizacao dos checkins após esse upload, para não baixar novamente esse checkin
                        Session session = Session.withContext(getApplicationContext());
                        session.saveSyncDateCheckinsDown(session.getAuthencationUser().getId(), Long.parseLong(response.toString()));

                    }

                    // finalizou
                    doneSyncCheckinsUp();

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.v(getClass().getName(), error.toString());
                    
                    // finalizou
                    doneSyncCheckinsUp();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void syncronizeCheckins(final String syncType) {

        // busca os checkins do funcionário atual, que foram sincronizados após a data da última sincronização desse funcionário
        CheckinService.getSyncCheckins(getApplicationContext(), Session.withContext(getApplicationContext()).getAuthencationUser().getId(), new Response.Listener() {

            @Override
            public void onResponse(Object response) {

                SyncCheckins syncCheckins = new GsonBuilder()
                        .registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
                        .registerTypeAdapter(Cliente.class, new GsonClienteAdapter())
                        .create()
                        .fromJson(response.toString(), new TypeToken<SyncCheckins>() {}.getType());

                for (Checkin c : syncCheckins.getCheckins()) {

                    try {
                        c.setSync(true);
                        checkinDAO.save(c);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                Session session = Session.withContext(getApplicationContext());
                session.saveSyncDateCheckinsDown(session.getAuthencationUser().getId(), syncCheckins.getDataSync());

                // finalizou
                doneSyncCheckins(syncType);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.v(getClass().getName(), error.networkResponse.toString());

                // finalizou
                doneSyncCheckins(syncType);
            }
        });

    }

    private void syncronizeClientes() {

        // busca os clientes alterados, pois ainda não foram sincronizados com o app
        ClienteService.getSyncClientes(getApplicationContext(), Session.withContext(getApplicationContext()).getAuthencationUser().getSyncDataClientesDown(), new Response.Listener() {

            @Override
            public void onResponse(Object response) {

                SyncClientes syncClientes = new Gson().fromJson(response.toString(), new TypeToken<SyncClientes>() {
                }.getType());

                // instancia lista de clientes, caso não encontre nenhum cliente a ser sincronizado
                syncClientes.setClientes(syncClientes.getClientes() == null ? new ArrayList<Cliente>() : syncClientes.getClientes());

                // salva os clientes não sincronizados
                for (Cliente c : syncClientes.getClientes()) {

                    try {
                        c.setVisitado(c.hasCoords());
                        clienteDAO.save(c);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                // salva a data da sincronização
                if (!syncClientes.getClientes().isEmpty())
                    Session.withContext(getApplicationContext()).saveSyncDateClientesDown(syncClientes.getDataSync());

                // finalizou
                doneSyncClientes();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                // finalizou
                doneSyncClientes();
            }
        });

    }

    private void syncronizeClienteLocalizacoes() throws SQLException {

        final List<LocalizacaoCliente> localizacaoClientes = clienteDAO.buscarNaoVisitados();

        // busca os clientes alterados, pois ainda não foram sincronizados com o app
        ClienteService.postClientesLocalizacoes(getApplicationContext(), localizacaoClientes, new Response.Listener() {

            @Override
            public void onResponse(Object response) {

                if (!response.toString().isEmpty()) {

                    for (LocalizacaoCliente loc : localizacaoClientes) {

                        try {
                            clienteDAO.marcarVisitado(loc.getId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                }

                // finalizou
                doneSyncClientesNaoVisitados();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                // finalizou
                doneSyncClientesNaoVisitados();
            }
        });

    }

    private void done() {
        //TODO: Enviar em broadcast a mensagem com o tipo de sincronziação concluida
        Intent localIntent = new Intent(Constants.BROADCAST_SYNC_SERVICE_ALL).putExtra(Constants.SYNC_SERVICE_DATA_STATUS, Constants.SYNC_SERVICE_DATA_STATUS_FINISH);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void doneSyncClientes() {
        syncClienteDone = true;
        onDoneAsyncRequest.onDone();
    }

    private void doneSyncClientesNaoVisitados() {
        syncClienteNaoVisitados = true;
        onDoneAsyncRequest.onDone();
    }

    private void doneSyncCheckins(final String syncType) {
        syncCheckinDone = true;
        onDoneAsyncRequest.onDone();

        if (syncType.equals(ARG_SYNC_BOTH) || syncType.equals(ARG_SYNC_CHECKINS_UPLOAD))
            syncronizeCheckinsUpload();

    }

    private void doneSyncCheckinsUp() {
        syncCheckinUpDone = true;
        onDoneAsyncRequest.onDone();
    }

    interface OnDoneAsyncRequest {
        void onDone();
    }

}
