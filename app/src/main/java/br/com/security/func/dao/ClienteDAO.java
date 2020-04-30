package br.com.security.func.dao;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.security.func.config.DatabaseHelper;
import br.com.security.func.models.orm.Cliente;
import br.com.security.func.models.stub.LocalizacaoCliente;

/**
 * Created by mariomartins on 15/09/17.
 */

public class ClienteDAO {

    private Dao<Cliente, Long> clienteDAO;

    public static ClienteDAO getInstance(Context context) {
        return new ClienteDAO(new DatabaseHelper(context));
    }

    private ClienteDAO(DatabaseHelper databaseHelper) {
        this.clienteDAO = databaseHelper.getClienteDao();
    }

    public void save(Cliente cliente) throws SQLException {
        this.clienteDAO.createOrUpdate(cliente);
    }

    public Cliente buscar(Long id) throws SQLException {
        return this.clienteDAO.queryBuilder().where().eq("id", id).and().eq("excluido", false).queryForFirst();
    }

    /**
     * Busca o cliente por seu hash id
     *
     * @param text
     * @return
     * @throws SQLException
     */
    public Cliente buscar(String text) throws SQLException {
        return clienteDAO.queryBuilder().where().eq("hash_id", text).and().eq("excluido", false).queryForFirst();
    }

    /**
     * Busca todos os clientes que não foram excluídos
     *
     * @param nome
     * @return
     * @throws SQLException
     */
    public List<Cliente> todos(String nome) throws SQLException {
        QueryBuilder<Cliente, Long> queryBuilder = clienteDAO.queryBuilder();
        queryBuilder.where().like("nome", String.format("%%%s%%", nome.toUpperCase())).and().eq("excluido", false);
        queryBuilder.orderBy("nome", true);

        return queryBuilder.query();
    }

    public void salvarCoordenadas(Long id, double latitude, double longitude) throws SQLException {
        UpdateBuilder<Cliente, Long> updateBuilder = clienteDAO.updateBuilder();
        updateBuilder.updateColumnValue("latitude", latitude);
        updateBuilder.updateColumnValue("longitude", longitude);
        updateBuilder.where().eq("id", id);
        updateBuilder.update();
    }

    public List<LocalizacaoCliente> buscarNaoVisitados() throws SQLException {

        List<LocalizacaoCliente> localizacaoClientes = new ArrayList<>();
        List<String[]> results = clienteDAO.queryRaw("select a.id, cast(a.latitude as varchar), cast(a.longitude as varchar) from cliente a where a.excluido = 0 and a.visitado = 0 and coalesce(a.latitude, 0) <> 0 and coalesce(a.longitude, 0) <> 0").getResults();

        for (String[] columns : results) {
            localizacaoClientes.add(new LocalizacaoCliente(Long.valueOf(columns[0]), Double.valueOf(columns[1]), Double.valueOf(columns[2])));
        }

        Log.v(getClass().getName(), localizacaoClientes.toString());

        return localizacaoClientes;
    }

    public void marcarVisitado(Long id) throws SQLException {
        UpdateBuilder<Cliente, Long> updateBuilder = clienteDAO.updateBuilder();
        updateBuilder.updateColumnValue("visitado", true);
        updateBuilder.where().eq("id", id);
        updateBuilder.update();
    }
}
