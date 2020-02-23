package com.example.mislugares.presentacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mislugares.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

public class UserActivity extends AppCompatActivity {


    EditText id,nombre,telefono,email;
    ImageView imageusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        id=findViewById(R.id.usuarioid);
        nombre=findViewById(R.id.ussuarionombre);
        telefono=findViewById(R.id.usuariotelefono);
        email=findViewById(R.id.usuarioemail);
        imageusuario= findViewById(R.id.imagenusuario);



        Button unirCuenta = (Button) findViewById(R.id.btnunircuentas);
        unirCuenta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            Intent i = new Intent(UserActivity.this,CustomLoginActivity.class);
            i.putExtra("unificar",true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            }
        });

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if(usuario!=null){
        nombre.setText(usuario.getDisplayName());
        email.setText(usuario.getEmail() );
        telefono.setText(usuario.getPhoneNumber());
        id.setText(usuario.getUid());
        Uri urlFoto = usuario.getPhotoUrl();
        if(urlFoto!=null)
        new DownloadImageTask(imageusuario)
                    .execute(urlFoto.toString());

       // imageusuario.setImageURI(usuario.getPhotoUrl());




        }


    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
