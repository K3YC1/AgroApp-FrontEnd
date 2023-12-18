package com.example.agro_app.view.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.agro_app.R;
import com.example.agro_app.databinding.ActivitySesionBinding;
import com.example.agro_app.retrofit.request.LoginRequest;
import com.example.agro_app.retrofit.response.LoginResponse;
import com.example.agro_app.viewmodel.AuthViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class SesionActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySesionBinding binding;
    private AuthViewModel authViewModel;
    private SwitchCompat switchButton;
    private LinearLayout layoutOn, layoutOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.btnIniciarSesion.setOnClickListener(this);
        binding.btnRegistrarse.setOnClickListener(this);
        // Asigna el clic al TextView
        TextView textViewOlvidarContraseña = findViewById(R.id.textViewOlvidarContraseña);
        textViewOlvidarContraseña.setOnClickListener(this);

        switchButton = findViewById(R.id.switchButton);
        layoutOn = findViewById(R.id.layoutOn);
        layoutOff = findViewById(R.id.layoutOff);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    layoutOn.setVisibility(View.VISIBLE);
                    layoutOff.setVisibility(View.GONE);

                } else {

                    layoutOn.setVisibility(View.GONE);
                    layoutOff.setVisibility(View.VISIBLE);
                }
            }
        });

        authViewModel.loginResponseMutableLiveData
                .observe(this, new Observer<Response<LoginResponse>>() {
                    @Override
                    public void onChanged(Response<LoginResponse> response) {
                        validarLogin(response);
                    }
                });

        authViewModel.registroResponseMutableLiveData
                .observe(this, new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onChanged(Response<ResponseBody> response) {
                        validarRegistro(response);
                    }
                });

        authViewModel.confirmarCambioContraseñaResponseMutableLiveData
                .observe(this, new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onChanged(Response<ResponseBody> response) {
                        validarConfirmacionCambioContrasena(response);
                    }
                });

        authViewModel.recuperarContraseñaResponseMutableLiveData.observe(this, new Observer<Response<ResponseBody>>() {
            @Override
            public void onChanged(Response<ResponseBody> response) {
                validarRecuperacionContrasena(response);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnIniciarSesion) {
            invocarLogin();
        } else if (view.getId() == R.id.btnRegistrarse) {
            invocarRegistro();
        }
        // Agrega la lógica para mostrar el cuadro de diálogo de recuperación de contraseña
        if (view.getId() == R.id.textViewOlvidarContraseña) {
            mostrarDialogoRecuperacionContraseña();
        }
    }

    public void invocarRegistro() {
        authViewModel.registrarUsuario(
                binding.etUsername.getText().toString(),
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString()
        );
    }

    public void invocarLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setCorreo(binding.ettEmailAddress.getText().toString());
        loginRequest.setContraseña(binding.ettPassword.getText().toString());
        authViewModel.authenticarUsuario(loginRequest);
    }


    public void validarLogin(Response<LoginResponse> response) {
        if (response.isSuccessful()) {
            LoginResponse loginResponse = response.body();

            if (loginResponse != null) {
                // La respuesta fue exitosa y hay datos en el cuerpo de la respuesta
                Log.d("LoginResponse", loginResponse.toString());

                // Verificar si el usuario está confirmado
                if (loginResponse.isConfirmado()) {

                    // Después de un inicio de sesión exitoso en SesionActivity
                    SharedPreferences sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("sesion_iniciada", true);
                    editor.putInt("id_usuario", loginResponse.getId_usuario()); // Almacenar el id_usuario
                    editor.apply();

                    // Crear un Intent para pasar datos a HomeActivity
                    Intent intent = new Intent(SesionActivity.this, HomeActivity.class);

                    // Agregar datos del usuario al Intent
                    intent.putExtra("nombreUsuario", loginResponse.getNombreUsuario());
                    intent.putExtra("correo", loginResponse.getCorreo());

                    // Iniciar HomeActivity con el Intent
                    startActivity(intent);
                }
            } else {
                // El cuerpo de la respuesta está vacío, mostrar mensaje de error
                Log.e("Error", "El cuerpo de la respuesta está vacío");
                mostrarSnackbar("No se pudo iniciar Sesión");
            }
        } else {
            // Algo salió mal, mostramos un mensaje de error basado en el código de estado
            String mensajeError = "No se pudo iniciar Sesión";

            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                mensajeError = "El usuario no esta registrado!";
            } else if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                mensajeError = "Contraseña incorrecta!";
            }

            Log.e("Error", mensajeError);

            mostrarSnackbar(mensajeError);
        }
    }

    private void mostrarSnackbar(String mensaje) {
        Snackbar.make(binding.getRoot(), mensaje, Snackbar.LENGTH_SHORT).show();
    }

    // Método de registro con código
    private void validarRegistro(Response<ResponseBody> response) {
        if (response != null) {
            if (response.isSuccessful()) {
                // El registro fue exitoso
                Log.d("RegistroExitoso", "Registro exitoso. Mensaje del servidor: " + response.body());
                // Mostrar un mensaje de éxito
                mostrarSnackbar("Por favor, verifica tu correo para obtener el codigo y validar tu cuenta!");

                mostrarCuadroConfirmacion();
                // También puedes redirigir a la pantalla de inicio de sesión o hacer alguna otra acción
            } else {
                // Algo salió mal durante el registro, manejar el error según el código de estado
                String mensajeError = "Error en el registro";

                if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                    mensajeError = "Por favor, complete todos los datos";
                } else if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    mensajeError = "Por favor, introduzca un correo válido";
                } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                    mensajeError = "El correo ya está registrado. Por favor, use otro correo";
                } else if (response.code() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    mensajeError = "Error interno del servidor";
                }

                Log.e("Error", mensajeError);

                // Muestra un Snackbar o algún otro tipo de mensaje para informar al usuario
                mostrarSnackbar(mensajeError);
            }
        } else {
            // La respuesta es nula, manejar este escenario según tus necesidades
            Log.e("Error", "La respuesta de registro es nula");
            mostrarSnackbar("Error en la respuesta de registro");
        }
    }

    // Método para manejar el registro exitoso y mostrar el cuadro de confirmación
    private void mostrarCuadroConfirmacion() {
        // Inflar el diseño del cuadro de diálogo personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.cuadro_confirmacion, null);

        // Configurar elementos dentro del cuadro de diálogo
        EditText editCode = dialogView.findViewById(R.id.edit_code);
        Button btnConfirmar = dialogView.findViewById(R.id.btn_confirm);
        TextView txtMessage = dialogView.findViewById(R.id.txt_message);
        txtMessage.setText(R.string.txtTituloV);  // Configurar el mensaje según sea necesario

        // Crear un cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        Dialog dialog = builder.create();

        // Configurar el botón de confirmación
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes manejar la lógica de confirmación del código
                String codigo = editCode.getText().toString();
                confirmarCodigo(codigo, dialog);
            }
        });

        // Para que el cuadro de diálogo no sea cancelable al tocar fuera de él
        dialog.setCanceledOnTouchOutside(false);


        // Mostrar el cuadro de diálogo
        dialog.show();
    }

    // Método para limpiar los campos de registro
    private void limpiarCamposRegistro() {
        binding.etUsername.getText().clear();
        binding.etEmail.getText().clear();
        binding.etPassword.getText().clear();
    }

    // Método para confirmar el código utilizando Retrofit
    private void confirmarCodigo(String codigo, Dialog dialog) {
        AuthViewModel authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.confirmarUsuarioConCodigo(codigo);
        authViewModel.confirmacionResponseMutableLiveData.observe(this, new Observer<Response<ResponseBody>>() {
            @Override
            public void onChanged(Response<ResponseBody> response) {
                // Manejar la respuesta del servidor después de confirmar el código
                if (response != null) {
                    if (response.isSuccessful()) {
                        // Confirmación exitosa
                        Log.d("ConfirmacionExitosa", "¡Registro confirmado con éxito!");
                        // Mostrar un mensaje de éxito
                        mostrarSnackbar("¡Registro confirmado con éxito, ahora puedes Iniciar Sesion!");

                        // Cerrar el cuadro de diálogo solo en caso de confirmación exitosa
                        dialog.dismiss();

                        // Limpiar campos después de la confirmación exitosa
                        limpiarCamposRegistro();
                    } else {
                        // Algo salió mal durante la confirmación, manejar el error según el código de estado
                        String mensajeError = "Error en la confirmación";

                        if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                            mensajeError = "El código de verificación no es válido o ha expirado.";
                        } else {
                            mensajeError = "Error desconocido";
                        }

                        Log.e("Error", mensajeError);

                        // Mostrar un Snackbar o algún otro tipo de mensaje para informar al usuario
                        mostrarSnackbar(mensajeError);

                        // No cerrar el cuadro de diálogo en caso de error, para mantenerlo abierto hasta que la confirmación sea exitosa
                    }
                }
            }
        });
    }

    //Recuperacion de Contraseña

    private AlertDialog dialogRecuperacionContrasena;
    private void mostrarDialogoRecuperacionContraseña() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");

        // Inflar el diseño del cuadro de diálogo personalizado
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_recuperar_contrasena, null);
        final EditText correoEditText = view.findViewById(R.id.correoEditText);
        Button btnEnviarCorreo = view.findViewById(R.id.btnEnviarCorreo);

        builder.setView(view);

        // Configurar el botón de enviar correo
        btnEnviarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = correoEditText.getText().toString().trim();
                if (!correo.isEmpty()) {
                    // Llamar al método para recuperar contraseña
                    authViewModel.recuperarContraseña(correo);
                } else {
                    mostrarSnackbar("Ingrese un correo válido");
                }
            }
        });

        // Configurar el botón negativo (o cancelar)
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Solo se cierra el cuadro de diálogo
            }
        });
        // Crear y mostrar el cuadro de diálogo
        dialogRecuperacionContrasena = builder.create();
        dialogRecuperacionContrasena.show();


    }

    private void validarRecuperacionContrasena(Response<ResponseBody> response) {
        if (response != null) {
            if (response.isSuccessful()) {
                // La recuperación fue exitosa
                Log.d("RecuperacionExitosa", "Recuperación exitosa. Mensaje del servidor: " + response.body());
                // Mostrar un mensaje de éxito
                mostrarSnackbar("Se ha enviado un correo con el código de recuperación.");

                // Cerrar el cuadro de diálogo de recuperación de contraseña si está mostrándose
                if (dialogRecuperacionContrasena != null && dialogRecuperacionContrasena.isShowing()) {
                    dialogRecuperacionContrasena.dismiss();
                }

                // Mostrar el cuadro de diálogo de confirmación de cambio de contraseña
                mostrarDialogoConfirmacionCambioContrasena();
            } else {
                // Algo salió mal durante la recuperación, manejar el error según el código de estado
                String mensajeError = "Error en la recuperación de contraseña";

                try {
                    // Intenta obtener el cuerpo de la respuesta
                    mensajeError = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Maneja la excepción aquí, ya sea mostrando un mensaje de error genérico o tomando otra acción.
                    mensajeError = "Error al leer el cuerpo de la respuesta.";
                }

                // Mostrar un Snackbar o algún otro tipo de mensaje para informar al usuario
                mostrarSnackbar(mensajeError);
            }
        } else {
            // La respuesta es nula, manejar este escenario según tus necesidades
            Log.e("Error", "La respuesta de recuperación de contraseña es nula");
            mostrarSnackbar("Error en la respuesta de recuperación de contraseña");
        }
    }

    private AlertDialog dialogConfirmacionCambioContrasena;
    private void mostrarDialogoConfirmacionCambioContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setTitle("Confirmar Cambio de Contraseña");

        // Inflar el diseño del cuadro de diálogo personalizado
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirmar_cambio_contrasena, null);
        final EditText codigoEditText = view.findViewById(R.id.codigoEditText);
        final EditText nuevaContraseñaEditText = view.findViewById(R.id.nuevaContraseñaEditText);
        Button btnConfirmarCambio = view.findViewById(R.id.btnConfirmarCambio);

        builder.setView(view);

        // Configurar el botón de confirmar cambio
        btnConfirmarCambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = codigoEditText.getText().toString().trim();
                String nuevaContraseña = nuevaContraseñaEditText.getText().toString().trim();

                if (!codigo.isEmpty() && !nuevaContraseña.isEmpty()) {
                    // Llamar al método para confirmar el cambio de contraseña
                    authViewModel.confirmarCambioContraseña(codigo, nuevaContraseña);
                } else {
                    mostrarSnackbar("Complete todos los campos");
                }
                // Cerrar el cuadro de diálogo después de hacer clic en el botón
                if (dialogConfirmacionCambioContrasena != null && dialogConfirmacionCambioContrasena.isShowing()) {
                    dialogConfirmacionCambioContrasena.dismiss();
                }
            }
        });

        // Configurar el botón negativo (o cancelar)
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No necesitas hacer nada aquí, ya que solo estás cerrando el cuadro de diálogo
            }
        });

        dialogConfirmacionCambioContrasena = builder.create();
        dialogConfirmacionCambioContrasena.show();
    }

    private void validarConfirmacionCambioContrasena(Response<ResponseBody> response) {
        if (response != null) {
            if (response.isSuccessful()) {
                // Confirmación exitosa
                Log.d("ConfirmacionExitosa", "¡Cambio de contraseña confirmado con éxito!");

                // Mostrar un mensaje de éxito
                mostrarSnackbar("¡Cambio de contraseña confirmado con éxito!");

                // Cerrar el cuadro de diálogo de recuperación de contraseña si está mostrándose
                if (dialogConfirmacionCambioContrasena != null && dialogConfirmacionCambioContrasena.isShowing()) {
                    dialogConfirmacionCambioContrasena.dismiss();
                }
                // Puedes agregar aquí la lógica adicional después de la confirmación exitosa, como redirigir a una nueva actividad, etc.
            } else {
                // Algo salió mal durante la confirmación, manejar el error según el código de estado
                String mensajeError = "Error en la confirmación de cambio de contraseña";

                if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    mensajeError = "El código de verificación no es válido o ha expirado.";
                } else {
                    mensajeError = "Error desconocido";
                }

                Log.e("Error", mensajeError);

                // Mostrar un Snackbar o algún otro tipo de mensaje para informar al usuario
                mostrarSnackbar(mensajeError);

                // No cerrar el cuadro de diálogo en caso de error, para mantenerlo abierto hasta que la confirmación sea exitosa
            }
        }
    }
}