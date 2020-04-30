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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.security.func.R;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.adapters.ClientesRecyclerAdapter;
import br.com.security.func.dao.ClienteDAO;
import br.com.security.func.dialogs.ClienteFilterDialog;
import br.com.security.func.models.orm.Cliente;

public class ClientesFragment extends Fragment {

    private RecyclerView clienteRecyclerView;
    private RecyclerView.Adapter clientesAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ClienteDAO clienteDAO;
    private List<Cliente> clientes = new ArrayList<>();
    private String lastFiltro;
    private View layoutNoRecords, layoutRecords;
    private LinearLayout layoutProgress;

    public static ClientesFragment newInstance() {
        ClientesFragment fragment = new ClientesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DashboardActivity) getActivity()).navigation.setSelectedItemId(R.id.nav_clientes);

        setHasOptionsMenu(true);
        clienteDAO = ClienteDAO.getInstance(getContext());
        clientesAdapter = new ClientesRecyclerAdapter(getContext(), clientes);
        layoutManager = new LinearLayoutManager(getContext()); // Linear layout
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        layoutNoRecords = view.findViewById(R.id.layout_no_records);
        layoutRecords = view.findViewById(R.id.layout_records);
        layoutProgress = view.findViewById(R.id.layout_progress);
        clienteRecyclerView = view.findViewById(R.id.clientes_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        clienteRecyclerView.setHasFixedSize(true);
        clienteRecyclerView.setLayoutManager(layoutManager);
        clienteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        clienteRecyclerView.setAdapter(clientesAdapter);

        prepareDataSet("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.clientes_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.filtrar:
                final ClienteFilterDialog clienteFilterDialog = new ClienteFilterDialog(getContext());
                clienteFilterDialog.setOnConfirmListener(new ClienteFilterDialog.OnConfirmListener() {

                    @Override
                    public void onConfirm() {
                        lastFiltro = clienteFilterDialog.edFiltroNome.getText().toString();
                        prepareDataSet(lastFiltro);
                        clienteFilterDialog.dismiss();
                    }
                });

                clienteFilterDialog.show();
                clienteFilterDialog.setFiltro(lastFiltro);

                return false;

            case R.id.nav_dashboard:
                ((DashboardActivity) getActivity()).loadDashboardFragment(true);
                return false;
        }

        return false;

    }

    private void prepareDataSet(final String nome) {

        AsyncTask getClientesTask = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                layoutProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    return clienteDAO.todos(nome);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                try {

                    if (o != null && o instanceof List) {
                        clientes.clear();
                        clientes.addAll((List<Cliente>) o);
                    }


                } finally {
                    layoutProgress.setVisibility(View.GONE);
                    clientesAdapter.notifyDataSetChanged();

                    if (lastFiltro != null && !lastFiltro.trim().isEmpty())
                        ((TextView) layoutNoRecords.findViewById(R.id.tx_filter_result)).setText("Nenhum cliente encontrado");
                    else
                        ((TextView) layoutNoRecords.findViewById(R.id.tx_filter_result)).setText("Nenhum cliente sincronizado");


                    ((TextView) layoutNoRecords.findViewById(R.id.tx_filter)).setText(lastFiltro);

                    // mostra determinado layout
                    if (!clientes.isEmpty()) {
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

    private void showLayoutNoRecords(boolean show) {
        layoutNoRecords.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showLayoutRecords(boolean show) {
        layoutRecords.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
