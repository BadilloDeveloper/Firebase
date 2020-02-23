package com.example.mislugares.presentacion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mislugares.Aplicacion;
import com.example.mislugares.R;
import com.example.mislugares.modelo.GeoPunto;
import com.example.mislugares.modelo.Lugar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;
import static java.lang.StrictMath.abs;


public class AdaptadorLugaresFirestore extends RecyclerView.Adapter<AdaptadorLugares.ViewHolder> implements EventListener<QuerySnapshot> {
    public static final String TAG = "Mislugares";
    private Context context;
    private Query query;
    private List<DocumentSnapshot> items;
    private ListenerRegistration registration;
    private LayoutInflater inflador;
    private View.OnClickListener onClickListener;


    public AdaptadorLugaresFirestore(Context context, Query query) {
        items = new ArrayList<DocumentSnapshot>();
        this.query = query;
        this.context = context;
        inflador = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        view.setOnClickListener(onClickListener);
        return new AdaptadorLugares.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdaptadorLugares.ViewHolder holder, int position) {
        Lugar lugar = getItem(position);
        personalizaVista(holder, lugar);
        holder.itemView.setTag(new Integer(position));
    }

    // Personalizamos un ViewHolder a partir de un lugar
    public void personalizaVista(AdaptadorLugares.ViewHolder holder, Lugar lugar) {
        holder.nombre.setText(lugar.getNombre());
        holder.direccion.setText(lugar.getDireccion());
        int id = R.drawable.otros;
        switch (lugar.getTipo()) {
            case RESTAURANTE:
                id = R.drawable.restaurante;
                break;
            case BAR:
                id = R.drawable.bar;
                break;
            case COPAS:
                id = R.drawable.copas;
                break;
            case ESPECTACULO:
                id = R.drawable.espectaculos;
                break;
            case HOTEL:
                id = R.drawable.hotel;
                break;
            case COMPRAS:
                id = R.drawable.compras;
                break;
            case EDUCACION:
                id = R.drawable.educacion;
                break;
            case DEPORTE:
                id = R.drawable.deporte;
                break;
            case NATURALEZA:
                id = R.drawable.naturaleza;
                break;
            case GASOLINERA:
                id = R.drawable.gasolinera;
                break;
        }
        holder.foto.setImageResource(id);
        holder.foto.setScaleType(ImageView.ScaleType.FIT_END);
        holder.valoracion.setRating(lugar.getValoracion());
        GeoPunto pos = ((Aplicacion) context.getApplicationContext()).posicionActual;
        if (pos.equals(GeoPunto.SIN_POSICION) || lugar.getPosicion().equals(GeoPunto.SIN_POSICION)) {
            holder.distancia.setText("... Km");
        } else {
            int d = (int) pos.distancia(lugar.getPosicion());
            if (d < 2000) holder.distancia.setText(d + " m");
            else holder.distancia.setText(d / 1000 + " Km");
        }
    }


    public Lugar getItem(int pos) {
        return items.get(pos).toObject(Lugar.class);
    }

    public String getKey(int pos) {
        return items.get(pos).getId();
    }

    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()) {
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public void startListening() {
        items = new ArrayList<DocumentSnapshot>();
        registration = query.addSnapshotListener(this);
    }

    public void stopListening() {
        registration.remove();
    }

    @Override
    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.e(TAG, "error al recibir evento", e);
            Toast.makeText(context, "FirestoreException: Ver LogCat", Toast.LENGTH_LONG).show();
            return;
        }
        for (DocumentChange dc : snapshots.getDocumentChanges()) {
            int pos = dc.getNewIndex();
            int oldPos = dc.getOldIndex();
            switch (dc.getType()) {
                case ADDED:
                    items.add(pos, dc.getDocument());
                    notifyItemInserted(pos);
                    break;
                case REMOVED:
                    items.remove(oldPos);
                    notifyItemRemoved(oldPos);
                    break;
                case MODIFIED:
                    items.remove(oldPos);
                    items.add(pos, dc.getDocument());
                    notifyItemRangeChanged(min(pos, oldPos), abs(pos - oldPos) + 1);
                    break;
                default:
                    Log.w(TAG, "Tipo de cambio desconocido", e);
            }
        }
    }
}