package br.com.security.func.dialogs;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;

import br.com.security.func.R;

/**
 * Created by mariomartins on 16/09/17.
 */

public class ClienteFilterDialog extends Dialog {

    public EditText edFiltroNome;
    private Button btnConfirmar;
    private ImageView btnClose;
    private OnConfirmListener onConfirmListener;

    public ClienteFilterDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.dialog_filtro_clientes);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        btnClose = findViewById(R.id.btn_close);
        edFiltroNome = findViewById(R.id.ed_filtro_nome);
        btnConfirmar = findViewById(R.id.btn_confirm);

        edFiltroNome.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (edFiltroNome.getCompoundDrawables()[DRAWABLE_RIGHT] != null && event.getRawX() >= (edFiltroNome.getRight() - edFiltroNome.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        edFiltroNome.getText().clear();
                        edFiltroNome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                        return true;
                    }
                }
                return false;
            }
        });

        edFiltroNome.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() >= 3)
                    edFiltroNome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, R.drawable.ic_clear, 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

                if (onConfirmListener != null) {
                    onConfirmListener.onConfirm();
                }
            }
        });
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public void setFiltro(String lastFiltro) {

        if (lastFiltro != null)
            edFiltroNome.setText(lastFiltro);
    }

    public interface OnConfirmListener {

        void onConfirm();
    }

}
