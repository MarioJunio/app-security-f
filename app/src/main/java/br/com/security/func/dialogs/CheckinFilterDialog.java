package br.com.security.func.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.security.func.R;
import br.com.security.func.dao.filters.FiltroHistorico;

/**
 * Created by mariomartins on 16/09/17.
 */

public class CheckinFilterDialog extends Dialog implements DatePickerDialog.OnDateSetListener {

    private Activity activity;
    public EditText edFiltroCliente, edFiltroDataInicial, edFiltroDataFinal;
    private Button btnConfirmar;
    private ImageView btnClose;
    private OnConfirmListener onConfirmListener;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private int fieldSelectedDate;

    public CheckinFilterDialog(@NonNull Activity context) {
        super(context);
        this.activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.dialog_filtro_checkins);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // cria dialogo date picker
        createDatePickerDialog();

        btnClose = findViewById(R.id.btn_close);
        edFiltroCliente = findViewById(R.id.ed_filtro_nome);
        edFiltroDataInicial = findViewById(R.id.ed_filtro_data_inicial);
        edFiltroDataFinal = findViewById(R.id.ed_filtro_data_final);
        btnConfirmar = findViewById(R.id.btn_confirm);

        edFiltroCliente.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (edFiltroCliente.getCompoundDrawables()[DRAWABLE_RIGHT] != null && event.getRawX() >= (edFiltroCliente.getRight() - edFiltroCliente.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        edFiltroCliente.getText().clear();
                        edFiltroCliente.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                        return true;
                    }
                }
                return false;
            }
        });

        edFiltroCliente.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() >= 3)
                    edFiltroCliente.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, R.drawable.ic_clear, 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edFiltroDataInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldSelectedDate = 1;
                datePickerDialog.show();
            }
        });

        edFiltroDataFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldSelectedDate = 2;
                datePickerDialog.show();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onConfirmListener != null)
                    onConfirmListener.onConfirm();
            }
        });
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public void setFiltro(FiltroHistorico filtro) {

        if (filtro != null) {
            edFiltroCliente.setText(filtro.getCliente());

            if (filtro.getDataInicial() != null)
                edFiltroDataInicial.setText(dateFormat.format(filtro.getDataInicial()));

            if (filtro.getDataFinal() != null)
                edFiltroDataFinal.setText(dateFormat.format(filtro.getDataFinal()));
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        if (fieldSelectedDate == 1)
            edFiltroDataInicial.setText(String.format("%d/%02d/%d", day, month + 1, year));
        else if (fieldSelectedDate == 2)
            edFiltroDataFinal.setText(String.format("%d/%02d/%d", day, month + 1, year));

    }

    private void createDatePickerDialog() {

        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(activity, this, year, month, dayOfMonth);
        datePickerDialog.setCancelable(false);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Limpar", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (fieldSelectedDate == 1)
                    edFiltroDataInicial.getText().clear();
                else if (fieldSelectedDate == 2)
                    edFiltroDataFinal.getText().clear();
            }
        });
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

}
