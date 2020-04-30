package br.com.security.func.config;

import android.Manifest;

/**
 * Created by mariomartins on 27/09/17.
 */

public class Permissions {

    public final static int GPS_PERMISSION_RESULT = 101;
    public final static int CAMERA_PERMISSION_RESULT = 102;
    public final static int SMS_PERMISSION_RESULT = 103;

    public static final String GPS_PERMISSIONS[] = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    public static final String CAMERA_PERMISSIONS[] = {Manifest.permission.CAMERA};
    public static final String SMS_PERMISSIONS[] = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS};

}
