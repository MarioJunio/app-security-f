package br.com.security.func.service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import br.com.security.func.activities.AuthActivity;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.activities.SplashScreenActivity;

/**
 * Created by mariomartins on 12/09/17.
 */

public class Router {

    public static void goAuthView(AppCompatActivity activity) {
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void goSplashScreenView(AppCompatActivity activity) {
        Intent intent = new Intent(activity, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void goDashboardView(AppCompatActivity activity) {
        Intent intent = new Intent(activity, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
