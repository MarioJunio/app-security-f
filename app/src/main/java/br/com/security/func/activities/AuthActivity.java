package br.com.security.func.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.security.func.R;
import br.com.security.func.config.App;
import br.com.security.func.config.Session;
import br.com.security.func.models.stub.User;
import br.com.security.func.net.AuthenticationService;
import br.com.security.func.service.Router;

public class AuthActivity extends AppCompatActivity {

    // UI references.
    private EditText edUsername;
    private EditText edPasswd;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        App.removeActionBarShadow(this);
        setContentView(R.layout.auth_activity);

        // Set up the login form.
        edUsername = (EditText) findViewById(R.id.username);
        edPasswd = (EditText) findViewById(R.id.password);
        progress = findViewById(R.id.login_progress);

        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button btnForgotPasswd = (Button) findViewById(R.id.btn_forgot_passwd);
        btnForgotPasswd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(AuthActivity.this, RecoverPasswdActivity.class));
            }
        });

    }

    private void attemptLogin() {

        // Reset errors.
        edUsername.setError(null);
        edPasswd.setError(null);

        // Store values at the time of the login attempt.
        String username = edUsername.getText().toString();
        String password = edPasswd.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            edPasswd.setError(getString(R.string.error_invalid_password));
            focusView = edPasswd;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            edUsername.setError(getString(R.string.error_field_required));
            focusView = edUsername;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else
            doAuth(username, password);
    }

    private void doAuth(String username, String password) {

        if (!App.hasInternetConnection(this)) {
            Toast.makeText(getApplicationContext(), "Verifique sua conexão com a Internet, para realizar a autenticação", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            showProgress(true);
            AuthenticationService.doAuth(this, username, password, new Response.Listener() {

                @Override
                public void onResponse(Object response) {
                    showProgress(false);

                    User user = new Gson().fromJson((String) response, new TypeToken<User>() {
                    }.getType());

                    Session.withContext(getApplicationContext()).saveUser(user);
                    Router.goSplashScreenView(AuthActivity.this);
                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    showProgress(false);

                    edPasswd.setError(getApplicationContext().getResources().getString(R.string._401));
                    edPasswd.requestFocus();
                }
            });

        } catch (AuthFailureError authFailureError) {
            showProgress(false);
            Toast.makeText(getApplicationContext(), "Ocorreu um probleminha ao tentar se autenticar, contate o suporte imediatamente", Toast.LENGTH_LONG).show();
            authFailureError.printStackTrace();
        }

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progress.setVisibility(show ? View.VISIBLE : View.GONE);
            progress.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

        } else {
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}

