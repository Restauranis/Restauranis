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

public class Buscador extends AppCompatActivity {

    private String tipo, localidad, predeterminada, idCocina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buscador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.miniweb_toolbar);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        localidad = preferences.getString("Localidad", "");
        toolbar.setTitle("UBICACION");
        setSupportActionBar(toolbar);
        final CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        appBarLayout.setTitle("ASFD");


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tipo = getIntent().getStringExtra("tipo");
        predeterminada = getIntent().getStringExtra("predeterminada");
        idCocina = getIntent().getStringExtra("idCocina");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString("tipo", tipo);
            arguments.putString("predeterminada", predeterminada);
            arguments.putString("idCocina", idCocina);
            BuscadorFragment fragment = new BuscadorFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.buscador_details, fragment).commit();
        }

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
    }
}
