package br.com.security.func.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.security.func.R;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.stub.CheckinStatus;
import br.com.security.func.wrappers.CheckinType;
import br.com.security.func.wrappers.DateType;
import br.com.security.func.wrappers.ListItemCheckin;

public class CheckinsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ListItemCheckin> dataSet;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public CheckinsRecyclerAdapter(Context context, List<ListItemCheckin> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        // se for item do tipo Data
        if (viewType == ListItemCheckin.TYPE_DATE)
            viewHolder = new DateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_checkin_date, parent, false));
        else if (viewType == ListItemCheckin.TYPE_CHECKIN_TOP)
            viewHolder = new CheckinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_checkin_top, parent, false));
        else if (viewType == ListItemCheckin.TYPE_CHECKIN_MIDDLE)
            viewHolder = new CheckinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_checkin_mid, parent, false));
        else if (viewType == ListItemCheckin.TYPE_CHECKIN_BOTTOM)
            viewHolder = new CheckinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_checkin_bottom, parent, false));
        else if (viewType == ListItemCheckin.TYPE_CHECKIN_FULL)
            viewHolder = new CheckinViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_checkin_full, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // tipo da view
        int type = getItemViewType(position);

        // se for view para exibir a data de agrupamento
        if (type == ListItemCheckin.TYPE_DATE) {
            DateType dataType = (DateType) dataSet.get(position);
            DateViewHolder dateViewHolder = (DateViewHolder) holder;
            dateViewHolder.txGroupDate.setText(dataType.getDate());
        } else {
            Checkin checkin = ((CheckinType) dataSet.get(position)).getCheckin();

            CheckinViewHolder checkinViewHolder = (CheckinViewHolder) holder;
            checkinViewHolder.txHorario.setText(dateFormat.format(checkin.getData()));
            checkinViewHolder.txNome.setText(checkin.getCliente().getNome());
            checkinViewHolder.txEndereco.setText(String.format("%s, %d - %s, %s - %s, %s", checkin.getCliente().getLogradouro(), checkin.getCliente().getNumero(), checkin.getCliente().getBairro(), checkin.getCliente().getCidade(), checkin.getCliente().getUf(), checkin.getCliente().getCep()));
            checkinViewHolder.btnStatus.setText(checkin.getStatus());

            if (checkin.getStatus().equals(CheckinStatus.NORMAL.name()))
                checkinViewHolder.btnStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            else if (checkin.getStatus().equals(CheckinStatus.SUSPEITO.name()))
                checkinViewHolder.btnStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_light));
            else if (checkin.getStatus().equals(CheckinStatus.PERIGO.name()))
                checkinViewHolder.btnStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.red_dark));

            checkinViewHolder.txObservacao.setText(checkin.getDescricao());

            int viewType = getItemViewType(position);

//            if (viewType == ListItemCheckin.TYPE_CHECKIN_BOTTOM || viewType == ListItemCheckin.TYPE_CHECKIN_FULL)
//                checkinViewHolder.lineSeparator.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txGroupDate;

        public DateViewHolder(View view) {
            super(view);
            this.view = view;
            this.txGroupDate = view.findViewById(R.id.tx_group_date);
        }
    }

    public static class CheckinViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txNome, txEndereco, txHorario, txObservacao;
        Button btnStatus;
//        View lineSeparator;

        public CheckinViewHolder(View view) {
            super(view);
            this.view = view;
            this.txNome = view.findViewById(R.id.tx_nome);
            this.txEndereco = view.findViewById(R.id.tx_endereco);
            this.txHorario = view.findViewById(R.id.tx_horario);
            this.btnStatus = view.findViewById(R.id.btn_status);
            this.txObservacao = view.findViewById(R.id.tx_observacao);
//            this.lineSeparator = view.findViewById(R.id.line_separator);
        }
    }

}

