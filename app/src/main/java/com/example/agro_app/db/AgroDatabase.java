package com.example.agro_app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.agro_app.db.dao.ContadorUsuariosDao;
import com.example.agro_app.db.dao.ListaDeDeseosDao;
import com.example.agro_app.db.entity.ContadorUsuarios;
import com.example.agro_app.db.entity.ListaDeDeseos;

@Database(entities = {ContadorUsuarios.class, ListaDeDeseos.class}, version = 1)
public abstract class AgroDatabase extends RoomDatabase {

    public abstract ContadorUsuariosDao contadorUsuariosDao();

    public abstract ListaDeDeseosDao listaDeDeseosDao();
    private static volatile  AgroDatabase INSTANCIA;

    public static AgroDatabase getDatabase(final Context context){
        if(INSTANCIA == null){
            synchronized (AgroDatabase.class){
                if(INSTANCIA == null){
                    INSTANCIA = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AgroDatabase.class,
                                    "agrobd")
                            .build();
                }
            }
        }
        return INSTANCIA;
    }
}
