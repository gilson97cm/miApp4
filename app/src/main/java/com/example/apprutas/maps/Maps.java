package com.example.apprutas.maps;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apprutas.MapAllRoutes;
import com.example.apprutas.R;
import com.example.apprutas.Util;
import com.example.apprutas.bd.connection;
import com.example.apprutas.directionhelpers.FetchURL;
import com.example.apprutas.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Currency;


public class Maps extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {


    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;

    private GoogleMap mMap;
    //variables para la localizacion
    LocationManager locationManager; //administrador
    Location location; //localizacion
    String idUser = "";
    String name = "";
    String lastName = "";
    byte[] blob = null;

    Integer idRoute = 0;
    String city = "";
    float lat = 0;
    float lng = 0;
    String startRoute = "";
    String endRoute = "";
    String wayRoute = "";

    MarkerOptions markerOptions;
    private Polyline currentPolyline;

    String idStart = "";
    String idEnd = "";

    Util util = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //recuperar variables que envia MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idUser = extras.getString("idUser");
            // consult(idUser);
        }
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.show();
        }
        listPoints = new ArrayList<>();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //tipos de mapas
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        //default position
        float zoomLevel = 12;
        final LatLng cotopaxi = new LatLng(-0.8851348333331117, -78.63627496756541);
        //mMap.addMarker(new MarkerOptions().position().title("El Salto").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cotopaxi, zoomLevel));
        Antut(idUser, googleMap);

        //consular ubicaciones

    }

    public void Antut(String id, GoogleMap googleMap) {
        mMap = googleMap;

        connection db = new connection(this, "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();

        Cursor user = baseDatos.rawQuery("SELECT * FROM user WHERE id = '" + id + "'", null);
        if (user.moveToFirst()) {
            name = user.getString(1);
            lastName = user.getString(2);

            blob = user.getBlob(3);
            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            Bitmap roundBtmp = util.getCroppedBitmap(bmp);
            Bitmap finalBtmp = Bitmap.createScaledBitmap(roundBtmp, 60, 60, false);
            ImageView image = new ImageView(this);
            //avatarUserRoutes.setImageBitmap(finatBtmp);
            Cursor routes = baseDatos.rawQuery("SELECT * FROM route WHERE idUser = '" + id + "' ORDER BY id ASC ", null);
            int i = 0, c = 0;
            if (routes.moveToFirst()) {
                do {
                    idRoute = routes.getInt(0);
                    city = i+1+". "+routes.getString(1);
                    lat = Float.parseFloat(routes.getString(2));
                    lng = Float.parseFloat(routes.getString(3));
                    //  startRoute = routes.getString(5);
                    // endRoute = routes.getString(6);
                    LatLng point = new LatLng(lat, lng);
                    //  mMap.addMarker(new MarkerOptions().position(point).title(city).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    markerOptions = new MarkerOptions().position(point)
                            .title(city)
                            .snippet(name + " " + lastName)
                            .icon(BitmapDescriptorFactory.fromBitmap(finalBtmp));
                    mMap.addMarker(markerOptions);
                    i++;
                } while (routes.moveToNext());
            } else {
                Toast.makeText(this, "No hay lugares para marcar.", Toast.LENGTH_SHORT).show();
            }

            //consulta para ruta inicial
            Cursor start = baseDatos.rawQuery("SELECT id, latLng FROM route WHERE idUser = '" + id + "' ORDER BY id ASC ", null);
            if (start.moveToFirst()) {
                idStart = start.getString(0);
                startRoute = start.getString(1);
            } else {
                Toast.makeText(this, "No hay ruta inicial.", Toast.LENGTH_SHORT).show();
            }
            //consulta para ruta final
            Cursor end = baseDatos.rawQuery("SELECT id, latLng FROM route WHERE idUser = '" + id + "' ORDER BY id DESC ", null);
            if (end.moveToFirst()) {
                idEnd = end.getString(0);
                endRoute = end.getString(1);
            } else {
                Toast.makeText(this, "No hay ruta final.", Toast.LENGTH_SHORT).show();
            }

            if (routes.getCount() > 2) {
                //puntos medios
                Cursor waypoints = baseDatos.rawQuery("SELECT GROUP_CONCAT(latLng, '|')   FROM route WHERE id <> '" + idStart + "' AND id <> '" + idEnd + "' and idUser = '" + idUser + "'", null);
                if (waypoints.moveToFirst()) {
                    wayRoute = waypoints.getString(0);
                }
                //ruta con puntos medios

                new FetchURL(Maps.this).execute(util.getUrlWhitWayPoints(startRoute, wayRoute, endRoute, "driving"), "driving");
            }

            if(routes.getCount() == 2){
                //ruta con puntos medios
                new FetchURL(Maps.this).execute(util.getUrl(startRoute, endRoute, "driving"), "driving");
            }

        } else {
            Toast.makeText(this, "No hay registro.", Toast.LENGTH_SHORT).show();
        }
        baseDatos.close();
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
