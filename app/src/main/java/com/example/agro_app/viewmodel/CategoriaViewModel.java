package com.example.agro_app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.agro_app.retrofit.AgroCliente;
import com.example.agro_app.retrofit.AgroServicio;
import com.example.agro_app.retrofit.response.CategoriaResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class CategoriaViewModel extends ViewModel {

    private MutableLiveData<List<CategoriaResponse>> listaCategorias = new MutableLiveData<>();
    private AgroServicio agroServicio;

    public CategoriaViewModel() {
        // Obtener la instancia de AgroServicio a través de AgroCliente
        AgroCliente agroCliente = new AgroCliente();
        agroServicio = agroCliente.getInstance();

        obtenerCategorias();
    }

    private void obtenerCategorias() {
        Call<List<CategoriaResponse>> call = agroServicio.getAllCategorias();

        call.enqueue(new Callback<List<CategoriaResponse>>() {
            @Override
            public void onResponse(Call<List<CategoriaResponse>> call, Response<List<CategoriaResponse>> response) {
                if (response.isSuccessful()) {
                    List<CategoriaResponse> categorias = response.body();
                    if (categorias != null && !categorias.isEmpty()) {
                        listaCategorias.setValue(categorias);
                    } else {
                        Log.e("CategoriaViewModel", "La lista de categorías está vacía");
                    }
                } else {
                    // Manejar errores en la respuesta
                    Log.e("CategoriaViewModel", "Error en la respuesta: " + response.message());
                    // Imprimir detalles adicionales si están disponibles
                    if (response.errorBody() != null) {
                        try {
                            Log.e("CategoriaViewModel", "Detalles del error: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CategoriaResponse>> call, Throwable t) {
                // Manejar errores en la solicitud
                t.printStackTrace();
            }
        });
    }

    public LiveData<List<CategoriaResponse>> getListaCategorias() {
        return listaCategorias;
    }
}
