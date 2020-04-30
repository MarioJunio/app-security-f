package br.com.security.func.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.security.func.config.DatabaseHelper;
import br.com.security.func.config.Session;
import br.com.security.func.dao.filters.FiltroHistorico;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.orm.Cliente;

/**
 * Created by mariomartins on 15/09/17.
 */

public class CheckinDAO {

    private Dao<Checkin, Long> checkinDao;
    private Dao<Cliente, Long> clienteDao;

    public static CheckinDAO getInstance(Context context) {
        return new CheckinDAO(new DatabaseHelper(context));
    }

    private CheckinDAO(DatabaseHelper databaseHelper) {
        this.checkinDao = databaseHelper.getCheckinDao();
        this.clienteDao = databaseHelper.getClienteDao();
    }

    public void save(Checkin checkin) throws SQLException {
        this.checkinDao.createOrUpdate(checkin);
    }

    public List<Checkin> buscarDesincronizados() throws SQLException {
        return checkinDao.queryBuilder().where().eq("sync", false).query();
    }

    public void marcarComoSincronizado(Long id) throws SQLException {
        UpdateBuilder<Checkin, Long> updateBuilder = checkinDao.updateBuilder();
        updateBuilder.updateColumnValue("sync", true);
        updateBuilder.where().eq("id", id);
        updateBuilder.update();
    }

    public List<Checkin> buscarHistorico(Context context, FiltroHistorico filtro) throws SQLException {
        QueryBuilder<Checkin, Long> queryBuilder = checkinDao.queryBuilder();
        QueryBuilder<Cliente, Long> queryBuilderCliente = clienteDao.queryBuilder();
        queryBuilder.join(queryBuilderCliente);

        Where<Checkin, Long> where = queryBuilder.where().eq("funcionario_id", Session.withContext(context).getAuthencationUser().getId());
        queryBuilder.orderBy("data", false);

        // se possui filtro
        if (filtro != null) {

            if (filtro.getCliente() != null)
                queryBuilderCliente.where().like("nome", String.format("%%%s%%", filtro.getCliente().toUpperCase()));

            if (filtro.getDataFinal() != null && filtro.getDataInicial() != null)
                where.and().between("data", filtro.getDataInicial(), filtro.getDataFinal());
            else if (filtro.getDataInicial() != null)
                where.and().ge("data", filtro.getDataInicial());
            else if (filtro.getDataFinal() != null)
                where.and().le("data", filtro.getDataFinal());
        }

        return queryBuilder.query();
    }

    public List<Checkin> buscarDashboard(Context context) throws SQLException, ParseException {

        List<Checkin> checkinList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        GenericRawResults<String[]> strings = checkinDao.queryRaw("select a.cliente_id, b.nome, cast(a.latitude as varchar), cast(a.longitude as varchar), a.status, a.descricao, max(a.data) as data, a.id from checkin a join cliente b on(b.id = a.cliente_id) where a.funcionario_id = ? group by a.cliente_id", String.valueOf(Session.withContext(context).getAuthencationUser().getId()));

        for (String[] columns : strings)
            checkinList.add(new Checkin(Long.parseLong(columns[7]), 0l, new Cliente(Long.parseLong(columns[0]), columns[1]), dateFormat.parse(columns[6]), Double.valueOf(columns[2]), Double.valueOf(columns[3]), columns[4], columns[5], false, null));

        return checkinList;
    }
}
