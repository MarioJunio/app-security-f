package br.com.security.func.location;

import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import br.com.security.func.config.Permissions;
import br.com.security.func.dialogs.AppDialog;

/**
 * Created by mariomartins on 19/09/17.
 */

public class LocationService {

    private static LocationService instance;
    private Activity activity;

    private LocationManager locationManager;
    private LocationListener locationListener;
    public boolean isGPS = false;
    public boolean canGetLocation = true;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;

    public static LocationService getInstance(Activity activity, LocationListener locationListener) {
        instance = new LocationService(activity, locationListener);
        return instance;
    }

    private LocationService(Activity activity, LocationListener locationListener) {
        this.activity = activity;
        this.locationListener = locationListener;
        init();
    }

    public void init() {

        // gerenciador de localização
        locationManager = (LocationManager) activity.getSystemService(Service.LOCATION_SERVICE);

        if (locationListener != null)
            requestPermissionGPS();

    }

    public void requestPermissionGPS() {

        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPS)
            showSettingsAlert();

        // se for Android M para cima, requisite as permissões ao usuário
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, Permissions.GPS_PERMISSIONS, Permissions.GPS_PERMISSION_RESULT);
            canGetLocation = false;
        } else
            canGetLocation = true;

        // inicializa serviço de localização
        getLocation();

    }

    public void showSettingsAlert() {
        AppDialog.showDialog(activity, null, "Para continuar, permita que o dispositivo ative a localização utilizando o GPS.", "Ativar", null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(intent, 1);
            }
        }, null);
    }

    public void getLocation() {

        try {

            if (canGetLocation) {

                if (isGPS && locationListener != null)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public boolean isGPS() {
        return isGPS;
    }

    public void setGPS(boolean GPS) {
        isGPS = GPS;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    public void setCanGetLocation(boolean canGetLocation) {
        this.canGetLocation = canGetLocation;
    }
}
