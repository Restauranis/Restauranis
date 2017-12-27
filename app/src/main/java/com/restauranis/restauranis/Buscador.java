package com.restauranis.restauranis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Buscador extends AppCompatActivity {

    private String tipo, localidad, predeterminada, idCocina, buscador;
    private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.miniweb_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tipo = getIntent().getStringExtra("tipo");
        predeterminada = getIntent().getStringExtra("predeterminada");
        idCocina = getIntent().getStringExtra("idCocina");
        buscador = getIntent().getStringExtra("buscador");
        latitud = getIntent().getDoubleExtra("lat",0);
        longitud = getIntent().getDoubleExtra("lon",0);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString("tipo", tipo);
            arguments.putString("predeterminada", predeterminada);
            arguments.putString("idCocina", idCocina);
            arguments.putString("buscador", buscador);
            arguments.putDouble("lat",latitud);
            arguments.putDouble("lon",longitud);
            BuscadorFragment fragment = new BuscadorFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.buscador_details, fragment).commit();
        }

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
    }
}
