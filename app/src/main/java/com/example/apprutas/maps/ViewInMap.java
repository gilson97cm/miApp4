package com.example.apprutas.maps;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apprutas.R;
import com.example.apprutas.Util;
import com.example.apprutas.bd.connection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewInMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String idRoute;
    String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_in_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //recuperar variables que envia el usuario para marcar una ruta
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idRoute = extras.getString("idRoute");
            idUser = extras.getString("idUser");
            //consult(idUser);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        Toast.makeText(this, idUser+"\n"+idRoute, Toast.LENGTH_SHORT ).show();
        //tipos de mapas
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        Antut(googleMap);
    }

    public void Antut(GoogleMap googleMap) {
        mMap = googleMap;
        String city = "";
        String nameUser = "";
        String lastNameUser = "";
        float lat = 0;
        float lng = 0;
        byte[] blob = null;
        Bitmap bmp = null;

        //consultar ruta de la bd
        connection db = new connection(this, "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();
        Cursor fila = baseDatos.rawQuery("SELECT * FROM route WHERE id = '" + idRoute + "'", null);
        if (fila.moveToFirst()) {

            city = fila.getString(1);
            lat = Float.parseFloat(fila.getString(2));
            lng = Float.parseFloat(fila.getString(3));

            Cursor user = baseDatos.rawQuery("SELECT * FROM user WHERE id = '" + idUser + "'", null);
            if (user.moveToFirst()) {
                blob = user.getBlob(3); // se recupera  la imagen
                nameUser = user.getString(1);
                lastNameUser = user.getString(2);
                bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);


                ImageView image = new ImageView(this);
                // avatarUserRoutes.setImageBitmap(bmp);
            } else {
                Toast.makeText(this, "No hay usuario.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No hay lugar.", Toast.LENGTH_SHORT).show();
        }
        baseDatos.close();
        //imagen redonda
        Util util = new Util();
        Bitmap roundBtmp = util.getCroppedBitmap(bmp);
        //cambiar el tamaño
        Bitmap finalBtmp = Bitmap.createScaledBitmap(roundBtmp, 60, 60, false);

        final LatLng punto1 = new LatLng(lat, lng);


        mMap.addMarker(new MarkerOptions().position(punto1)
                .title(city)
                .snippet(nameUser + " " + lastNameUser)
                .icon(BitmapDescriptorFactory.fromBitmap(finalBtmp)));

        float zoomLevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(punto1, zoomLevel));
    }


}
