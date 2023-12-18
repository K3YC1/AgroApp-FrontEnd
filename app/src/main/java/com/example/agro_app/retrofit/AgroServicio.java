package com.example.agro_app.retrofit;

import com.example.agro_app.retrofit.request.LoginRequest;
import com.example.agro_app.retrofit.response.CategoriaResponse;
import com.example.agro_app.retrofit.response.LoginResponse;
import com.example.agro_app.retrofit.response.ProductoResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgroServicio {

    @POST("usuario/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @FormUrlEncoded
    @POST("usuario/registrar")
    Call<ResponseBody> registerNewUser(
            @Field("nombreUsuario") String nombreUsuario,
            @Field("correo") String correo,
            @Field("contraseña") String contraseña
    );

    @FormUrlEncoded
    @POST("usuario/confirmar")
    Call<ResponseBody> confirmUser(
            @Field("codigo") String codigo
    );

    @FormUrlEncoded
    @POST("usuario/recuperar-contraseña")
    Call<ResponseBody> recuperarContraseña(
            @Field("correo") String correo
    );

    @FormUrlEncoded
    @POST("usuario/confirmar-cambio-contraseña")
    Call<ResponseBody> confirmarCambioContraseña(
            @Field("codigoln") String codigoln,
            @Field("nuevaContraseña") String nuevaContraseña
    );

    @GET("usuario/logout")
    Call<ResponseBody> logoutUser();

    @GET("lista-categorias")
    Call<List<CategoriaResponse>> getAllCategorias();

    // Obtener todos los productos
    @GET("lista-productos")
    Call<List<ProductoResponse>> getAllProductos();


    @GET("productos-por-categoria/{categoriaId}")
    Call<List<ProductoResponse>> getProductosByCategoria(@Path("categoriaId") int categoriaId);

    @GET("producto/{id}")
    Call<ProductoResponse> getProductoById(@Path("id") int id);
}
