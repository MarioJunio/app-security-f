package br.com.security.func.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.security.func.R;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.adapters.CheckinsRecyclerAdapter;
import br.com.security.func.config.App;
import br.com.security.func.dao.CheckinDAO;
import br.com.security.func.dao.filters.FiltroHistorico;
import br.com.security.func.dialogs.CheckinFilterDialog;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.wrappers.CheckinType;
import br.com.security.func.wrappers.DateType;
import br.com.security.func.wrappers.ListItemCheckin;

public class HistoricoFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout layoutProgress;
    private CheckinDAO checkinDAO;
    private View layoutNoRecords, layoutRecords;
    private List<ListItemCheckin> checkins = new ArrayList<>();
    private FiltroHistorico filtroHistorico = new FiltroHistorico();

    public static HistoricoFragment newInstance() {
        HistoricoFragment fragment = new HistoricoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DashboardActivity) getActivity()).navigation.setSelectedItemId(R.id.nav_history);
        setHasOptionsMenu(true);
        checkinDAO = CheckinDAO.getInstance(getActivity());
        adapter = new CheckinsRecyclerAdapter(getContext(), checkins);
        layoutManager = new LinearLayoutManager(getContext()); // Linear layout
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historico, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        layoutProgress = view.findViewById(R.id.layout_progress);
        layoutNoRecords = view.findViewById(R.id.layout_no_records);
        layoutRecords = view.findViewById(R.id.layout_records);

        recyclerView = view.findViewById(R.id.checkins_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareDataSet();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.historico_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.filtrar:
                final CheckinFilterDialog checkinFilterDialog = new CheckinFilterDialog(getActivity());

                checkinFilterDialog.setOnConfirmListener(new CheckinFilterDialog.OnConfirmListener() {

                    @Override
                    public void onConfirm() {

                        try {
                            filtroHistorico.setCliente(checkinFilterDialog.edFiltroCliente.getText().toString());

                            if (!checkinFilterDialog.edFiltroDataInicial.getText().toString().isEmpty()) {
                                filtroHistorico.setDataInicial(App.parseDate(checkinFilterDialog.edFiltroDataInicial.getText().toString()));
                                filtroHistorico.getDataInicial().setHours(0);
                                filtroHistorico.getDataInicial().setMinutes(0);
                                filtroHistorico.getDataInicial().setSeconds(0);
                            } else
                                filtroHistorico.setDataInicial(null);

                            if (!checkinFilterDialog.edFiltroDataFinal.getText().toString().isEmpty()) {
                                filtroHistorico.setDataFinal(App.parseDate(checkinFilterDialog.edFiltroDataFinal.getText().toString()));
                                filtroHistorico.getDataFinal().setHours(23);
                                filtroHistorico.getDataFinal().setMinutes(59);
                                filtroHistorico.getDataFinal().setSeconds(59);
                            } else
                                filtroHistorico.setDataFinal(null);

                            checkinFilterDialog.dismiss();
                            prepareDataSet();

                        } catch (ParseException e) {
                            Toast.makeText(getContext(), "Data está no formato inválido", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                checkinFilterDialog.show();
                checkinFilterDialog.setFiltro(filtroHistorico);

                return false;

            case R.id.nav_dashboard:
                ((DashboardActivity) getActivity()).loadDashboardFragment(true);
                return false;
        }

        return false;

    }

    private void prepareDataSet() {

        AsyncTask getClientesTask = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                layoutProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Object doInBackground(Object[] objects) {

                List<ListItemCheckin> historico = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    Calendar dataAnterior = null;
                    List<Checkin> checkins = checkinDAO.buscarHistorico(getActivity(), filtroHistorico);
                    int c = 0, j = 0;

                    for (int i = 0; i < checkins.size(); i++) {

                        Checkin checkin = checkins.get(i);
                        Calendar dataAtual = toCalendar(checkin.getData());

                        if (dataAnterior == null || (dataAtual.get(Calendar.DAY_OF_MONTH) != dataAnterior.get(Calendar.DAY_OF_MONTH) || dataAtual.get(Calendar.MONTH) != dataAnterior.get(Calendar.MONTH) || dataAtual.get(Calendar.YEAR) != dataAnterior.get(Calendar.YEAR))) {

                            if (dataAnterior != null) {

                                CheckinType checkinType = (CheckinType) historico.get(j - 1);

                                // se existi apenas um item no grupo anterior
                                if (c == 1)
                                    checkinType.setType(ListItemCheckin.TYPE_CHECKIN_FULL);
                                else
                                    checkinType.setType(ListItemCheckin.TYPE_CHECKIN_BOTTOM);
                            }

                            c = 0;

                            historico.add(new DateType(dateFormat.format(checkin.getData())));
                            j++;
                            historico.add(new CheckinType(checkin, ListItemCheckin.TYPE_CHECKIN_TOP));

                        } else
                            historico.add(new CheckinType(checkin, ListItemCheckin.TYPE_CHECKIN_MIDDLE));

                        j++;

                        // contador de elementos em cada grupo
                        c++;

                        // altera a data anterior
                        dataAnterior = dataAtual;

                    }

                    if (j > 0) {

                        CheckinType checkinType = (CheckinType) historico.get(j - 1);

                        // ajusta o último elemento
                        if (c == 1)
                            checkinType.setType(ListItemCheckin.TYPE_CHECKIN_FULL);
                        else
                            checkinType.setType(ListItemCheckin.TYPE_CHECKIN_BOTTOM);
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return historico;
            }

            @Override
            protected void onPostExecute(Object o) {

                try {

                    if (o != null && o instanceof List) {
                        checkins.clear();
                        checkins.addAll((List<ListItemCheckin>) o);
                    }

                } finally {
                    layoutProgress.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();

                    if (filtroHistorico.isNotEmpty())
                        ((TextView) layoutNoRecords.findViewById(R.id.tx_filter_result)).setText("Nenhuma inspeção encontrada");
                    else
                        ((TextView) layoutNoRecords.findViewById(R.id.tx_filter_result)).setText("Nenhuma inspeção sincronizada");

                    // mostra determinado layout
                    if (!checkins.isEmpty()) {
                        showLayoutNoRecords(false);
                        showLayoutRecords(true);
                    } else {
                        showLayoutNoRecords(true);
                        showLayoutRecords(false);
                    }



                }

            }
        };

        getClientesTask.execute();
    }

    public static Calendar toCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;

    }

    private void showLayoutNoRecords(boolean show) {
        layoutNoRecords.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showLayoutRecords(boolean show) {
        layoutRecords.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
