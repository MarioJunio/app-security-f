package br.com.security.func.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import br.com.security.func.R;
import br.com.security.func.config.App;
import br.com.security.func.config.Permissions;
import br.com.security.func.dialogs.AppDialog;
import br.com.security.func.dialogs.ConfirmExitDialog;
import br.com.security.func.net.UserService;
import br.com.security.func.receivers.SmsBroadcastReceiver;

public class RecoverPasswdActivity extends AppCompatActivity {

    private SmsBroadcastReceiver smsBroadcastReceiver;
    private Step1 step1;
    private Step2 step2;
    private Step3 step3;
    private User user;
    private CountDownTimer timer;
    private int step;
    private int attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.removeActionBarShadow(this);
        setContentView(R.layout.recover_passwd_activity);

        // inicia widgets
        init();

        // inicia receivers
        registerReceivers();

        // controla o step atual
        step = 1;
    }

    private void registerReceivers() {

        smsBroadcastReceiver = new SmsBroadcastReceiver(null, "Security app:");
        smsBroadcastReceiver.setSmsListener(new SmsBroadcastReceiver.SmsListener() {

            @Override
            public void onSmsReceived(String text) {

                if (user == null || user.getVerificationCode() == null)
                    return;

                if (text != null && !TextUtils.isEmpty(text) && user.getVerificationCode().equals(text.trim())) {
                    timer.cancel();
                    initStep3();
                }

            }
        });

        // registra receiver para ação de SMS
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

    }

    @Override
    protected void onStop() {
        super.onStop();

        try {

            if (smsBroadcastReceiver != null) {
                unregisterReceiver(smsBroadcastReceiver);
                smsBroadcastReceiver = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setStep1();
        setStep2();
        setStep3();

        step2.mainView.setVisibility(View.GONE);
        step3.mainView.setVisibility(View.GONE);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Botão voltar
            case android.R.id.home: {

                if (step >= 2) {
                    ConfirmExitDialog.show(this, timer);
                } else {
                    finish();
                }

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (step >= 2) {
            ConfirmExitDialog.show(this, timer);
        } else {
            finish();
        }

    }

    private void setStep1() {
        step1 = new Step1();

        step1.mainView = findViewById(R.id.step1);
        step1.edUsername = (EditText) findViewById(R.id.username);
        step1.txPhoneConf = (TextView) findViewById(R.id.txPhoneConf);
        step1.btnEditUser = (ImageButton) findViewById(R.id.btn_edit_user);
        step1.btnSearchUser = (Button) findViewById(R.id.btn_search_user);
        step1.btnConfirm = (Button) findViewById(R.id.btn_confirm);
        step1.layoutSearchUser = findViewById(R.id.layout_search_user);
        step1.layoutProgress = findViewById(R.id.layout_progress);
        step1.layoutConfirmUser = findViewById(R.id.layout_confirm_user);

        // busca usuário
        step1.btnSearchUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String usuario = step1.edUsername.getText().toString();

                // valida se o usuário foi informado corretamente
                if (usuario.trim().isEmpty()) {
                    step1.edUsername.setError("Informe o usuário corretamente!");
                    return;
                }

                step1.layoutSearchUser.setVisibility(View.GONE);
                step1.layoutProgress.setVisibility(View.VISIBLE);
                step1.edUsername.setEnabled(false);


                UserService.getTelefone(getApplicationContext(), usuario, new Response.Listener() {

                    @Override
                    public void onResponse(Object response) {

                        step1.layoutConfirmUser.setVisibility(View.VISIBLE);
                        step1.btnEditUser.setVisibility(View.VISIBLE);

                        // obtem usuário encontrado
                        user = new User(usuario, response.toString());

                        step1.txPhoneConf.setText(user.getPhone());
                        step1.layoutProgress.setVisibility(View.GONE);

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        step1.layoutProgress.setVisibility(View.GONE);
                        step1.layoutSearchUser.setVisibility(View.VISIBLE);
                        step1.edUsername.setEnabled(true);
                        step1.edUsername.setError("Usuário não encontrado");
                        step1.edUsername.requestFocus();
                    }
                });

            }
        });

        // troca o usuário
        step1.btnEditUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                step1.edUsername.setEnabled(true);
                step1.edUsername.getText().clear();
                step1.btnEditUser.setVisibility(View.GONE);
                step1.layoutSearchUser.setVisibility(View.VISIBLE);
                step1.layoutProgress.setVisibility(View.GONE);
                step1.layoutConfirmUser.setVisibility(View.GONE);
            }
        });

        // Confirma o usuário
        step1.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final ProgressDialog progress = ProgressDialog.show(RecoverPasswdActivity.this, "Aguarde",
                        "Autorizando dispositivo....", true);
                progress.show();

                UserService.getVerificationCode(getApplicationContext(), user.getUsername(), new Response.Listener() {

                    @Override
                    public void onResponse(Object response) {
                        progress.dismiss();

                        Log.v(getClass().getName(), response.toString());

                        user.setVerificationCode((String) response);

                        if (App.canAskPermission() && !App.hasSMSPermissions(RecoverPasswdActivity.this)) {
                            requestPermissions(Permissions.SMS_PERMISSIONS, Permissions.SMS_PERMISSION_RESULT);
                        } else
                            initStep2();

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(RecoverPasswdActivity.this, "Não foi possível obter o código de verificação", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void setStep2() {
        step2 = new Step2();

        step2.mainView = findViewById(R.id.step2);
        step2.txUsername = (TextView) findViewById(R.id.txUsername);
        step2.txPhone = (TextView) findViewById(R.id.txPhone);
        step2.fieldConfirmationCode = (EditText) findViewById(R.id.fieldCodeConfirm);
        step2.txTiming = (TextView) findViewById(R.id.txTiming);
        step2.progressTiming = (ProgressBar) findViewById(R.id.progress);
    }

    private void setStep3() {
        step3 = new Step3();

        step3.mainView = findViewById(R.id.step3);
        step3.fieldNewPasswd = (EditText) findViewById(R.id.fieldNewPasswd);
        step3.fieldNewPasswdConf = (EditText) findViewById(R.id.fieldNewPasswdConfirm);
        step3.btnConfirm = (Button) findViewById(R.id.btnConfirmRecover);

        // ao clicar no botão alterar
        step3.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String novaSenha = step3.fieldNewPasswd.getText().toString();
                String novaSenhaConf = step3.fieldNewPasswdConf.getText().toString();

                step3.fieldNewPasswd.setError(null);
                step3.fieldNewPasswdConf.setError(null);

                // valida se as senhas correspondem
                if (!novaSenha.equals(novaSenhaConf)) {
                    step3.fieldNewPasswdConf.setError("As senhas não correspondem");
                } else if (novaSenha.length() <= 3) {
                    step3.fieldNewPasswd.setError("A senha deve conter pelo menos 4 caracteres");
                } else {

                    UserService.alterarSenha(getApplicationContext(), user.getUsername(), user.verificationCode, novaSenha, new Response.Listener() {

                        @Override
                        public void onResponse(Object response) {

                            AppDialog.showDialog(RecoverPasswdActivity.this, null, "Senha foi alterada com sucesso", "OK", null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }

                            }, null);

                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            String message = error.networkResponse.statusCode == 404 ? "Solicitação para alterar senha não encontrada" : "Não foi possível alterar a senha no momento, contate o suporte imediatamente";
                            AppDialog.showDialog(RecoverPasswdActivity.this, null, message, "OK", null, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }

                            }, null);

                        }
                    });

                }

            }
        });
    }

    private void initStep2() {
        step = 2;

        step1.mainView.setVisibility(View.GONE);
        step2.mainView.setVisibility(View.VISIBLE);
        step3.mainView.setVisibility(View.GONE);

        // inicia step2
        step2.txUsername.setText(user.getUsername());
        step2.txPhone.setText(user.getPhone());

        // instancia temporizador
        timer = new CountDownTimer((1000 * 60 * 5), 1000) {

            @Override
            public void onTick(long l) {

                long minutes = (l / 1000) / 60;
                long seconds = (l / 1000) - (minutes * 60);

                step2.txTiming.setText(String.format("%s:%s", minutes, seconds < 10 ? "0" + seconds : seconds));
                step2.progressTiming.incrementProgressBy(1);
            }

            @Override
            public void onFinish() {

                AppDialog.showDialog(RecoverPasswdActivity.this, null, "O código de verificação não foi identificado, a operação atual será finalizada", "OK", null, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RecoverPasswdActivity.this.finish();
                    }

                }, null);

            }
        };

        // ao digitar o código de verificação
        step2.fieldConfirmationCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                // valida o código digitado pelo usuário
                if (editable.toString().equals(user.getVerificationCode())) {
                    timer.cancel();
                    App.closeKeyboard(RecoverPasswdActivity.this);
                    initStep3();
                } else if (editable.toString().length() >= 6) {
                    attempts++;

                    if (attempts >= 3) {

                        step2.fieldConfirmationCode.setEnabled(false);
                        AppDialog.showDialog(RecoverPasswdActivity.this, null, "Você informou o código de verificação incorreto 3 vezes", "OK", null, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RecoverPasswdActivity.this.finish();
                            }

                        }, null);

                    } else
                        Toast.makeText(RecoverPasswdActivity.this, "O código informado está incorreto, restam " + (3 - attempts) + " tentativas", Toast.LENGTH_SHORT).show();

                    editable.clear();
                }

            }
        });

        // inicia temporizador
        timer.start();
    }

    private void initStep3() {
        step = 3;

        step1.mainView.setVisibility(View.GONE);
        step2.mainView.setVisibility(View.GONE);
        step3.mainView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Permissions.SMS_PERMISSION_RESULT:

                if (App.hasSMSPermissions(this))
                    initStep3();

                break;
        }

    }

    /**
     * Step1
     */
    private class Step1 {
        View mainView;

        // widgets
        EditText edUsername;
        TextView txPhoneConf;
        ImageButton btnEditUser;
        Button btnSearchUser;
        Button btnConfirm;

        // Layout controlados por ações
        View layoutSearchUser;
        View layoutProgress;
        View layoutConfirmUser;
    }

    /**
     * Step 2
     */
    private class Step2 {

        View mainView;

        // widgets
        TextView txUsername;
        TextView txPhone;
        EditText fieldConfirmationCode;
        TextView txTiming;
        ProgressBar progressTiming;
    }

    /**
     * Step3
     */
    private class Step3 {

        View mainView;
        EditText fieldNewPasswd;
        EditText fieldNewPasswdConf;
        Button btnConfirm;
    }

    private class User {

        private String username;
        private String phone;
        private String verificationCode;

        public User(String username, String phone) {
            this.username = username;
            this.phone = phone;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }
    }
}
