package com.example.agro_app.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listaDeDeseos")
public class ListaDeDeseos {

    @PrimaryKey(autoGenerate = true)
    public int listaId;

    public int idUsuario;

    public int productoId; // Referencia al producto en la tabla de Productos de MySQL

    public int getListaId() {
        return listaId;
    }

    public void setListaId(int listaId) {
        this.listaId = listaId;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }
}
