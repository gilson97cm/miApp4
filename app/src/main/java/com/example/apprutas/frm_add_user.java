package com.example.apprutas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apprutas.bd.connection;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class frm_add_user extends AppCompatActivity {
    final int REQUEST_CODE_GALLERY = 999;
    Toolbar toolbar;
    TextInputLayout txtInputId;
    TextInputEditText txtEditId;

    TextInputLayout txtInputName;
    TextInputEditText txtEditName;

    TextInputLayout txtInputLastName;
    TextInputEditText txtEditLastName;

    ImageView avatarViewFrm;
    Button btnChoseImgDeal;
    Button btnImgDef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_add_user);
        init();

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbarAddUsers);
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

    }

    //visto para registrar tiendas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_user_frm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveUser) {
            insertUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //insertar datos en la bbdd
    private void insertUser() {
        connection db = new connection(this, "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();
        String id = txtEditId.getText().toString();
        String name = txtEditName.getText().toString();
        String lastName = txtEditLastName.getText().toString();
        byte[] avatarUser = ImageViewToByte(avatarViewFrm);

        if ((!id.isEmpty()) && (!name.isEmpty()) && (!lastName.isEmpty())) {
            ContentValues registro = new ContentValues();
            Cursor fila = baseDatos.rawQuery("SELECT * FROM user WHERE id = '"+id+"'",null);

            if (fila.getCount() <= 0) {
                registro.put("id", id);
                registro.put("name", name);
                registro.put("lastName", lastName);
                registro.put("avatar", avatarUser);

                baseDatos.insert("user", null, registro);
                baseDatos.close();

                // clean();
                Toast.makeText(this, "Usuario registrada con exito.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Users.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();
            } else {
                Toast.makeText(this, "el Código ya existe", Toast.LENGTH_SHORT).show();
            }
        } else {
            txtInputId.setError(" ");
            txtInputName.setError(" ");
            txtInputLastName.setError(" ");
            txtEditId.requestFocus();
            Toast.makeText(this, "Hay campos vacios.", Toast.LENGTH_SHORT).show();
        }

    }

    //de imagen a byte
    private byte[] ImageViewToByte(ImageView avatarUser) {
        Bitmap bitmap = ((BitmapDrawable) avatarUser.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //cambiar el logo
    public void choseImg(View v) {
        ActivityCompat.requestPermissions(frm_add_user.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
    }
    //poner el logo por defecto
    public void imgDefault(View v){
        avatarViewFrm.setImageResource(R.drawable.ic_default);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Sin acceso.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                avatarViewFrm.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private  void init(){
        txtInputId = (TextInputLayout) findViewById(R.id.txtInputId);
        txtEditId = (TextInputEditText) findViewById(R.id.txtEditId);

        txtInputName = (TextInputLayout) findViewById(R.id.txtInputName);
        txtEditName = (TextInputEditText) findViewById(R.id.txtEditName);

        txtInputLastName = (TextInputLayout) findViewById(R.id.txtInputLastName);
        txtEditLastName = (TextInputEditText) findViewById(R.id.txtEditLastName);

        avatarViewFrm = (ImageView) findViewById(R.id.avatarViewFrm);
        btnChoseImgDeal = (Button) findViewById(R.id.btnChoseImgDeal);
        btnImgDef = (Button) findViewById(R.id.btnImgDef);

    }
}
