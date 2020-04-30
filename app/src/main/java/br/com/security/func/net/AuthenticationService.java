package br.com.security.func.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import br.com.security.func.config.App;
import br.com.security.func.config.Session;

/**
 * Created by mariomartins on 12/09/17.
 */

public class AuthenticationService {

    public static final String TAG = "Authentication";
    public static final String URL_AUTH = "/auth";
    public static final String URL_LOGOUT = "/logoff";

    public static void doAuth(final Context context, final String username, final String password, Response.Listener listener, Response.ErrorListener errorListener) throws AuthFailureError {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(URL_AUTH), listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return builtAuthHeaders(username, password);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String cookies = response.headers.get("Set-Cookie");

                if (cookies != null && !cookies.trim().isEmpty()) {

                    String[] tokenCookies = cookies.split(";");
                    String jsessionid = tokenCookies[0];

                    if (!TextUtils.isEmpty(jsessionid))
                        Session.withContext(context).saveAuthToken(jsessionid.trim());
                }

                return super.parseNetworkResponse(response);
            }
        };

        request.setTag(TAG);

        queue.add(request);
    }

    public static void doAuth(Context context, final String token, Response.Listener listener, Response.ErrorListener errorListener) throws AuthFailureError {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(URL_AUTH), listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return builtAuthHeaders(token);
            }
        };

        request.setTag(TAG);

        queue.add(request);
    }

    public static void doLogout(Context context, Response.Listener listener, Response.ErrorListener errorListener) throws AuthFailureError {

        final String token = Session.withContext(context).getAuthencationUser().getAuthToken();

        Log.v(context.getClass().getName(), "Token: " + token);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, App.HOST.concat(URL_LOGOUT), listener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return builtAuthHeaders(token);
            }
        };

        request.setTag(TAG);

        queue.add(request);
    }

    public static Map<String, String> builtAuthHeaders(String username, String password) {
        Map<String, String> headers = new HashMap<>();

        String credentials = String.format("%s:%s", username, password);
        String auth = String.format("Basic %s", Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT));

        headers.put("Authorization", auth);

        return headers;
    }

    public static Map<String, String> builtAuthHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", token);
        return headers;
    }

}
