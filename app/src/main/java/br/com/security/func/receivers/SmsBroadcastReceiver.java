package br.com.security.func.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Created by mariomartins on 11/09/17.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private String providerNumber;
    private String textSmsFilter;
    private SmsListener smsListener;

    public SmsBroadcastReceiver(String providerNumber, String textSmsFilter) {
        this.providerNumber = providerNumber;
        this.textSmsFilter = textSmsFilter;
    }

    public void setSmsListener(SmsListener smsListener) {
        this.smsListener = smsListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            String smsSender = "";
            String smsBody = "";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }

            } else {

                Bundle smsBundle = intent.getExtras();

                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");

                    if (pdus == null)
                        return;

                    SmsMessage[] messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }

                    smsSender = messages[0].getOriginatingAddress();
                }

            }

            // checa se o SMS recebido se inicia com o texto 'Security code: 000000'
            if (smsBody.trim().startsWith(textSmsFilter)) {

                if (smsListener != null) {
                    smsListener.onSmsReceived(smsBody.split(":")[1]);
                }
            }
        }

    }

    public interface SmsListener {
        void onSmsReceived(String text);
    }
}
