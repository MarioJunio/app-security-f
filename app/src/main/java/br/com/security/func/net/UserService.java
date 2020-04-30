package br.com.security.func.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import br.com.security.func.config.App;

/**
 * Created by mariomartins on 13/09/17.
 */

public class UserService {

    public static final String TAG = "UserService";
    public static final String GET_TELEFONE_URL = "/recovery/empregado/get/";
    public static final String GET_CODE_URL = "/recovery/empregado/get-code";
    public static final String SALVAR_NOVA_SENHA_URL = "/recovery/empregado/salvar-nova-senha";

    public static void getTelefone(Context context, String usuario, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, App.HOST.concat(GET_TELEFONE_URL).concat(String.format("/%s", usuario)), listener, errorListener);
        request.setTag(TAG);

        queue.add(request);
    }

    public static void getVerificationCode(Context context, final String usuario, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(GET_CODE_URL), listener, errorListener) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);

                return params;
            }
        };

        request.setTag(TAG);

        queue.add(request);

    }

    public static void alterarSenha(Context context, final String usuario, final String code, final String novaSenha, Response.Listener listener, Response.ErrorListener errorListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(SALVAR_NOVA_SENHA_URL), listener, errorListener) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                params.put("code", code);
                params.put("novaSenha", novaSenha);

                return params;
            }
        };

        request.setTag(TAG);

        queue.add(request);

    }
}
