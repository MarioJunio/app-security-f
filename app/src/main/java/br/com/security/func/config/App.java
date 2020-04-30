package br.com.security.func.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.security.func.application.MyApplication;
import br.com.security.func.service.SyncService;

/**
 * Created by mariomartins on 12/09/17.
 */

public class App {

    //    public static final String HOST = "http://18.228.31.137:9090";
    public static final String HOST = "http://datasecurity.ddns.net:22016";
//    public static final String HOST = "http://192.168.100.6:22016";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static void closeKeyboard(AppCompatActivity activity) {

        View view = activity.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public static void removeActionBarShadow(AppCompatActivity activity) {
        activity.getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        activity.getSupportActionBar().setElevation(0);
    }

    public static void turnOnFlashLight(boolean on) {

        Camera mCam = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters p = mCam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCam.setParameters(p);
        mCam.startPreview();
    }

    public static void requestSyncService(Context context, final String syncType) {
        Intent mServiceIntent = new Intent(context, SyncService.class);
        Bundle bundle = new Bundle();
        bundle.putString(SyncService.ARG_SYNC_TYPE, syncType);
        mServiceIntent.putExtras(bundle);
        context.startService(mServiceIntent);
    }

    public static float parseCodeCliente(Long id) {
        return ((float) id - 1050f) / 3;
    }

    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public static Date parseDate(String dateFormatted) throws ParseException {
        return dateFormat.parse(dateFormatted);
    }

    public static int getPixelsInDP(int dps) {
        final float scale = MyApplication.context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static boolean hasInternetConnection(Activity activity) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
    }

    public static boolean hasPermission(Activity activity, String permission) {
        return canAskPermission() && activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasSMSPermissions(Activity activity) {

        boolean ok = true;

        for (String perm : Permissions.SMS_PERMISSIONS) {

            if (!hasPermission(activity, perm)) {
                ok = false;
                break;
            }
        }

        return ok;
    }

    public static boolean canAskPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }
}
