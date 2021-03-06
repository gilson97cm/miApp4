package com.example.apprutas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.apprutas.user.Users;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   private Toolbar toolbar;
   private CardView cardUser;
   private CardView cardMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        //cardView
        cardUser = (CardView) findViewById(R.id.cardUsers);
        cardMap = (CardView) findViewById(R.id.cardMap);

        //permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);

        cardUser.setOnClickListener(this);
        cardMap.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.cardUsers:
                intent = new Intent(MainActivity.this, Users.class);
                Toast.makeText(this,"Usuarios",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cardMap:
                intent = new Intent(MainActivity.this, MapAllRoutes.class);
                Toast.makeText(this,"Mapa",Toast.LENGTH_SHORT).show();
                break;
        }

        if(intent != null){
            startActivity(intent);
           // finish();
        }
    }
}
