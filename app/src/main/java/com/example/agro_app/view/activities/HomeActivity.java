package com.example.agro_app.view.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.agro_app.CarritoActivity;
import com.example.agro_app.R;
import com.example.agro_app.retrofit.AgroCliente;
import com.example.agro_app.retrofit.AgroServicio;
import com.example.agro_app.retrofit.response.CategoriaResponse;
import com.example.agro_app.retrofit.response.ProductoResponse;
import com.example.agro_app.view.Adapters.ProductoAdapter;
import com.example.agro_app.viewmodel.AuthViewModel;
import com.example.agro_app.viewmodel.CategoriaViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ProductoAdapter productoAdapter;

    private DrawerLayout drawer_Layout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView barrasMenu;
    private ImageView home1;
    private ImageView hearth1;
    private ImageView rotate1;
    private ImageView carrito;

    private LinearLayout catalogoLayout;
    private LinearLayout listaDeseosLayout;
    private LinearLayout comprasRecientesLayout;

    private ImageView ultimoImageViewSeleccionado;
    private AuthViewModel authViewModel;

    private CategoriaViewModel categoriaViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home1 = findViewById(R.id.home1);
        hearth1 = findViewById(R.id.hearth1);
        rotate1 = findViewById(R.id.rotate1);
        carrito = findViewById(R.id.carrito);

        // Inicializar vistas
        drawer_Layout = findViewById(R.id.drawerLayout);
        barrasMenu = findViewById(R.id.barrasmenu);

        catalogoLayout = findViewById(R.id.Catalogo);
        listaDeseosLayout = findViewById(R.id.ListadeDeseos);
        comprasRecientesLayout = findViewById(R.id.ComprasRecientes);

        // Establece el color verde personalizado en ImageView10 al inicio
        setGreenColor(home1);

        // Muestra el LinearLayout asociado a Catalogo al inicio
        catalogoLayout.setVisibility(View.VISIBLE);
        listaDeseosLayout.setVisibility(View.GONE);
        comprasRecientesLayout.setVisibility(View.GONE);

        // Asigna un OnClickListener a cada ImageView
        home1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageViewClick(home1, catalogoLayout);
            }
        });

        hearth1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageViewClick(hearth1, listaDeseosLayout);
            }
        });

        rotate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageViewClick(rotate1, comprasRecientesLayout);
            }
        });

        // Asigna un OnClickListener al ImageView del carrito
        carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia una nueva actividad para el carrito
                Intent intent = new Intent(HomeActivity.this, CarritoActivity.class);
                startActivity(intent);
            }
        });

        // Configurar ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_Layout, R.string.open, R.string.close);
        drawer_Layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Configurar clic en el ImageView para mostrar/ocultar el menú
        barrasMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_Layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_Layout.closeDrawer(GravityCompat.START);
                } else {
                    drawer_Layout.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0); // Obtener la vista del encabezado
        Button btnCerrarSesion = headerView.findViewById(R.id.btnCerrarSesion);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        // Inicializar ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observar el resultado del cierre de sesión
        authViewModel.getCerrarSesionLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean exitoso) {
                if (exitoso) {
                    // El cierre de sesión fue exitoso
                    // Redirigir a la pantalla de inicio de sesión
                    Intent intent = new Intent(HomeActivity.this, SesionActivity.class);
                    startActivity(intent);
                    finish(); // Esto evita que el usuario pueda regresar presionando el botón "Atrás"
                } else {
                    // Hubo un problema al cerrar la sesión
                    // Muestra un Snackbar con el mensaje de error
                    mostrarSnackbar("Error al cerrar sesión");
                }
            }
        });

        // Obtener datos del Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("nombreUsuario") && intent.hasExtra("correo")) {
            String nombreUsuario = intent.getStringExtra("nombreUsuario");
            String correo = intent.getStringExtra("correo");

            // Ahora, puedes usar esta información para actualizar el nav_header_main
            actualizarNavHeader(nombreUsuario, correo);
        }else {
            // Si no hay datos en el Intent, intenta obtenerlos de SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
            String nombreUsuario = sharedPreferences.getString("nombreUsuario", "");
            String correo = sharedPreferences.getString("correo", "");

            // Actualizar el nav_header_main con la información recuperada
            actualizarNavHeader(nombreUsuario, correo);
        }

        // Inicializar ViewModel
        categoriaViewModel = new ViewModelProvider(this).get(CategoriaViewModel.class);

        // Observar el cambio en la lista de categorías
        categoriaViewModel.getListaCategorias().observe(this, categorias -> mostrarCategorias(categorias));

        // Inicializar productoAdapter
        RecyclerView recyclerView = findViewById(R.id.rvProductos);
        productoAdapter = new ProductoAdapter(new ArrayList<>());
        recyclerView.setAdapter(productoAdapter);

        // Establecer el LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Obtener la instancia de AgroServicio
        AgroServicio agroServicio = new AgroCliente().getInstance();

        // Llamar al endpoint para obtener los productos de la primera categoría
        Call<List<ProductoResponse>> call = agroServicio.getProductosByCategoria(1); //Obtener la ID de la primera categoría
        call.enqueue(new Callback<List<ProductoResponse>>() {
            @Override
            public void onResponse(Call<List<ProductoResponse>> call, Response<List<ProductoResponse>> response) {
                if (response.isSuccessful()) {
                    List<ProductoResponse> productos = response.body();
                    productoAdapter.updateList(productos);
                }
            }

            @Override
            public void onFailure(Call<List<ProductoResponse>> call, Throwable t) {
                // Manejar el error
                t.printStackTrace();
            }
        });
    }

    private void actualizarNavHeader(String nombreUsuario, String correo) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        // Obtén referencias a los TextView en el nav_header_main
        TextView tvUser = headerView.findViewById(R.id.tvUser);
        TextView tvCorreo = headerView.findViewById(R.id.tvCorreo);

        // Establece los textos
        tvUser.setText(nombreUsuario);
        tvCorreo.setText(correo);

        // Guardar información en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nombreUsuario", nombreUsuario);
        editor.putString("correo", correo);
        editor.apply();

    }

    // Método para cerrar sesión
    private void cerrarSesion() {
        authViewModel.cerrarSesion();

        // Actualiza SharedPreferences para reflejar que el usuario no está en sesión
        SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sesion_iniciada", false);
        editor.apply();

        // Finaliza la actividad actual
        finish();
    }

    // Método para mostrar un Snackbar
    private void mostrarSnackbar(String mensaje) {
        View view = findViewById(android.R.id.content);
        Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT).show();
    }

    private void setGreenColor(ImageView imageView) {
        // Obtén el color personalizado desde colors.xml
        int colorVerdePersonalizado = ContextCompat.getColor(this, R.color.agro_verde);

        // Establece el color verde personalizado en el ImageView
        imageView.setColorFilter(colorVerdePersonalizado);

        // Guarda el ImageView actual como el último seleccionado
        ultimoImageViewSeleccionado = imageView;
    }

    private void onImageViewClick(ImageView imageView, LinearLayout targetLinearLayout) {
        // Restaura el color del último ImageView seleccionado
        if (ultimoImageViewSeleccionado != null) {
            ultimoImageViewSeleccionado.clearColorFilter();
        }

        // Guarda el ImageView actual como el último seleccionado
        ultimoImageViewSeleccionado = imageView;

        // Obtén el color personalizado desde colors.xml
        int colorVerdePersonalizado = ContextCompat.getColor(this, R.color.agro_verde);

        // Cambia el color del ImageView al color personalizado
        imageView.setColorFilter(colorVerdePersonalizado);

        // Cambia la visibilidad de los LinearLayout según el ImageView seleccionado
        catalogoLayout.setVisibility(imageView == home1 ? View.VISIBLE : View.GONE);
        listaDeseosLayout.setVisibility(imageView == hearth1 ? View.VISIBLE : View.GONE);
        comprasRecientesLayout.setVisibility(imageView == rotate1 ? View.VISIBLE : View.GONE);
    }

    // Método para verificar si el usuario ha iniciado sesión
    private boolean usuarioHaIniciadoSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
        return sharedPreferences.getBoolean("sesion_iniciada", false);
    }

    private boolean usuarioHaCerradoSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
        return !sharedPreferences.getBoolean("sesion_iniciada", false);
    }

    @Override
    public void onBackPressed() {
        // Verificar si el usuario ha iniciado sesión
        if (usuarioHaIniciadoSesion()) {
            // Si el usuario ha iniciado sesión, salir de la aplicación
            finishAffinity(); // Cierra todas las actividades de la aplicación
        } else if (usuarioHaCerradoSesion()) {
            // Si el usuario no ha iniciado sesión, comportamiento normal del botón de retroceso
            super.onBackPressed();
        }
    }

    private void mostrarCategorias(List<CategoriaResponse> categorias) {
        // Obtener el contenedor de texto dinámico en tu layout
        LinearLayout contenedorCategorias = findViewById(R.id.contenedor_categorias);  // Ajusta según tu layout

        // Limpiar el contenedor
        contenedorCategorias.removeAllViews();

        // Crear dinámicamente TextView para cada categoría
        for (int i = 0; i < categorias.size(); i++) {
            // Crear un nuevo TextView
            TextView textView = new TextView(this);

            // Establecer el texto con el nombre de la categoría
            textView.setText(categorias.get(i).getNombreCategoria());

            // Configurar el tipo de letra (fuente)
            Typeface typeface = ResourcesCompat.getFont(this, R.font.nunito_bold);
            textView.setTypeface(typeface);

            // Configurar el margen
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(40, 0, 40, 0); // Izquierda, arriba, derecha, abajo
            textView.setLayoutParams(layoutParams);

            // Cambiar el color del texto al ser seleccionado o si es el primer TextView
            if (i == 0) {
                textView.setTextColor(getResources().getColor(R.color.agro_verde));
            } else {
                textView.setTextColor(getResources().getColor(R.color.colorcategorias));
            }

            final int index = i;
            // Agregar un OnClickListener para manejar la selección del TextView
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Cambiar el color del texto al ser seleccionado
                    textView.setTextColor(getResources().getColor(R.color.agro_verde));

                    // Restablecer el color de los otros TextViews
                    for (int j = 0; j < contenedorCategorias.getChildCount(); j++) {
                        View childView = contenedorCategorias.getChildAt(j);
                        if (childView instanceof TextView && childView != view) {
                            ((TextView) childView).setTextColor(getResources().getColor(R.color.colorcategorias));
                        }
                    }

                    // Obtener la ID de la categoría seleccionada
                    int categoriaId = categorias.get(index).getCategoriaId();

                    // Obtener la instancia de AgroServicio
                    AgroServicio agroServicio = new AgroCliente().getInstance();

                    // Llamar al endpoint para obtener los productos de la categoría seleccionada
                    Call<List<ProductoResponse>> call = agroServicio.getProductosByCategoria(categoriaId);
                    call.enqueue(new Callback<List<ProductoResponse>>() {
                        @Override
                        public void onResponse(Call<List<ProductoResponse>> call, Response<List<ProductoResponse>> response) {
                            if (response.isSuccessful()) {
                                List<ProductoResponse> productos = response.body();
                                productoAdapter.updateList(productos);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ProductoResponse>> call, Throwable t) {
                            // Manejar el error
                            t.printStackTrace();
                        }
                    });

                }
            });

            // Agregar TextView al LinearLayout
            contenedorCategorias.addView(textView);
        }
    }


}
