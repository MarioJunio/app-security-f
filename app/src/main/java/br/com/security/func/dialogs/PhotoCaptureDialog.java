package br.com.security.func.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;

import br.com.security.func.R;
import br.com.security.func.utils.AppIntents;

public class PhotoCaptureDialog extends Dialog {

    private final int REQUEST_CODE;

    private Fragment fragment;
    private Bitmap bitmap;
    private ImageView fotoCapturada;
    private ImageButton btnNovaFoto;
    private ImageButton btnVoltar;

    public PhotoCaptureDialog(@NonNull Fragment fragment, Bitmap bitmap, final int REQUEST_CODE) {
        super(fragment.getActivity());
        this.fragment = fragment;
        this.bitmap = bitmap;
        this.REQUEST_CODE = REQUEST_CODE;

        Log.v("PhotoCaptureDialog", this.bitmap.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.inspecao_foto_capturada);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (fragment.getResources().getDisplayMetrics().heightPixels * 0.9));

        initWidgets();
        initListeners();
    }

    private void initWidgets() {
        fotoCapturada = findViewById(R.id.foto_captuada);
        btnNovaFoto = findViewById(R.id.btn_capturar);
        btnVoltar = findViewById(R.id.btn_back);

        fotoCapturada.setImageBitmap(bitmap);
    }

    private void initListeners() {
        btnNovaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    AppIntents.startCameraCapture(fragment, REQUEST_CODE);
                    dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
