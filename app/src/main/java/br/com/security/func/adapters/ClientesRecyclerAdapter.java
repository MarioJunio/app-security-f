package br.com.security.func.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.security.func.R;
import br.com.security.func.models.orm.Cliente;

public class ClientesRecyclerAdapter extends RecyclerView.Adapter<ClientesRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Cliente> dataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        CardView cardView;
        ImageView icUser, icAddress, icPhone;
        TextView txNome, txEndereco, txTelefone;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ClientesRecyclerAdapter(Context context, List<Cliente> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ClientesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_cliente, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.icUser = view.findViewById(R.id.ic_user);
        viewHolder.icAddress = view.findViewById(R.id.ic_address);
        viewHolder.icPhone = view.findViewById(R.id.ic_phone);

        viewHolder.cardView = view.findViewById(R.id.card_view);
        viewHolder.txNome = view.findViewById(R.id.tx_nome);
        viewHolder.txEndereco = view.findViewById(R.id.tx_endereco);
        viewHolder.txTelefone = view.findViewById(R.id.tx_telefone);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cliente cliente = dataSet.get(position);

        // se as coordenadas de localização ja foram cadastradas não exibi em amarelo
        if (!cliente.hasCoords()) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

            paintWhite(holder.icUser);
            paintWhite(holder.icAddress);
            paintWhite(holder.icPhone);

            holder.txNome.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.txEndereco.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.txTelefone.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.txNome.setText(cliente.getNome());
        holder.txEndereco.setText(String.format("%s, %d - %s, %s - %s, %s", cliente.getLogradouro(), cliente.getNumero(), cliente.getBairro(), cliente.getCidade(), cliente.getUf(), cliente.getCep()));
        holder.txTelefone.setText(TextUtils.isEmpty(cliente.getTelefone1()) ? (TextUtils.isEmpty(cliente.getTelefone2()) ? "Telefone indisponível" : cliente.getTelefone2()) : cliente.getTelefone1());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    void paintWhite(ImageView ic) {
        ic.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
    }

}

