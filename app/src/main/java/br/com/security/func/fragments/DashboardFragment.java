package br.com.security.func.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.security.func.R;
import br.com.security.func.activities.DashboardActivity;
import br.com.security.func.application.MyApplication;
import br.com.security.func.config.Session;
import br.com.security.func.dao.CheckinDAO;
import br.com.security.func.dialogs.AppDialog;
import br.com.security.func.location.LocationService;
import br.com.security.func.models.orm.Checkin;
import br.com.security.func.net.AuthenticationService;
import br.com.security.func.service.Router;

public class DashboardFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private LocationService locationService;
    private Location location;
    private GoogleMap mMap;
    private CheckinDAO checkinDAO;

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MapsInitializer.initialize(getContext());
        ((DashboardActivity) getActivity()).clearNavSelected();

        checkinDAO = CheckinDAO.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        prepareDataSet();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dashboard_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout:

                AppDialog.showDialog(getActivity(), null, "Você está efetuando o logout, deseja continuar?", "Sim", "Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {

                            AuthenticationService.doLogout(getContext(), new Response.Listener() {

                                @Override
                                public void onResponse(Object response) {
                                    logout();
                                }

                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    logout();
                                    Toast.makeText(getContext(), "Verifique se você está conectado a Internet, para efetuar a autenticação novamente", Toast.LENGTH_LONG).show();
                                }
                            });

                        } catch (AuthFailureError authFailureError) {
                            authFailureError.printStackTrace();
                        }
                    }

                }, null);

                return false;
        }

        return false;

    }

    private void logout() {
        Session.withContext(getContext()).saveAuthToken("");
        Router.goAuthView((AppCompatActivity) getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // se for android <= M, pode obter o local
        if (mMap != null && locationService != null && locationService.isCanGetLocation())
            mMap.setMyLocationEnabled(true);


        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-18.7302, -47.4912), 5));
    }

    @Override
    public void onLocationChanged(Location location) {

        if (mMap != null && location != null) {
            this.location = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
        this.locationService.getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {

        if (locationService.getLocationManager() != null) {
            locationService.getLocationManager().removeUpdates(this);
            this.location = null;
        }
    }

    private void prepareDataSet() {

        AsyncTask getClientesTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                try {
                    return checkinDAO.buscarDashboard(getContext());
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                if (mMap != null && o != null && o instanceof List) {
                    List<Checkin> checkins = (List<Checkin>) o;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    for (Checkin checkin : checkins) {
                        mMap.addMarker(createMarker(checkin.getCliente().getNome(), dateFormat.format(checkin.getData()), new LatLng(checkin.getLatitude(), checkin.getLongitude())));
                    }

                }
            }
        };

        getClientesTask.execute();

    }

    public static Bitmap createDrawableFromView(Activity context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private MarkerOptions createMarker(String cliente, String horario, LatLng latLng) {

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(cliente).snippet(horario);

        try {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), ((LayoutInflater) MyApplication.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_custom_marker, null))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return markerOptions;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public GoogleMap getmMap() {
        return mMap;
    }
}
