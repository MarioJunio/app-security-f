package br.com.security.func.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public enum ConnectionType {
        NOT_CONNECTED, CONNECTED, WIFI, MOBILE
    }

    public static ConnectionType getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected ? ConnectionType.CONNECTED : ConnectionType.NOT_CONNECTED;
    }
}