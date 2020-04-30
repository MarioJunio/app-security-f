package br.com.security.func.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import br.com.security.func.config.App;
import br.com.security.func.service.SyncService;
import br.com.security.func.utils.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // verifica se a ação é do tipo alteração de conexão
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {

            final Timer timer = new Timer();

            final TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    boolean requested = false;

                    NetworkUtil.ConnectionType status = NetworkUtil.getConnectivityStatus(context);

                    Log.v(getClass().getName(), status.name());

                    // se existe conexão com a internet então tente sincronizar os dados
                    if (status == NetworkUtil.ConnectionType.CONNECTED && !requested) {
                        App.requestSyncService(context, SyncService.ARG_SYNC_BOTH);
                        Log.v(getClass().getName(), "Requisitando sincronização");
                    }
                }
            };

            timer.schedule(timerTask, 10000l);

        }
    }
}