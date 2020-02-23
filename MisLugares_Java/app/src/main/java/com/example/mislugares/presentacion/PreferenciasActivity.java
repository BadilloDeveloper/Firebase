package com.example.mislugares.presentacion;

import android.app.Activity;
import android.os.Bundle;

public class PreferenciasActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SelectorFragment.ponerAdaptador(this);

    }
}