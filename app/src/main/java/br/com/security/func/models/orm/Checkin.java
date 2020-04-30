package br.com.security.func.models.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by mariomartins on 14/09/17.
 */

@DatabaseTable
public class Checkin {

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private Long id;

    @DatabaseField(uniqueCombo = true, columnName = "funcionario_id")
    private Long funcionarioId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
    private Cliente cliente;

    @DatabaseField(uniqueCombo = true, dataType = DataType.DATE_STRING, format = "dd/MM/yyyy HH:mm:ss")
    private Date data;

    @DatabaseField(dataType = DataType.DOUBLE)
    private double latitude;

    @DatabaseField(dataType = DataType.DOUBLE)
    private double longitude;

    @DatabaseField
    private String status;

    @DatabaseField
    private String descricao;

    @DatabaseField
    private boolean sync;

    @DatabaseField
    private String foto;

    public Checkin() {
    }

    public Checkin(Long funcionarioId, Cliente cliente, Date data, double latitude, double longitude, String status, String descricao, boolean sync, String foto) {
        this.funcionarioId = funcionarioId;
        this.cliente = cliente;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.descricao = descricao;
        this.sync = sync;
        this.foto = foto;
    }

    public Checkin(Long id, Long funcionarioId, Cliente cliente, Date data, double latitude, double longitude, String status, String descricao, boolean sync, String foto) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.cliente = cliente;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.descricao = descricao;
        this.sync = sync;
        this.foto = foto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Checkin{" +
                "id=" + id +
                ", funcionarioId=" + funcionarioId +
//                ", clienteId=" + clienteId +
                ", cliente=" + cliente +
                ", data=" + data +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status='" + status + '\'' +
                ", descricao='" + descricao + '\'' +
                ", sync=" + sync +
                ", foto=" + foto +
                '}';
    }
}
