package br.com.security.func.net;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

public class NetService {

    static RetryPolicy retryPolicy() {
        return new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}
