package br.com.security.func.activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.security.func.R;
import br.com.security.func.config.Permissions;
import br.com.security.func.config.Session;
import br.com.security.func.dialogs.AppDialog;
import br.com.security.func.fragments.ClientesFragment;
import br.com.security.func.fragments.DashboardFragment;
import br.com.security.func.fragments.HistoricoFragment;
import br.com.security.func.fragments.InspecionarCheckinFragment;
import br.com.security.func.fragments.InspecionarFragment;
import br.com.security.func.fragments.InspecionarManualFragment;
import br.com.security.func.location.LocationService;
import br.com.security.func.wrappers.InspecaoWrapper;

public class DashboardActivity extends AppCompatActivity {

    private LocationService locationService;
    private ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(Permissions.GPS_PERMISSIONS));
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private Location location;

    public BottomNavigationView navigation;
    private Toolbar toolbar;

    private Fragment currentFragment;
    private FRAGMENT_TAGS activeFragment;

    enum FRAGMENT_TAGS {
        DASHBOARD, INSPECAO, INSPECAO_MANUAL, INSPECAO_CHECKIN, CLIENTES, HISTORICO
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

                    if (activeFragment == FRAGMENT_TAGS.INSPECAO_CHECKIN) {

                        new AlertDialog.Builder(DashboardActivity.this)
                                .setMessage("Você realmente deseja cancelar a inspeção atual?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        navigationSelected(item);
                                    }

                                }).setNegativeButton("Não", null).setCancelable(false).show();

                        return false;
                    } else {
                        navigationSelected(item);
                        return true;
                    }

                }

            };

    private void navigationSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_inspection:
                item.setCheckable(true);
                loadInspecionarFragment();
                break;
            case R.id.nav_clientes:
                item.setCheckable(true);
                loadClientesFragment();
                break;
            case R.id.nav_history:
                item.setCheckable(true);
                loadHistoricoFragment();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        setActionBarUserProfile();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // limpa seleção de menu inicial
        clearNavSelected();

        if (savedInstanceState != null)
            return;

        if (findViewById(R.id.fragment_content) != null) {
            DashboardFragment fragment = DashboardFragment.newInstance();
            currentFragment = fragment;
            activeFragment = FRAGMENT_TAGS.DASHBOARD;

            locationService = LocationService.getInstance(DashboardActivity.this, fragment);
            fragment.setLocationService(locationService);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, fragment).commit();
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        loadDashboardFragment(false);
    }

    public void loadDashboardFragment(boolean ignoreDialog) {

        if (findViewById(R.id.fragment_content) != null && activeFragment != FRAGMENT_TAGS.DASHBOARD) {

            if ((activeFragment == FRAGMENT_TAGS.INSPECAO_CHECKIN || activeFragment == FRAGMENT_TAGS.INSPECAO_MANUAL) && !ignoreDialog) {

                AppDialog.showDialog(this, null, "Tem certeza que deseja cancelar essa inspeção?", "Sim", "Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DashboardFragment fragment = DashboardFragment.newInstance();
                        locationService = LocationService.getInstance(DashboardActivity.this, fragment);
                        fragment.setLocationService(locationService);

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment, FRAGMENT_TAGS.DASHBOARD.name()).commit();
                        activeFragment = FRAGMENT_TAGS.DASHBOARD;
                        navigation.getMenu().getItem(0).setCheckable(false);
                        navigation.getMenu().getItem(1).setCheckable(false);
                        navigation.getMenu().getItem(2).setCheckable(false);
                    }

                }, null);

            } else {
                DashboardFragment fragment = DashboardFragment.newInstance();
                locationService = LocationService.getInstance(DashboardActivity.this, fragment);
                fragment.setLocationService(locationService);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment, FRAGMENT_TAGS.DASHBOARD.name()).commit();
                currentFragment = fragment;
                activeFragment = FRAGMENT_TAGS.DASHBOARD;

                navigation.getMenu().getItem(0).setCheckable(false);
                navigation.getMenu().getItem(1).setCheckable(false);
                navigation.getMenu().getItem(2).setCheckable(false);
            }

        }

    }

    public void loadInspecionarFragment() {

        if (findViewById(R.id.fragment_content) != null && activeFragment != FRAGMENT_TAGS.INSPECAO) {
            InspecionarFragment fragment = InspecionarFragment.newInstance();

            locationService = LocationService.getInstance(DashboardActivity.this, fragment);
            fragment.setLocationService(locationService);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
            currentFragment = fragment;
            activeFragment = FRAGMENT_TAGS.INSPECAO;
        }

    }

    public void loadInspecionarManualFragment() {

        if (findViewById(R.id.fragment_content) != null && activeFragment != FRAGMENT_TAGS.INSPECAO_MANUAL) {
            InspecionarManualFragment fragment = InspecionarManualFragment.newInstance();

            locationService = LocationService.getInstance(DashboardActivity.this, fragment);
            fragment.setLocationService(locationService);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
            currentFragment = fragment;
            activeFragment = FRAGMENT_TAGS.INSPECAO_MANUAL;
        }

    }

    public void loadInspecionarCheckinFragment(InspecaoWrapper inspecaoWrapper, boolean qrcodeScanner) {

        if (findViewById(R.id.fragment_content) != null && activeFragment != FRAGMENT_TAGS.INSPECAO_CHECKIN) {
            InspecionarCheckinFragment fragment = InspecionarCheckinFragment.newInstance(inspecaoWrapper, qrcodeScanner);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
            currentFragment = fragment;
            activeFragment = FRAGMENT_TAGS.INSPECAO_CHECKIN;
        }

    }

    public void loadClientesFragment() {

        if (findViewById(R.id.fragment_content) != null && activeFragment != FRAGMENT_TAGS.CLIENTES) {
            ClientesFragment fragment = ClientesFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
            currentFragment = fragment;
            activeFragment = FRAGMENT_TAGS.CLIENTES;
        }

    }

    public void loadHistoricoFragment() {

        if (findViewById(R.id.fragment_content) != null && activeFragment != FRAGMENT_TAGS.HISTORICO) {
            HistoricoFragment fragment = HistoricoFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
            currentFragment = fragment;
            activeFragment = FRAGMENT_TAGS.HISTORICO;
        }

    }

    private void setActionBarUserProfile() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView txUsername = toolbar.findViewById(R.id.profile_username);
        txUsername.setText(Session.withContext(getApplicationContext()).getAuthencationUser().getLogin());

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    public void clearNavSelected() {

        if (navigation != null)
            navigation.getMenu().getItem(0).setCheckable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {

            case 0:
                locationService.requestPermissionGPS();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case Permissions.GPS_PERMISSION_RESULT:

                permissionsRejected.clear();
                for (String perms : permissionsToRequest) {

                    if (!hasPermission(perms))
                        permissionsRejected.add(perms);

                }

                if (!permissionsRejected.isEmpty()) {

                    if (canAskPermission()) {

                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {

                            AppDialog.showDialog(this, null, "Essas permissões são essenciais para o funcionamento da aplicação. Por favor permita o acesso", "Permitir", null, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (canAskPermission())
                                        requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), Permissions.GPS_PERMISSION_RESULT);

                                }

                            }, null);

                            return;
                        }
                    }

                } else {
                    locationService.setCanGetLocation(true);
                    locationService.getLocation();

                    // se estiver na tela de dashboard então atualize o mapa
                    if (currentFragment instanceof DashboardFragment) {
                        DashboardFragment dashboardFragment = (DashboardFragment) currentFragment;
                        dashboardFragment.onMapReady(dashboardFragment.getmMap());
                    }
                }

                break;

            case Permissions.CAMERA_PERMISSION_RESULT:

                int permIndexRejected = -1;

                for (int i = 0; i < Permissions.CAMERA_PERMISSIONS.length; i++) {

                    if (!hasPermission(Permissions.CAMERA_PERMISSIONS[i])) {
                        permIndexRejected = i;
                        break;
                    }

                }

                Log.v(getClass().getName(), permIndexRejected + "");

                if (permIndexRejected >= 0 && canAskPermission() && shouldShowRequestPermissionRationale(Permissions.CAMERA_PERMISSIONS[permIndexRejected]))
                    requestPermissions(Permissions.CAMERA_PERMISSIONS, Permissions.CAMERA_PERMISSION_RESULT);
                else if (currentFragment instanceof InspecionarFragment) {
                    InspecionarFragment fragment = (InspecionarFragment) currentFragment;
                    fragment.allowCamera(true);
                }

                break;
        }
    }

    private boolean hasPermission(String permission) {

        if (canAskPermission())
            return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

        return true;
    }

    private boolean canAskPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

}
