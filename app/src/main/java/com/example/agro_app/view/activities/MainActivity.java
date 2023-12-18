package com.example.agro_app.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.agro_app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAcceder = findViewById(R.id.btnIniciarSesion);

        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configurar un Intent para abrir OtraActividad
                Intent intent = new Intent(MainActivity.this, SesionActivity.class);
                startActivity(intent);
            }
        });

        // Verificar el estado de la sesión al lanzar la aplicación
        if (usuarioHaIniciadoSesion()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Cierra la actividad actual para que no vuelva atrás al presionar el botón de retroceso.
            return;
        }
    }

    private boolean usuarioHaIniciadoSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
        return sharedPreferences.getBoolean("sesion_iniciada", false);
    }
}