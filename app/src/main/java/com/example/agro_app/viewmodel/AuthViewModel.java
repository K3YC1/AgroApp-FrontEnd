package com.example.agro_app.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.agro_app.retrofit.AgroCliente;
import com.example.agro_app.retrofit.AgroServicio;
import com.example.agro_app.retrofit.request.LoginRequest;
import com.example.agro_app.retrofit.response.LoginResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private AgroServicio agroServicio;
    public MutableLiveData<Response<LoginResponse>> loginResponseMutableLiveData
            = new MutableLiveData<>();

    public MutableLiveData<Response<ResponseBody>> registroResponseMutableLiveData
            = new MutableLiveData<>();


    public MutableLiveData<Response<ResponseBody>> confirmacionResponseMutableLiveData
            = new MutableLiveData<>();

    public MutableLiveData<Response<ResponseBody>> recuperarContraseñaResponseMutableLiveData
            = new MutableLiveData<>();

    public MutableLiveData<Response<ResponseBody>> confirmarCambioContraseñaResponseMutableLiveData
            = new MutableLiveData<>();

    private MutableLiveData<Boolean> cerrarSesionLiveData
            = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        agroServicio = new AgroCliente().getInstance();
    }

    //Metodo para Authenticar Usuario
    public void authenticarUsuario(LoginRequest loginRequest) {
        new AgroCliente().getInstance().login(loginRequest)
                .enqueue((new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        loginResponseMutableLiveData.setValue(response);
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                }));
    }

    // Método para registrar usuario
    public void registrarUsuario(String nombreUsuario, String correo, String contraseña) {
        new AgroCliente().getInstance().registerNewUser(nombreUsuario, correo, contraseña)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            registroResponseMutableLiveData.setValue(response);
                        } else {
                            // Respuesta no exitosa
                            int errorCode = response.code();
                            Log.e("Error", "Respuesta no exitosa: " + errorCode);
                            registroResponseMutableLiveData.setValue(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Manejar el caso de un fallo en la llamada
                        t.printStackTrace();
                    }
                });
    }

    // Método para confirmar usuario con código
    public void confirmarUsuarioConCodigo(String codigo) {
        new AgroCliente().getInstance().confirmUser(codigo)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        confirmacionResponseMutableLiveData.setValue(response);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Manejar el caso de un fallo en la llamada
                        t.printStackTrace();
                    }
                });
    }

    // Método para recuperar contraseña
    public void recuperarContraseña(String correo) {
        agroServicio.recuperarContraseña(correo).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    recuperarContraseñaResponseMutableLiveData.setValue(response);
                } else {
                    // Respuesta no exitosa
                    int errorCode = response.code();
                    Log.e("Error", "Respuesta no exitosa en recuperarContraseña: " + errorCode);
                    recuperarContraseñaResponseMutableLiveData.setValue(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Manejar el caso de un fallo en la llamada
                t.printStackTrace();
            }
        });
    }

    // Método para confirmar cambio de contraseña
    public void confirmarCambioContraseña(String codigo, String nuevaContraseña) {
        agroServicio.confirmarCambioContraseña(codigo, nuevaContraseña).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                confirmarCambioContraseñaResponseMutableLiveData.setValue(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Manejar el caso de un fallo en la llamada
                t.printStackTrace();
            }
        });
    }

    // Método para cerrar sesión
    public void cerrarSesion() {
        new AgroCliente().getInstance().logoutUser()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // La sesión se cerró exitosamente
                            cerrarSesionLiveData.setValue(true);
                        } else {
                            // Hubo un problema al cerrar la sesión
                            // Maneja el error según tus necesidades
                            cerrarSesionLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Hubo un error de red u otro problema
                        // Maneja el error según tus necesidades
                        t.printStackTrace();
                        cerrarSesionLiveData.setValue(false);
                    }
                });
    }

    // LiveData para observar el resultado del cierre de sesión
    public LiveData<Boolean> getCerrarSesionLiveData() {
        return cerrarSesionLiveData;
    }
}
