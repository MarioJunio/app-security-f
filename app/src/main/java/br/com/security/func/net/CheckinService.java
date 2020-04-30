package br.com.security.func.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.security.func.config.App;
import br.com.security.func.config.Session;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.orm.Cliente;
import br.com.security.func.utils.GsonClienteAdapter;
import br.com.security.func.utils.GsonUTCDateAdapter;

/**
 * Created by mariomartins on 15/09/17.
 */

public class CheckinService extends NetService {


    public static final String TAG = "CheckinService";
    public static final String GET_CHECKINS = "/checkins";
    public static final String POST_CHECKINS = "/checkins/sincronizar";

    public static void getSyncCheckins(final Context context, Long id, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, App.HOST.concat(GET_CHECKINS).concat(String.format("/%s", String.valueOf(id))).concat(String.format("/%s", String.valueOf(Session.withContext(context).getSyncDateCheckinDown(id)))), listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationService.builtAuthHeaders(Session.withContext(context).getAuthencationUser().getAuthToken());
            }
        };

        request.setRetryPolicy(retryPolicy());
        request.setTag(TAG);

        queue.add(request);
    }

    public static void postSyncCheckins(final Context context, final List<Checkin> checkins, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(POST_CHECKINS), listener, errorListener) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                try {

                    String json = new GsonBuilder()
                            .registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
                            .registerTypeAdapter(Cliente.class, new GsonClienteAdapter())
                            .create()
                            .toJson(checkins, new TypeToken<List<Checkin>>() {}.getType());

                    System.out.println(json);

                    return json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationService.builtAuthHeaders(Session.withContext(context).getAuthencationUser().getAuthToken());
            }

        };

        request.setRetryPolicy(retryPolicy());
        request.setTag(TAG);

        queue.add(request);

    }
}
