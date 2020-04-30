package br.com.security.func.fragments;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.sql.SQLException;

import br.com.security.func.R;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.config.App;
import br.com.security.func.config.Session;
import br.com.security.func.dao.ClienteDAO;
import br.com.security.func.dialogs.AppDialog;
import br.com.security.func.location.LocationService;
import br.com.security.func.models.orm.Cliente;
import br.com.security.func.utils.Geo;
import br.com.security.func.wrappers.InspecaoWrapper;

public class InspecionarManualFragment extends Fragment implements LocationListener {

    private EditText edCode;
    private LocationService locationService;
    private Menu menu;
    private Location location;

    public static InspecionarManualFragment newInstance() {
        InspecionarManualFragment fragment = new InspecionarManualFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspecionar_manual, container, false);

        edCode = view.findViewById(R.id.edClienteCode);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        edCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean visible = edCode.getText().length() > 0;
                menu.getItem(0).getActionView().animate().alpha(visible ? 1f : 0f);
                menu.getItem(1).getActionView().animate().alpha(visible ? 1f : 0f);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.inspecao_manual_menu, menu);

        ImageView icQuit = new ImageView(getActivity());
        icQuit.setImageResource(R.drawable.ic_quit);
        icQuit.setPadding(App.getPixelsInDP(5), App.getPixelsInDP(5), App.getPixelsInDP(20), App.getPixelsInDP(5));
        icQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).loadDashboardFragment(false);
                App.closeKeyboard((AppCompatActivity) getActivity());
            }
        });

        ImageView icAccept = new ImageView(getActivity());
        icAccept.setImageResource(R.drawable.ic_accept);
        icAccept.setPadding(App.getPixelsInDP(5), App.getPixelsInDP(5), App.getPixelsInDP(20), App.getPixelsInDP(5));
        icAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextStep();
            }
        });

        MenuItem menuQuit = menu.getItem(0);
        menuQuit.setActionView(icQuit);
        menuQuit.getActionView().setAlpha(0f);

        MenuItem menuAccept = menu.getItem(1);
        menuAccept.setActionView(icAccept);
        menuAccept.getActionView().setAlpha(0f);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
    }

    private void nextStep() {

        // verifica se o gps está ativo
        if (location == null) {
            locationService.requestPermissionGPS();
            Log.v(getClass().getName(), "Localizacao invalida");
            return;
        }

        String code = edCode.getText().toString();

        if (code.isEmpty()) {
            edCode.setError("Informe o código do cliente");
            edCode.requestFocus();
            return;
        }

        try {
            // código do cliente
            float id = App.parseCodeCliente(Long.parseLong(code));

            // busca o cliente na base de dados local
            Cliente cliente = null;

            if (id % 1 == 0)
                cliente = ClienteDAO.getInstance(getActivity()).buscar((long) id);

            // se o cliente existe, passe para a próxima tela, se não notifique o usuário
            if (cliente != null) {

                final InspecaoWrapper inspecaoWrapper = new InspecaoWrapper(cliente.getId(), cliente.getNome(), location.getLatitude(), location.getLongitude(), cliente.hasCoords());
                int distancia = Geo.getDistancia(location.getLatitude(), location.getLongitude(), cliente.getLatitude(), cliente.getLongitude());

                if (cliente.hasCoords()) {

                    // verifica se o funcionário está dentro do raio permitido para realizar a inspeção
                    if (distancia > Session.withContext(getContext()).getAuthencationUser().getMinRadius())
                        AppDialog.showDialog(getActivity(), null, "Você está " + distancia + " metros do endereço deste cliente, para realizar a inspeção é necessário que você esteja no endereço deste cliente.", "Entendi", null, null, null);
                    else
                        checkinOk(inspecaoWrapper);

                } else
                    checkinOk(inspecaoWrapper);

            } else {
                edCode.setError("Código inválido");
                edCode.requestFocus();
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void checkinOk(InspecaoWrapper inspecaoWrapper) {
        App.closeKeyboard((DashboardActivity) getActivity());
        ((DashboardActivity) (getActivity())).loadInspecionarCheckinFragment(inspecaoWrapper, false);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        //Log.v(getClass().getName(), "Pegou localizacao");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        this.locationService.getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {

        if (locationService.getLocationManager() != null) {
            locationService.getLocationManager().removeUpdates(this);
            this.location = null;
        }
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
