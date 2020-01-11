package com.example.apprutas.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apprutas.R;
import com.example.apprutas.Util;
import com.example.apprutas.adapter.RouteAdapter;
import com.example.apprutas.bd.connection;
import com.example.apprutas.entities.RouteVo;
import com.example.apprutas.maps.Maps;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class UserRoutes extends AppCompatActivity {

    ImageView avatarUserRoutes;
    TextView txtNameUserRoutes;
    TextView txtLastNameUserRoutes;
    TextView txtIdUserRoutes;

    RecyclerView recyclerViewRoutes;
    ArrayList<RouteVo> listRoutes;
    RouteAdapter adapter;

    TextInputLayout txtInputNameCity;
    TextInputEditText txtEditNameCity;

    TextView txtLat;
    TextView txtLng;

    Toolbar toolbar;

    //variables para la localizacion
    LocationManager locationManager; //administrador
    Location location; //localizacion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_routes);
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarUserRoutes);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        init();


        //recuperar variables que envia MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String idUser = extras.getString("id");
            consult(idUser);
        }

        //recycler adapter
        listRoutes = new ArrayList<>();
        recyclerViewRoutes = (RecyclerView) findViewById(R.id.recyclerViewRoutes);
        recyclerViewRoutes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RouteAdapter(listRoutes);
        recyclerViewRoutes.setAdapter(adapter);
        loadRoutes(txtIdUserRoutes.getText().toString());
    }

    //iconoes buscar y capturar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialSearchView searchViewM = (MaterialSearchView) findViewById(R.id.searchViewRoute);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_route, menu);
        MenuItem menuItem = menu.findItem(R.id.searchRoute);
        //SearchView searchView =(SearchView) MenuItemCompat.getActionView(menuItem);
        searchViewM.setMenuItem(menuItem);
        searchViewM.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    ArrayList<RouteVo> listaFiltrada = filter(listRoutes, newText);
                    adapter.setFilter(listaFiltrada);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setFilter(listRoutes);
                return true;
            }
        });
        //icono  para caputar ruta
        getMenuInflater().inflate(R.menu.capture_route, menu);
        //icono  para ver en el mapa con todas las rutas
        getMenuInflater().inflate(R.menu.all_route, menu);
        return true; //
    }

    private ArrayList<RouteVo> filter(ArrayList<RouteVo> categories, String text) {
        ArrayList<RouteVo> listaFiltrada = new ArrayList<RouteVo>();
        try {
            text = text.toLowerCase();

            for (RouteVo routeVo : categories) {
                String deal = routeVo.getCity().toLowerCase();

                if (deal.contains(text)) {
                    listaFiltrada.add(routeVo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaFiltrada;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String idUser = txtIdUserRoutes.getText().toString();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.captureRoute:

                //obtener latitud y longitud
                //solicitar permisos
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //   // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                    Toast.makeText(this, "sin permisos", Toast.LENGTH_SHORT).show();
                } else {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    assert locationManager != null;
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    //GPS_PROVIDER es para el emulador
                    //NETWORK_PROVIDER es para el telefono
                    if (location != null) {
                        txtLat.setText(String.valueOf(location.getLatitude()));
                        txtLng.setText(String.valueOf(location.getLongitude()));

                    } else {
                        Toast.makeText(this, "sin resultados", Toast.LENGTH_SHORT).show();
                    }
                }

                //guardar en la bd

                connection db = new connection(this, "bdRoutes", null, 1);
                SQLiteDatabase baseDatos = db.getWritableDatabase();
                String city = txtEditNameCity.getText().toString();
                String lat = txtLat.getText().toString();
                String lang = txtLng.getText().toString();
                String latLng = lat + "," + lang;

                if (!city.isEmpty()) {
                    ContentValues registro = new ContentValues();

                   registro.put("city", city);
                    registro.put("lat", lat);
                    registro.put("lang", lang);
                    registro.put("idUser", idUser);
                    registro.put("latLng", latLng);

                    baseDatos.insert("route", null, registro);
                    baseDatos.close();
                    clean();
                    listRoutes.clear();
                    adapter = new RouteAdapter(listRoutes);
                    recyclerViewRoutes.setAdapter(adapter);
                    recyclerViewRoutes.setAdapter(adapter);
                    loadRoutes(idUser);
                    Toast.makeText(this, "Se registro un lugar", Toast.LENGTH_SHORT).show();

                } else {
                    txtInputNameCity.setError(" ");
                    Toast.makeText(this, "Hay campos vacios.", Toast.LENGTH_SHORT).show();
                }
                //  Toast.makeText(this, "Ruta Guardada", Toast.LENGTH_SHORT).show();
                // return true;
                break;
            case R.id.allRoute:
                Intent intent = new Intent(UserRoutes.this, Maps.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("idUser", idUser);
                startActivity(intent);
                Toast.makeText(this, "Ver todos los lugares.", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void consult(String id) {
        connection db = new connection(this, "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();

        Cursor fila = baseDatos.rawQuery("SELECT * FROM user WHERE id = '" + id + "'", null);

        if (fila.moveToFirst()) {
            txtIdUserRoutes.setText(fila.getString(0));
            txtNameUserRoutes.setText(fila.getString(1));
            txtLastNameUserRoutes.setText(fila.getString(2));

            byte[] blob = fila.getBlob(3);
            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            Util util = new Util();
            Bitmap finatBtmp = util.getCroppedBitmap(bmp);
            ImageView image = new ImageView(this);
            avatarUserRoutes.setImageBitmap(finatBtmp);

        } else {
            Toast.makeText(this, "No hay registro.", Toast.LENGTH_SHORT).show();
        }
        baseDatos.close();

    }

    //agregar vistas al recyclerView
    private void loadRoutes(String idUser) {
        connection db = new connection(this, "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();
        if (db != null) {
            Cursor fila = baseDatos.rawQuery("SELECT * FROM route WHERE idUser = '" + idUser + "'  ORDER BY id DESC", null);
            int i = 0;
            if (fila.moveToFirst()) {
                do {
                    String id = fila.getString(0);
                    String city = fila.getString(1);
                    String lat = fila.getString(2);
                    String lng = fila.getString(3);
                    String idUser_ = fila.getString(4);
                    listRoutes.add(new RouteVo(id, city, lat, lng, idUser_));
                    i++;
                } while (fila.moveToNext());
            }
        }
    }

    private void init() {
        avatarUserRoutes = (ImageView) findViewById(R.id.avatarUserRoutes);
        txtNameUserRoutes = (TextView) findViewById(R.id.txtNameUserRoutes);
        txtLastNameUserRoutes = (TextView) findViewById(R.id.txtLastNameUserRoutes);
        txtIdUserRoutes = (TextView) findViewById(R.id.txtIdUserRoutes);

        txtInputNameCity = (TextInputLayout) findViewById(R.id.txtInputNameCity);
        txtEditNameCity = (TextInputEditText) findViewById(R.id.txtEditNameCity);

        txtLat = (TextView) findViewById(R.id.txtLat);
        txtLng = (TextView) findViewById(R.id.txtLng);
    }

    private void clean() {
        txtInputNameCity.setError(null);
        txtEditNameCity.setText("");
    }
}
