package br.com.security.func.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.sql.SQLException;

import br.com.security.func.R;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.config.Permissions;
import br.com.security.func.config.Session;
import br.com.security.func.dao.ClienteDAO;
import br.com.security.func.dialogs.AppDialog;
import br.com.security.func.location.LocationService;
import br.com.security.func.models.orm.Cliente;
import br.com.security.func.utils.Geo;
import br.com.security.func.wrappers.InspecaoWrapper;

public class InspecionarFragment extends Fragment implements LocationListener, QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private DashboardActivity dashboardActivity;
    private CountDownTimer timer;
    private LocationService locationService;
    private Location location;
    private boolean canUseCamera;

    public static InspecionarFragment newInstance() {
        InspecionarFragment fragment = new InspecionarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DashboardActivity) getActivity()).navigation.setSelectedItemId(R.id.nav_inspection);
        setHasOptionsMenu(true);

        // activity do dashboard responsavel por controlar os fragmentos
        dashboardActivity = (DashboardActivity) getActivity();

        timer = new CountDownTimer(15000, 15000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                qrCodeReaderView.stopCamera();
                dashboardActivity.loadInspecionarManualFragment();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inspecionar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (canUseCamera = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            initQRCodeReaderView(view);
        else
            requestCameraPermission();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.inspecao_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.nav_dashboard:
                ((DashboardActivity) getActivity()).loadDashboardFragment(true);
                return false;
        }

        return false;

    }

    private void requestCameraPermission() {

        AppDialog.showDialog(getActivity(), null, "Para realizar a inspeção é fundamental que você permita o uso da camera", "Ok", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(getActivity(), Permissions.CAMERA_PERMISSIONS, Permissions.CAMERA_PERMISSION_RESULT);
            }

        }, null);
    }


    private void initQRCodeReaderView(View view) {

        qrCodeReaderView = view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setVisibility(View.VISIBLE);

        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(1000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (canUseCamera) {
            qrCodeReaderView.startCamera();
            timer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (canUseCamera) {
            qrCodeReaderView.stopCamera();
            timer.cancel();
        }

    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        if (location == null) {
            locationService.requestPermissionGPS();
        } else {

            if (!text.trim().isEmpty()) {

                try {
                    // busca o cliente por hash
                    Cliente cliente = ClienteDAO.getInstance(getContext()).buscar(text);

                    // se o cliente for encontrado então passe para a próxima tela, se não notifique o usuário
                    if (cliente != null) {
                        timer.cancel();

                        final InspecaoWrapper inspecaoWrapper = new InspecaoWrapper(cliente.getId(), cliente.getNome(), location.getLatitude(), location.getLongitude(), cliente.hasCoords());
                        int distancia = Geo.getDistancia(location.getLatitude(), location.getLongitude(), cliente.getLatitude(), cliente.getLongitude());

                        if (cliente.hasCoords()) {

                            // verifica se o funcionário está dentro do raio permitido para realizar a inspeção
                            if (distancia > Session.withContext(getContext()).getAuthencationUser().getMinRadius()) {
                                AppDialog.showDialog(getActivity(), null, "Você está " + distancia + " metros do endereço deste cliente, para realizar a inspeção é fundamental que você esteja no endereço deste cliente.", "Entendi", null,

                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                timer.start();
                                            }

                                        }, null);
                            } else
                                dashboardActivity.loadInspecionarCheckinFragment(inspecaoWrapper, true);

                        } else
                            dashboardActivity.loadInspecionarCheckinFragment(inspecaoWrapper, true);

                    } else {
                        Toast.makeText(getContext(), "O QRCode é inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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

    public void allowCamera(boolean canUseCamera) {
        this.canUseCamera = canUseCamera;
        initQRCodeReaderView(getView());
        onResume();
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
}
