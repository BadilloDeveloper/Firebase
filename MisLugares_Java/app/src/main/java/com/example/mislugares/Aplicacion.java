package com.example.mislugares;

import android.app.Application;

import com.example.mislugares.datos.LugaresAsinc;
import com.example.mislugares.datos.LugaresBD;

import com.example.mislugares.datos.LugaresFirestore;
import com.example.mislugares.modelo.GeoPunto;

import com.example.mislugares.modelo.Lugar;
import com.example.mislugares.presentacion.AdaptadorLugaresBD;

import com.example.mislugares.presentacion.AdaptadorLugaresFirestore;
import com.example.mislugares.presentacion.AdaptadorLugaresFirestoreUI;
import com.example.mislugares.presentacion.SelectorFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Aplicacion extends Application {

    public LugaresAsinc lugares;
    public AdaptadorLugaresFirestore adaptador;


    public GeoPunto posicionActual = new GeoPunto(0.0, 0.0);

    @Override
    public void onCreate() {
        super.onCreate();
        lugares = new LugaresFirestore();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference lugares = db.collection("lugares");

       // Query query = FirebaseFirestore.getInstance().collection("lugares").limit(50);
       // FirestoreRecyclerOptions<Lugar> opciones = new FirestoreRecyclerOptions.Builder<Lugar>().setQuery(query, Lugar.class).build();

        SelectorFragment.ponerAdaptador(this);
       /* adaptador = new AdaptadorLugaresBD(lugares
                , lugares.extraeCursor());*/
    }
   /* @Override public void onCreate() {
        super.onCreate();
        lugares.añadeEjemplos();
    }*/


/*    public LugaresLista getLugares() {
        return lugares;
    }*/
}
