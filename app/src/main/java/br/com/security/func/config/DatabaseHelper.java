package br.com.security.func.config;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.ArrayList;
import java.util.List;

import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.orm.Cliente;

/**
 * Created by mariomartins on 14/09/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // nome do database para sua aplicacao
    private static final String DATABASE_NAME = "data.sqlite";

    // sempre que voce mudar objetos em seu database incremente a versao
    private static final int DATABASE_VERSION = 1;

    // DAO
    private Dao<Cliente, Long> clienteDao;
    private Dao<Checkin, Long> checkinDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Cliente.class);
            TableUtils.createTable(connectionSource, Checkin.class);
        } catch (SQLException e) {
            Log.d(DatabaseHelper.class.getName(), "Não foi possível criar as tabelas", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            List<String> allSql = new ArrayList<>();

            switch (oldVersion) {

                case 1:
                    // allSql.add("alter table AdData add column `new_col` VARCHAR");
                    // allSql.add("alter table AdData add column `new_col2` VARCHAR");
            }

            for (String sql : allSql) {
                database.execSQL(sql);
            }

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }

    }

    public Dao<Cliente, Long> getClienteDao() {

        if (clienteDao == null) {

            try {
                clienteDao = getDao(Cliente.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }

        return clienteDao;
    }

    public Dao<Checkin, Long> getCheckinDao() {

        if (checkinDao == null) {

            try {
                checkinDao = getDao(Checkin.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }

        return checkinDao;
    }

}
