package com.example.agro_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CarritoActivity extends AppCompatActivity {

    private ImageView salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        salir = findViewById(R.id.salir);

        // Asigna un OnClickListener al ImageView del carrito
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad actual y vuelve a la actividad anterior
                finish();
            }
        });
    }
}