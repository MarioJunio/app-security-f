package br.com.security.func.dialogs;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mariomartins on 11/09/17.
 */

public class ConfirmExitDialog {

    public static void show(final AppCompatActivity activity, final Object extra) {

        new AlertDialog.Builder(activity)
                .setMessage("Você tem certeza que deseja cancelar essa operação?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (extra instanceof CountDownTimer) {
                            ((CountDownTimer) extra).cancel();
                        }

                        activity.finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();

    }

}

