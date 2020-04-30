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
import java.util.List;
import java.util.Map;

import br.com.security.func.config.App;
import br.com.security.func.config.Session;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.stub.LocalizacaoCliente;

/**
 * Created by mariomartins on 15/09/17.
 */

public class ClienteService {

    public static final String TAG = "ClienteService";
    public static final String GET_CLIENTES = "/clientes";
    public static final String POST_CLIENTE_CHECKIN = "/clientes/sync-localizacoes";


    public static void getSyncClientes(final Context context, Long time, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, App.HOST.concat(GET_CLIENTES).concat(String.format("/%s", String.valueOf(time))), listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationService.builtAuthHeaders(Session.withContext(context).getAuthencationUser().getAuthToken());
            }
        };

        request.setTag(TAG);

        queue.add(request);
    }

    public static void postClientesLocalizacoes(final Context context, final List<LocalizacaoCliente> localizacaoClientes, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(POST_CLIENTE_CHECKIN), listener, errorListener) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthenticationService.builtAuthHeaders(Session.withContext(context).getAuthencationUser().getAuthToken());
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                try {

                    String json = new GsonBuilder().create().toJson(localizacaoClientes, new TypeToken<List<Checkin>>() {
                    }.getType());

                    return json.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    uee.printStackTrace();
                    return null;
                }
            }

        };

        request.setTag(TAG);
        queue.add(request);
    }


}
