package com.example.mislugares.casos_uso;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.mislugares.presentacion.AcercaDeActivity;
import com.example.mislugares.presentacion.CustomLoginActivity;
import com.example.mislugares.presentacion.LoginActivity;
import com.example.mislugares.presentacion.MainActivity;
import com.example.mislugares.presentacion.MapaActivity;
import com.example.mislugares.presentacion.PreferenciasActivity;
import com.example.mislugares.presentacion.UserActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class CasosUsoActividades {

   protected Activity actividad;

   public CasosUsoActividades(Activity actividad) {
      this.actividad = actividad;
   }

   public void lanzarAcercaDe() {
      actividad.startActivity(
              new Intent(actividad, AcercaDeActivity.class));
   }

   public void lanzarPreferencias(int codidoSolicitud) {
      actividad.startActivityForResult(
              new Intent(actividad, PreferenciasActivity.class), codidoSolicitud);
   }

   public void lanzarMapa() {
      actividad.startActivity(
              new Intent(actividad, MapaActivity.class));
   }



   public void cerrarSesion(final MainActivity mainActivity) {
      AuthUI.getInstance().signOut(mainActivity) .addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override public void onComplete(@NonNull Task<Void> task) {
            Intent i = new Intent(mainActivity, CustomLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            actividad.startActivity(i);
            actividad.finish(); } });




   }

   public void lanzarActividadUsuario() {
      actividad.startActivity(
              new Intent(actividad, UserActivity.class));
   }
}