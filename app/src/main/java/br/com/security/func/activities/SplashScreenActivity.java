package br.com.security.func.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import br.com.security.func.R;
import br.com.security.func.config.App;
import br.com.security.func.config.Session;
import br.com.security.func.config.DatabaseHelper;
import br.com.security.func.models.stub.User;
import br.com.security.func.net.AuthenticationService;
import br.com.security.func.receivers.SyncStatusReceiver;
import br.com.security.func.service.Router;
import br.com.security.func.service.SyncService;
import br.com.security.func.utils.Constants;

public class SplashScreenActivity extends AppCompatActivity {

    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter syncStatusFilter = new IntentFilter(Constants.BROADCAST_SYNC_SERVICE_ALL);
    private SyncStatusReceiver syncStatusReceiver = new SyncStatusReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        // ao alterar status da sincronização
        syncStatusReceiver.setSyncStatusListener(new SyncStatusReceiver.SyncStatusListener() {

            @Override
            public void onStatusChange(Object status) {
                Router.goDashboardView(SplashScreenActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final User user = Session.withContext(getApplicationContext()).getAuthencationUser();

        // verifica se existe algum usuário na sessão local
        if (user.getAuthToken() != null && !user.getAuthToken().trim().isEmpty()) {

            try {

                // valida a sessão na internet desse usuário
                AuthenticationService.doAuth(getApplicationContext(), user.getAuthToken(), new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        // inicializa banco de dados
                        new DatabaseHelper(getApplicationContext()).getWritableDatabase();

                        // registra o broadcast receiver para obter o status da sincronziação
                        localBroadcastManager.registerReceiver(syncStatusReceiver, syncStatusFilter);

                        // inicia sincronização de dados em background através da internet
                        App.requestSyncService(SplashScreenActivity.this, SyncService.ARG_SYNC_BOTH);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null)
                            Router.goAuthView(SplashScreenActivity.this);
                        else
                            Router.goDashboardView(SplashScreenActivity.this);

                    }
                });

            } catch (AuthFailureError authFailureError) {
                authFailureError.printStackTrace();
            }

        } else {

            new CountDownTimer(2000l, 2000l) {

                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    Router.goAuthView(SplashScreenActivity.this);
                }

            }.start();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (syncStatusReceiver != null)
            localBroadcastManager.unregisterReceiver(syncStatusReceiver);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAffinity(this);
    }
}
