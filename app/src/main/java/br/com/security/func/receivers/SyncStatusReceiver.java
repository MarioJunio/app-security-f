package br.com.security.func.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.com.security.func.utils.Constants;

/**
 * Created by mariomartins on 15/09/17.
 */

public class SyncStatusReceiver extends BroadcastReceiver {

    private SyncStatusListener syncStatusListener;

    public void setSyncStatusListener(SyncStatusListener syncStatusListener) {
        this.syncStatusListener = syncStatusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Constants.BROADCAST_SYNC_SERVICE_ALL)) {
            syncStatusListener.onStatusChange(intent.getExtras().get(Constants.SYNC_SERVICE_DATA_STATUS));
        }

    }

    public interface SyncStatusListener {
        void onStatusChange(Object status);
    }
}
