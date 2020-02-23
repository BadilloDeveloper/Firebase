package com.example.mislugares.datos;

import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.modelo.Usuario;
import com.google.firebase.auth.FirebaseUser;

public interface LugaresAsinc {
    interface EscuchadorElemento {
        void onRespuesta(Lugar lugar);
    }

    interface EscuchadorTamaño {
        void onRespuesta(long tamaño);
    }

    void elemento(String id, EscuchadorElemento escuchador);

    void añade(Lugar lugar);

    String nuevo( );

    void borrar(String id);

    void actualiza(String id, Lugar lugar);

    void tamaño(EscuchadorTamaño escuchador);


}