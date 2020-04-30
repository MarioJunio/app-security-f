package br.com.security.func.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by mariomartins on 24/09/17.
 */

public class MyApplication extends Application {

    public static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
