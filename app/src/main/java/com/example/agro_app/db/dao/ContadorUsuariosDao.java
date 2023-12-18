package com.example.agro_app.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.agro_app.db.entity.ContadorUsuarios;

@Dao
public interface ContadorUsuariosDao {
    @Query("SELECT * FROM contadorUsuarios WHERE idUsuario = :idUsuario")
    ContadorUsuarios getContadorUsuariosByUserId(int idUsuario);

    @Insert
    void insert(ContadorUsuarios contadorUsuarios);

    @Update
    void update(ContadorUsuarios contadorUsuarios);

    @Delete
    void delete(ContadorUsuarios contadorUsuarios);
}
