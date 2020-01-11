package com.example.apprutas.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.apprutas.R;
import com.example.apprutas.adapter.UserAdapter;
import com.example.apprutas.bd.connection;
import com.example.apprutas.entities.UserVo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class Users extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    private MaterialSearchView mMaterialSearchView;
    RecyclerView recyclerViewUsers;
    ArrayList<UserVo> listUsers;
    UserAdapter adapter;

    CardView cardView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarUsers);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addUsers);
        fab.setOnClickListener(this);
        init();
        //recycler adapter
        listUsers = new ArrayList<>();
        recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(listUsers);
        recyclerViewUsers.setAdapter(adapter);
        loadUsers();


    }

    //accion buscar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialSearchView searchViewM = (MaterialSearchView) findViewById(R.id.searchViewDeal);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_user, menu);
        MenuItem menuItem = menu.findItem(R.id.searchUser);
        searchViewM.setMenuItem(menuItem);


        searchViewM.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    ArrayList<UserVo> listaFiltrada = filter(listUsers,newText);
                    adapter.setFilter(listaFiltrada);

                }catch (Exception e){
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
                adapter.setFilter(listUsers);
                return true;
            }
        });

        return true; //
    }

    private ArrayList<UserVo> filter(ArrayList<UserVo> deals, String text){
        ArrayList<UserVo> listaFiltrada =  new ArrayList<UserVo>();
        try {
            text = text.toLowerCase();

            for(UserVo UserVo: deals){
                String deal = UserVo.getLastName().toLowerCase();

                if(deal.contains(text)){
                    listaFiltrada.add(UserVo);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return listaFiltrada;
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.addUsers:
                intent = new Intent(Users.this, frm_add_user.class);
                Toast.makeText(Users.this, "Agregar Usuario", Toast.LENGTH_SHORT).show();
                break;
        }
        if(intent != null){
            startActivity(intent);
        }

    }

    //agregar vistas al recyclerView
    private void loadUsers() {
        connection db = new connection(this, "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();
        if (db != null) {
            Cursor fila = baseDatos.rawQuery("SELECT * FROM user ORDER BY lastName ASC", null);
            int i = 0;
            if (fila.moveToFirst()) {
                do {
                    String id = fila.getString(0);
                    String name = fila.getString(1);
                    String lastName = fila.getString(2);
                    byte[] avatar = fila.getBlob(3);

                    listUsers.add(new UserVo(id, name, lastName, avatar));
                    i++;
                } while (fila.moveToNext());
            }
        }
    }

    private void init(){
        cardView = (CardView) findViewById(R.id.cardView);

    }
}
