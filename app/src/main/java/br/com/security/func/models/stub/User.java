package br.com.security.func.models.stub;

/**
 * Created by mariomartins on 12/09/17.
 */

public class User {

    public static final String ID_FIELD = "id";
    public static final String LOGIN_FIELD = "login";
    public static final String TELEFONE_FIELD = "telefone";
    public static final String ATIVO_FIELD = "ativo";
    public static final String AUTH_TOKEN_FIELD = "auth_token";
    public static final String SYNC_DATA_CLIENTES_DOWN = "sync_data_clientes_down";
    public static final String SYNC_DATA_CHECKINS_UP = "sync_data_checkins_up";
    public static final String PREF_MIN_RADIUS = "min_radius";

    private Long id;
    private String login;
    private String telefone;
    private boolean ativo;
    private String authToken;
    private Long syncDataClientesDown;
    private Long syncDataCheckinsUp;
    private float minRadius;

    public User() {
    }

    public User(String authToken) {
        this.authToken = authToken;
    }

    public User(Long id, String login, String telefone) {
        this.id = id;
        this.login = login;
        this.telefone = telefone;
    }

    public User(Long id, String login, String telefone, String authToken) {
        this.id = id;
        this.login = login;
        this.telefone = telefone;
        this.authToken = authToken;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Long getSyncDataClientesDown() {
        return syncDataClientesDown;
    }

    public void setSyncDataClientesDown(Long syncDataClientesDown) {
        this.syncDataClientesDown = syncDataClientesDown;
    }

    public Long getSyncDataCheckinsUp() {
        return syncDataCheckinsUp;
    }

    public void setSyncDataCheckinsUp(Long syncDataCheckinsUp) {
        this.syncDataCheckinsUp = syncDataCheckinsUp;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", telefone='" + telefone + '\'' +
                ", ativo=" + ativo +
                ", authToken='" + authToken + '\'' +
                ", syncDataClientesDown=" + syncDataClientesDown +
                ", syncDataCheckinsUp=" + syncDataCheckinsUp +
                ", minRadius=" + minRadius +
                '}';
    }
}
