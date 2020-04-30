package br.com.security.func.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.com.security.func.R;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.config.App;
import br.com.security.func.config.Session;
import br.com.security.func.dao.CheckinDAO;
import br.com.security.func.dao.ClienteDAO;
import br.com.security.func.dialogs.AppDialog;
import br.com.security.func.dialogs.PhotoCaptureDialog;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.models.orm.Cliente;
import br.com.security.func.service.SyncService;
import br.com.security.func.utils.AppIntents;
import br.com.security.func.utils.Cameras;
import br.com.security.func.utils.Images;
import br.com.security.func.wrappers.InspecaoWrapper;

public class InspecionarCheckinFragment extends Fragment {

    private static final String ARG_CLIENTE_CHECKIN = "key_cliente_checkin";
    private static final String ARG_QRCODE_SCANNER = "key_qrcode_scanner";

    static final int INSPECAO_FOTO_CODE = 10;

    // permission requests
    static final int CAMERA_REQUEST = 1;
    static final int WRITE_EXTERNAL_STORAGE_REQUEST = 2;
    static final int ALL_REQUEST = 9;

    private InspecaoWrapper inspecaoWrapper;
    private boolean qrcodeScanner;

    private ImageView icQuit;
    private ImageView icCamera;
    private ImageView icConfirm;

    private EditText edClienteSelected;
    private ImageButton btnEditCliente;
    private LinearLayout checklist;
    private RadioGroup rgStatus;
    private EditText edDescricao;

    private Uri uriFotoInspecao;
    private Bitmap fotoInspecao;

    public static InspecionarCheckinFragment newInstance(InspecaoWrapper inspecaoWrapper, boolean qrcodeScanner) {
        InspecionarCheckinFragment fragment = new InspecionarCheckinFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CLIENTE_CHECKIN, inspecaoWrapper);
        bundle.putBoolean(ARG_QRCODE_SCANNER, qrcodeScanner);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = getArguments();

        if (bundle != null) {
            inspecaoWrapper = (InspecaoWrapper) bundle.getSerializable(ARG_CLIENTE_CHECKIN);
            qrcodeScanner = bundle.getBoolean(ARG_QRCODE_SCANNER);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {

            if (requestCode == ALL_REQUEST) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    uriFotoInspecao = AppIntents.startCameraCapture(this, INSPECAO_FOTO_CODE);
                } else {
                    Toast.makeText(getContext(), "Para utilizar essa funcionalidade, é necessário liberar a permissão para usar a câmera", Toast.LENGTH_SHORT).show();
                }
            }

            if (requestCode == CAMERA_REQUEST) {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uriFotoInspecao = AppIntents.startCameraCapture(this, INSPECAO_FOTO_CODE);
                } else {
                    Toast.makeText(getContext(), "Para utilizar essa funcionalidade, é necessário liberar a permissão para usar a câmera", Toast.LENGTH_LONG).show();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == INSPECAO_FOTO_CODE && resultCode == Activity.RESULT_OK) {

            try {
                icCamera.setImageResource(R.drawable.ic_image);

                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SecurityCheckin");
                File fFoto = new File(mediaStorageDir, getNomeFotoCapturada(uriFotoInspecao.getPathSegments()));
                Log.v(getClass().getCanonicalName(), "Path: " + fFoto.getAbsolutePath() + ", Existe: " + fFoto.exists());
                fotoInspecao = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uriFotoInspecao));
                fotoInspecao = Cameras.rotateToPortrait(fFoto.getAbsolutePath(), ThumbnailUtils.extractThumbnail(fotoInspecao, 1000, 650));

            } catch (IOException e) {
                AppDialog.showDialog(getActivity(), "Foto inspeção", "Não foi possivel capturar a foto", "OK", null, null, null);
                e.printStackTrace();
            }

        }
    }

    private String getNomeFotoCapturada(List<String> pathSegments) {

        for (String pathSegment : pathSegments) {

            if (pathSegment.startsWith("IMG_")) {
                return pathSegment;
            }
        }

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inspecionar_checkin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        edClienteSelected = view.findViewById(R.id.cliente_selected);
        btnEditCliente = view.findViewById(R.id.btn_edit_cliente);
        checklist = view.findViewById(R.id.checklist);
        rgStatus = view.findViewById(R.id.rg_status);
        edDescricao = view.findViewById(R.id.ed_descricao);

        // cliente selecionado
        edClienteSelected.setText(inspecaoWrapper.getNome());

        // verifica se a leitura foi scanner ou manual
        if (qrcodeScanner)
            btnEditCliente.setVisibility(View.GONE);
        else {
            btnEditCliente.setVisibility(View.VISIBLE);
            btnEditCliente.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    AppDialog.showDialog(getActivity(), null, "Você será redirecionado para a tela anterior, você confirma essa operação?", "Sim", "Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((DashboardActivity) (getActivity())).loadInspecionarManualFragment();
                        }
                    }, null);

                }
            });
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.inspecao_checkin_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final Fragment TMP_FRAGMENT = this;

        icQuit = new ImageView(getActivity());
        icQuit.setImageResource(R.drawable.ic_quit);
        icQuit.setPadding(App.getPixelsInDP(5), App.getPixelsInDP(5), App.getPixelsInDP(20), App.getPixelsInDP(5));
        icQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).loadDashboardFragment(false);
                App.closeKeyboard((AppCompatActivity) getActivity());
            }
        });

        icCamera = new ImageView(getActivity());
        icCamera.setImageResource(R.drawable.ic_camera);
        icCamera.setPadding(App.getPixelsInDP(5), App.getPixelsInDP(5), App.getPixelsInDP(20), App.getPixelsInDP(5));
        icCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // se alguma foto de inspecao ja foi tirada, a foto sera exibida
                if (fotoInspecao == null) {
                    callCamera();
                } else {
                    new PhotoCaptureDialog(TMP_FRAGMENT, fotoInspecao, INSPECAO_FOTO_CODE).show();
                }
            }
        });

        icConfirm = new ImageView(getActivity());
        icConfirm.setImageResource(R.drawable.ic_confirm);
        icConfirm.setPadding(App.getPixelsInDP(5), App.getPixelsInDP(5), App.getPixelsInDP(20), App.getPixelsInDP(5));
        icConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmCheckin();
            }
        });

        MenuItem menuQuit = menu.getItem(0);
        menuQuit.setActionView(icQuit);
        menuQuit.getActionView().animate().alpha(1.0f);

        MenuItem menuCamera = menu.getItem(1);
        menuCamera.setActionView(icCamera);
        menuCamera.getActionView().animate().alpha(1.0f);

        MenuItem menuConfirm = menu.getItem(2);
        menuConfirm.setActionView(icConfirm);
        menuConfirm.getActionView().animate().alpha(1f);
    }

    /**
     * Inicia a camera para tirar uma nova foto
     */
    private void callCamera() {

        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                boolean canAskPermission = App.canAskPermission();
                boolean hasWriteExternalStoragePermisson = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                boolean hasCameraPermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

                if (canAskPermission && !hasWriteExternalStoragePermisson && !hasCameraPermission) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ALL_REQUEST);
                }

                if (App.canAskPermission() && !hasWriteExternalStoragePermisson) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
                } else if (App.canAskPermission() && !hasCameraPermission) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                } else {
                    uriFotoInspecao = AppIntents.startCameraCapture(this, INSPECAO_FOTO_CODE);
                }

            } else {
                uriFotoInspecao = AppIntents.startCameraCapture(this, INSPECAO_FOTO_CODE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmCheckin() {
        boolean allChecked = true;
        String status = "";

        for (int i = 0; i < checklist.getChildCount(); i++) {
            CheckBox cb = (CheckBox) checklist.getChildAt(i);

            if (!cb.isChecked()) {
                allChecked = false;
                break;
            }
        }

        for (int i = 0; i < rgStatus.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgStatus.getChildAt(i);

            if (rb.isChecked()) {
                status = rb.getText().toString().toUpperCase();
                break;
            }
        }

        // vefica se a foto da inspeção foi capturada
        if (fotoInspecao == null) {
            AppDialog.showDialog(getActivity(), null, "Capture uma foto do local da inspeção.", "OK", null, null, null);
            return;
        }

        // verifica se todos os checkbox foram marcados
        if (!allChecked)
            AppDialog.showDialog(getActivity(), null, "Marque as opções de inspeção para continuar.", "OK", null, null, null);
        else {

            // se passou nas validações, pode concluir o checkin
            final String finalStatus = status;

            if (!inspecaoWrapper.isVisitado()) {
                AppDialog.showDialog(getActivity(), null, "Esse cliente ainda não foi visitado, certifique que você está no endereço correto do cliente, pois as inspeções futuras deste cliente só poderão ser realizadas neste endereço.", "Entendi", "Não entendi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveCheckin(finalStatus);
                    }
                }, null);

            } else {
                saveCheckin(finalStatus);
            }
        }
    }

    /**
     * Salva o checkin atual
     *
     * @param finalStatus
     */
    private void saveCheckin(final String finalStatus) {

        try {
            CheckinDAO.getInstance(getContext()).save(new Checkin(Session.withContext(getActivity()).getAuthencationUser().getId(), new Cliente(inspecaoWrapper.getClienteId()), new Date(), inspecaoWrapper.getLatitude(), inspecaoWrapper.getLongitude(), finalStatus, edDescricao.getText().toString(), false, Images.toBase64(fotoInspecao)));

            // se ainda não foi visitado, salve as primeiras coordenadas para verificação no próximo checkin
            if (!inspecaoWrapper.isVisitado()) {
                ClienteDAO.getInstance(getContext()).salvarCoordenadas(inspecaoWrapper.getClienteId(), inspecaoWrapper.getLatitude(), inspecaoWrapper.getLongitude());
            }

            // requisita sincronização
            App.requestSyncService(getContext(), SyncService.ARG_SYNC_CHECKINS_UPLOAD);
            App.requestSyncService(getContext(), SyncService.ARG_SYNC_CLIENTES_VISITADOS);

            //Snackbar.make(getActivity().findViewById(android.R.id.content), "Inspeção realizada com sucesso.", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "A inspeção foi realizada, e em breve será sincronizada", Toast.LENGTH_SHORT).show();
            ((DashboardActivity) (getActivity())).loadDashboardFragment(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
