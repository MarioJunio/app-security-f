package br.com.security.func.net;

import android.content.Context;
import android.content.res.Resources;

import br.com.security.func.R;

/**
 * Created by mariomartins on 12/09/17.
 */

public class HttpMessages {

    public static String getMessage(Context context, int statusCode) {

        String message = null;
        Resources resources = context.getResources();

        switch (statusCode) {

            case 401:
                message = resources.getString(R.string._401);
                break;

        }

        return message;
    }
}
