package com.example.agro_app.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgroCliente  {

    private static final String BASE_URL = "http://192.168.1.2:9080/api/usuarios/";
    private AgroServicio agroServicio;

    public AgroCliente(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        agroServicio = retrofit.create(AgroServicio.class);
    }
    public AgroServicio getInstance(){
        return agroServicio;
    }
}
