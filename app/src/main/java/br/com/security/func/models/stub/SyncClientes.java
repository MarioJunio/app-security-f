package br.com.security.func.models.stub;


import java.util.ArrayList;
import java.util.List;

import br.com.security.func.models.orm.Cliente;

public class SyncClientes {

    private Long dataSync;
    private List<Cliente> clientes = new ArrayList<>();

    public SyncClientes(Long dataSync, List<Cliente> collection) {
        super();
        this.dataSync = dataSync;
        this.clientes = collection;
    }

    public Long getDataSync() {
        return dataSync;
    }

    public void setDataSync(Long dataSync) {
        this.dataSync = dataSync;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

}
