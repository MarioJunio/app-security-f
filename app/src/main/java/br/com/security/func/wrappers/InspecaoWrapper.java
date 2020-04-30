package br.com.security.func.wrappers;

import java.io.Serializable;

/**
 * Created by mariomartins on 14/09/17.
 */

public class InspecaoWrapper implements Serializable {

    private Long clienteId;
    private String nome;
    private double latitude;
    private double longitude;
    private boolean visitado;

    public InspecaoWrapper() {
    }

    public InspecaoWrapper(Long clienteId, String nome, double latitude, double longitude, boolean visitado) {
        this.clienteId = clienteId;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visitado = visitado;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

    @Override
    public String toString() {
        return "InspecaoWrapper{" +
                "clienteId=" + clienteId +
                ", nome='" + nome + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", visitado=" + visitado +
                '}';
    }
}
