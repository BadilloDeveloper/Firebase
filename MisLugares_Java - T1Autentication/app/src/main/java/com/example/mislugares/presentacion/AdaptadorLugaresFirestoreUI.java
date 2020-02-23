package com.example.mislugares.presentacion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.mislugares.Aplicacion;
import com.example.mislugares.R;
import com.example.mislugares.modelo.GeoPunto;
import com.example.mislugares.modelo.Lugar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdaptadorLugaresFirestoreUI extends FirestoreRecyclerAdapter<Lugar, AdaptadorLugares.ViewHolder> {
    protected View.OnClickListener onClickListener;
    protected Context context;

    public AdaptadorLugaresFirestoreUI(@NonNull FirestoreRecyclerOptions<Lugar> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public AdaptadorLugares.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        view.setOnClickListener(onClickListener);
        return new AdaptadorLugares.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorLugares.ViewHolder holder, int position, @NonNull Lugar lugar) {
        personalizaVista(holder, lugar);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setTag(new Integer(position));//para obtener posición
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }


    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }

    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()) {
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
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


}
