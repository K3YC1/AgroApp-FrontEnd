package com.example.agro_app.view.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agro_app.ProductoActivity;
import com.example.agro_app.R;
import com.example.agro_app.db.AgroDatabase;
import com.example.agro_app.db.dao.ContadorUsuariosDao;
import com.example.agro_app.db.dao.ListaDeDeseosDao;
import com.example.agro_app.db.entity.ContadorUsuarios;
import com.example.agro_app.db.entity.ListaDeDeseos;
import com.example.agro_app.retrofit.response.ProductoResponse;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<ProductoResponse> productList;

    public ProductoAdapter(List<ProductoResponse> productList) {
        this.productList = productList;
    }

    public void updateList(List<ProductoResponse> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }

    @Override
    public ProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);

        return new ProductoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductoViewHolder holder, int position) {
        ProductoResponse product = productList.get(position);
        holder.tvnombreproducto.setText(product.getNombre());
        holder.tvprecio.setText("S/ " + String.valueOf(product.getPrecio()));
        // Cargar la imagen en el ImageView utilizando Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImagen())
                .into(holder.ivimagen);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public ProductoResponse getItem(int position) {
        return productList.get(position);
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvnombreproducto;
        public TextView tvprecio;
        public ImageView ivimagen;
        public ImageView ivagregar;
        public ImageView ivdeseo;

        public ProductoViewHolder(View view) {
            super(view);
            tvnombreproducto = (TextView) view.findViewById(R.id.tvnombreproducto);
            tvprecio = (TextView) view.findViewById(R.id.tvprecio);
            ivimagen = (ImageView) view.findViewById(R.id.ivimagen);

            // Agrega un OnClickListener a la vista clickeable
            ivimagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí puedes iniciar la actividad y pasar los datos del producto seleccionado
                    Intent intent = new Intent(v.getContext(), ProductoActivity.class);
                    intent.putExtra("nombre", productList.get(getAdapterPosition()).getNombre());
                    intent.putExtra("precio", productList.get(getAdapterPosition()).getPrecio());
                    intent.putExtra("descripcion", productList.get(getAdapterPosition()).getDescripcion());
                    intent.putExtra("imagen", productList.get(getAdapterPosition()).getImagen());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
