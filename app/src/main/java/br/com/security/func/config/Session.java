package br.com.security.func.config;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.security.func.application.MyApplication;
import br.com.security.func.models.stub.User;

/**
 * Created by mariomartins on 12/09/17.
 */

public class Session {

    public static final String KEY = "user_preferences";

    private SharedPreferences sharedPreferences;

    public static Session withContext(Context c) {
        return new Session();
    }

    public User getAuthencationUser() {

        createSharedPreferences();

        User user = new User();
        user.setId(sharedPreferences.getLong(User.ID_FIELD, 0l));
        user.setLogin(sharedPreferences.getString(User.LOGIN_FIELD, null));
        user.setTelefone(sharedPreferences.getString(User.TELEFONE_FIELD, null));
        user.setAuthToken(sharedPreferences.getString(User.AUTH_TOKEN_FIELD, null));
        user.setAtivo(sharedPreferences.getBoolean(User.ATIVO_FIELD, false));
        user.setSyncDataClientesDown(sharedPreferences.getLong(User.SYNC_DATA_CLIENTES_DOWN, 0l));
        user.setSyncDataCheckinsUp(sharedPreferences.getLong(User.SYNC_DATA_CHECKINS_UP, 0l));
        user.setMinRadius(sharedPreferences.getFloat(User.PREF_MIN_RADIUS, 50f));

        return user;
    }

    public long getSyncDateCheckinDown(Long id) {
        createSharedPreferences();
        return sharedPreferences.getLong(String.valueOf(id), 0l);
    }

    public void saveUser(User user) {
        createSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(User.ID_FIELD, user.getId() == null ? 0l : user.getId());
        editor.putString(User.LOGIN_FIELD, user.getLogin());
        editor.putString(User.TELEFONE_FIELD, user.getTelefone());
        editor.putBoolean(User.ATIVO_FIELD, user.isAtivo());
        editor.putFloat(User.PREF_MIN_RADIUS, (float) user.getMinRadius());
        editor.commit();
    }

    public void saveAuthToken(String token) {
        createSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.AUTH_TOKEN_FIELD, token);
        editor.commit();
    }

    public void saveSyncDateClientesDown(Long date) {
        createSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(User.SYNC_DATA_CLIENTES_DOWN, date);
        editor.commit();
    }

    public void saveSyncDateCheckinsDown(Long id, Long date) {
        createSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(String.valueOf(id), date);
        editor.commit();
    }

    public void saveSyncDateCheckinsUp(Long date) {
        createSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(User.SYNC_DATA_CHECKINS_UP, date);
        editor.commit();
    }

    public void savePrefMinRadius(float minRadius) {
        createSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(User.PREF_MIN_RADIUS, minRadius);
        editor.commit();
    }

    public void clearSession() {
        createSharedPreferences();
        sharedPreferences.edit().clear().commit();
    }

    private void createSharedPreferences() {
        this.sharedPreferences = MyApplication.context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }


}
