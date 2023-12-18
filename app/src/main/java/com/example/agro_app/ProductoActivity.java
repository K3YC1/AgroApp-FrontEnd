package com.example.agro_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProductoActivity extends AppCompatActivity {

    private ImageView btnsalir;
    private ImageView carrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        btnsalir = findViewById(R.id.btnsalir);
        carrito = findViewById(R.id.carrito);

        // Asignar un OnClickListener al ImageView del carrito
        btnsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad actual y vuelve a la actividad anterior
                finish();
            }
        });

        // Asignar un OnClickListener al ImageView del carrito
        carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia una nueva actividad para el carrito
                Intent intent = new Intent(ProductoActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        // Obténer los datos del producto seleccionado
        String nombre = getIntent().getStringExtra("nombre");
        double precio = getIntent().getDoubleExtra("precio", 0);
        String descripcion = getIntent().getStringExtra("descripcion");
        String imagen = getIntent().getStringExtra("imagen");

        // Ahora usar estos datos para mostrar los detalles del producto
        TextView tvnombrep = findViewById(R.id.tvnombrep);
        TextView tvpreciop = findViewById(R.id.tvpreciop);
        TextView tvdescripcion = findViewById(R.id.tvdescripcion);
        ImageView ivimagen = findViewById(R.id.ivimagen);

        tvnombrep.setText(nombre);
        tvpreciop.setText("S/ " + String.valueOf(precio));
        tvdescripcion.setText(descripcion);
        Glide.with(this).load(imagen).into(ivimagen);
    }
}