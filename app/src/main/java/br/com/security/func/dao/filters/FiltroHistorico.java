package br.com.security.func.dao.filters;

import java.util.Date;

public class FiltroHistorico {

    private String cliente;
    private Date dataInicial, dataFinal;

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public boolean isNotEmpty() {
        return (cliente != null && !cliente.trim().isEmpty()) || dataInicial != null || dataFinal != null;
    }
}