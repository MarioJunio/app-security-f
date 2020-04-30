package br.com.security.func.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by mariomartins on 23/09/17.
 */

public class AppDialog {

    public static void showDialog(Activity context, String title, String message, String positiveButton, String negativeButton, DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCallback) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveCallback)
                .setNegativeButton(negativeButton, negativeCallback)
                .setCancelable(false)
                .show();
    }

}
