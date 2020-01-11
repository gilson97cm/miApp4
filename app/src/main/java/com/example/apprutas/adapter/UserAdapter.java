package com.example.apprutas.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.apprutas.R;
import com.example.apprutas.user.UserRoutes;
import com.example.apprutas.user.Users;
import com.example.apprutas.Util;
import com.example.apprutas.bd.connection;
import com.example.apprutas.entities.UserVo;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserVo> mDataset; //ArrayList<UserVo> mDataset;
    private List<UserVo> mDatasetFull;

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserAdapter(List<UserVo> myDataset) { // public UserAdapter(ArrayList<UserVo> myDataset) {
        this.mDataset = myDataset;
        this.mDatasetFull = new ArrayList<>(mDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // creamos una vista nueva
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users_layout, null, false);
        return new UserViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        //asignamos los datos al cardView
        holder.txtIdUser.setText(mDataset.get(position).getId());
        holder.txtNameUser.setText(mDataset.get(position).getName());
        holder.txtLastNameUser.setText(mDataset.get(position).getLastName());

        //imagen
        byte[] avatarUser = mDataset.get(position).getAvatar();
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatarUser, 0, avatarUser.length);
        Util util = new Util();
        Bitmap finalBtmp = util.getCroppedBitmap(bitmap);
        holder.avatarView.setImageBitmap(finalBtmp);

        //contar el numero de rutas
        connection db = new connection(holder.context , "bdRoutes", null, 1);
        SQLiteDatabase baseDatos = db.getWritableDatabase();
        Cursor fila = baseDatos.rawQuery("SELECT * FROM route WHERE idUser = '" + mDataset.get(position).getId() + "'", null);
        Integer c = fila.getCount();
        baseDatos.close();
        holder.txtNumRoutes.setText(""+c+" Lugar(es)");

        //botones
        holder.setOnClickListeners();


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //filtrar usuarios
    public void setFilter(ArrayList<UserVo> dealList_) {
        this.mDataset = new ArrayList<>();
        this.mDataset.addAll(dealList_);
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;

        // dentro de esta clase hay que hacer referencia a los items que aparezcan en nuestro item (elemento de la lista)

        TextView txtIdUser;
        TextView txtNameUser;
        TextView txtLastNameUser;
        ImageView avatarView;
        TextView txtNumRoutes;

        //hacemos referencia a los botones para asignar eventos
        Button btnViewRoutes;
        Button btnDestroyUser;

        UserViewHolder(View v) {
            super(v);
            context = v.getContext();

            txtIdUser = (TextView) v.findViewById(R.id.txtIdUser);
            txtNameUser = (TextView) v.findViewById(R.id.txtNameUser);
            txtLastNameUser = (TextView) v.findViewById(R.id.txtLastNameUser);
            avatarView = (ImageView) v.findViewById(R.id.avatarView);
            txtNumRoutes = (TextView) v.findViewById(R.id.txtNumRoutes);


            //botones
            btnViewRoutes = (Button) v.findViewById(R.id.btnViewRoutes);
            btnDestroyUser = (Button) v.findViewById(R.id.btnDestroyUser);
        }

        void setOnClickListeners() {
            btnViewRoutes.setOnClickListener(this);
            btnDestroyUser.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String id = txtIdUser.getText().toString();
            String name = txtNameUser.getText().toString();
            String lastName = txtLastNameUser.getText().toString();
            switch (v.getId()) {
                case R.id.btnViewRoutes:
                    Intent intent = new Intent(context, UserRoutes.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //enviamos el id y el nombre de una actividad a otra
                    intent.putExtra("id", id);
                    Toast.makeText(context, "Usuario: " + name, Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                    break;
                case R.id.btnDestroyUser:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("¿Eliminar Usuario?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String id = txtIdUser.getText().toString();
                            String name = txtNameUser.getText().toString();

                            connection db = new connection(context, "bdRoutes", null, 1);
                            SQLiteDatabase baseDatos = db.getWritableDatabase();

                            if (!id.equals("")) {
                                Cursor fila = baseDatos.rawQuery("SELECT * FROM user WHERE id = '" + id + "'", null);
                                if (fila.getCount() <= 0) {
                                    Toast.makeText(context, "Nada para eliminar.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Cursor route = baseDatos.rawQuery("SELECT * FROM route WHERE idUser = '" + id + "'", null);
                                    if (route.getCount() <= 0) {
                                        Toast.makeText(context, "usuario sin rutas.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        baseDatos.delete("route", "idUser = '" + id + "'", null);
                                        Toast.makeText(context, "Se elimino rutas de: " + name, Toast.LENGTH_SHORT).show();
                                    }
                                    baseDatos.delete("user", "id = '" + id + "'", null);
                                    baseDatos.close();
                                    Toast.makeText(context, "Se elimino: " + name, Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(context, Users.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent1);
                                }
                            } else {
                                Toast.makeText(context, "Nada para eliminar.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
            }
        }
    }

}
