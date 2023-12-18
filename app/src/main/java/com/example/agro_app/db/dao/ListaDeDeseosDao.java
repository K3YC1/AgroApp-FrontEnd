package com.example.agro_app.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.agro_app.db.entity.ListaDeDeseos;

@Dao
public interface ListaDeDeseosDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ListaDeDeseos listaDeDeseos);

    @Query("DELETE FROM listaDeDeseos WHERE productoId = :productoId")
    void deleteByProductoId(int productoId);

    @Query("SELECT * FROM listaDeDeseos WHERE productoId = :productoId")
    ListaDeDeseos getListaDeDeseosByProductoId(int productoId);
    @Delete
    void eliminarPorProducto(ListaDeDeseos listaDeDeseos);
}
