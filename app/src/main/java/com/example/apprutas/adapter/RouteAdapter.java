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
import com.example.apprutas.UserRoutes;

import com.example.apprutas.bd.connection;
import com.example.apprutas.entities.RouteVo;


import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder>{
    private List<RouteVo> mDataset; //ArrayList<UserVo> mDataset;
    private List<RouteVo> mDatasetFull;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RouteAdapter(List<RouteVo> myDataset) { // public RouteAdapter(ArrayList<UserVo> myDataset) {
        this.mDataset = myDataset;
        this.mDatasetFull = new ArrayList<>(mDataset);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RouteAdapter.RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // creamos una vista nueva
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_routes_layout, null, false);
        return new RouteAdapter.RouteViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RouteAdapter.RouteViewHolder holder, int position) {
        //asignamos los datos al cardView
        holder.txtIdRoute.setText(mDataset.get(position).getId());
        holder.txtCity.setText(mDataset.get(position).getCity());
        holder.txtLat.setText(mDataset.get(position).getLat());
        holder.txtLng.setText(mDataset.get(position).getLng());
        //botones
        holder.setOnClickListeners();
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    //filtrar usuarios
    public void setFilter(ArrayList<RouteVo> dealList_){
        this.mDataset = new ArrayList<>();
        this.mDataset.addAll(dealList_);
        notifyDataSetChanged();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;

        // dentro de esta clase hay que hacer referencia a los items que aparezcan en nuestro item (elemento de la lista)

        TextView txtIdRoute;
        TextView txtCity;
        TextView txtLat;
        TextView txtLng;

        //hacemos referencia a los botones para asignar eventos
        Button btnViewInMap;
        Button btnDestroyRoute;

        RouteViewHolder(View v) {
            super(v);
            context = v.getContext();

            txtIdRoute = (TextView) v.findViewById(R.id.txtIdRoute);
            txtCity = (TextView) v.findViewById(R.id.txtCity);
            txtLat = (TextView) v.findViewById(R.id.txtLatCard);
            txtLng = (TextView) v.findViewById(R.id.txtLngCard);


            //botones
            btnViewInMap = (Button) v.findViewById(R.id.btnViewInMap);
            btnDestroyRoute = (Button) v.findViewById(R.id.btnDestroyRoute);
        }

        void setOnClickListeners() {
            btnViewInMap.setOnClickListener(this);
            btnDestroyRoute.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String id = txtIdRoute.getText().toString();
            String city = txtCity.getText().toString();
            switch (v.getId()) {
                case R.id.btnViewInMap:
                   // Intent intent = new Intent(context, UserRoutes.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //enviamos el id y el nombre de una actividad a otra
                    //intent.putExtra("id", id);
                    Toast.makeText(context, "ver en mapa" , Toast.LENGTH_SHORT).show();
                    //context.startActivity(intent);
                    break;
                case R.id.btnDestroyRoute:

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("¿Eliminar Ruta?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String id = txtIdRoute.getText().toString();
                            String city = txtCity.getText().toString();

                            connection db = new connection(context, "bdRoutes", null, 1);
                            SQLiteDatabase baseDatos = db.getWritableDatabase();

                            if (!id.equals("")) {
                                Cursor fila = baseDatos.rawQuery("SELECT * FROM route WHERE id = '"+id+"'", null);
                                if (fila.getCount() <= 0) {
                                    Toast.makeText(context, "Nada para eliminar.", Toast.LENGTH_SHORT).show();
                                } else {
                                     baseDatos.delete("route", "id = " + id, null);
                                   // baseDatos.delete("user", "id = '"+id+"'", null);
                                    baseDatos.close();
                                    Toast.makeText(context, "Se elimino: " + city, Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(context, UserRoutes.class);
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
