package com.restauranis.restauranis;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Miniweb extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int id_restaurante;
    private String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.miniweb);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.miniweb_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        id_restaurante = getIntent().getIntExtra("idrestaurante",0);
        tipo = getIntent().getStringExtra("tipo");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt("idrestaurante", id_restaurante);
            MiniwebFragment fragment = new MiniwebFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.miniweb_details, fragment)
                    .commit();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(Miniweb.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_cercanos) {
            Intent intent = new Intent(Miniweb.this, Buscador.class);
            intent.putExtra("tipo", "cercanos");
            startActivity(intent);
        } else if (id == R.id.nav_cocina) {
            Intent intent = new Intent(Miniweb.this, Buscador.class);
            startActivity(intent);
        } else if (id == R.id.nav_recomendados) {
            Intent intent = new Intent(Miniweb.this, Buscador.class);
            intent.putExtra("tipo", "valorados");
            startActivity(intent);
        } else if (id == R.id.nav_nuevos) {
            Intent intent = new Intent(Miniweb.this, Buscador.class);
            intent.putExtra("tipo", "nuevos");
            startActivity(intent);
        } else if (id == R.id.nav_premium) {
            Intent intent = new Intent(Miniweb.this, Buscador.class);
            intent.putExtra("tipo", "premium");
            startActivity(intent);
        }
        //menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.user_loged));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(tipo!=null){
            Intent intent = new Intent(Miniweb.this, Buscador.class);
            intent.putExtra("tipo", tipo);
            startActivity(intent);
        }else{
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
        }
    }
}
