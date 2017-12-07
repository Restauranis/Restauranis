package com.restauranis.restauranis;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Menu menu;
    RequestQueue requestQueue;
    private String email, localidad, nombre_usuario, nombre_restaurante, cocina, urlImagen, precio, telefono_usuario;
    private int id_restaurante;
    private String url = "https://www.restauranis.com/consultas-app.php";
    private TextView nombre_user;
    public List<Restaurant> restaurantes = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        localidad = preferences.getString("Localidad", "");
        email = preferences.getString("Email", "");
        requestQueue = Volley.newRequestQueue(this);
        nombre_user = (TextView) findViewById(R.id.nombre_user);
        recyclerView = (RecyclerView) findViewById(R.id.restaurants);

        if(localidad.isEmpty()){
            loadPersonalInfo();
        }else{
            nombre_usuario =preferences.getString("Nombre", "");
            telefono_usuario =preferences.getString("Telefono", "");
        }
        loadRestaurantes();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("¿Estás seguro que quieres salir de la aplicación?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_cercanos) {

        } else if (id == R.id.nav_cocina) {

        } else if (id == R.id.nav_recomendados) {

        } else if (id == R.id.nav_nuevos) {

        } else if (id == R.id.nav_premium) {

        }
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.user_loged));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPersonalInfo(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray j= new JSONArray(response);

                    // Parsea json
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            nombre_usuario = obj.getString("nombre");
                            telefono_usuario = obj.getString("telefono");
                            localidad = obj.getString("localidad");
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("Nombre", nombre_usuario);
                            editor.putString("Telefono", telefono_usuario);
                            editor.putString("Localidad", localidad);
                            editor.apply();
                            //nombre_user.setText(nombre);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("consulta", "2");
                map.put("email",email);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadRestaurantes() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA",response);
                try{
                    JSONArray j= new JSONArray(response);

                    // Parsea json
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            id_restaurante = obj.getInt("id");
                            nombre_restaurante = obj.getString("nombre");
                            precio = obj.getString("precio");
                            cocina = obj.getString("cocina");
                            urlImagen = obj.getString("foto");
                            Restaurant rest = new Restaurant(id_restaurante, nombre_restaurante, urlImagen, precio, cocina);

                            restaurantes.add(rest);
                            //nombre_user.setText(nombre);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(restaurantes));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("consulta", "1");
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Restaurant> mValues;
        private Context mContext;

        public SimpleItemRecyclerViewAdapter(List<Restaurant> items) {
            this.mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurants, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            Picasso.with(getApplicationContext()).load(mValues.get(position).urlImage).into(holder.imageView);

            holder.mView.setTag(mValues.get(position).id);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = (int) v.getTag();
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Miniweb.class);
                    intent.putExtra("idrestaurante", currentPos);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView textViewNombre, textViewCocina, textViewPrecio;
            public final ImageView imageView;
            public Restaurant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewNombre = (TextView) view.findViewById(R.id.title);
                textViewCocina = (TextView) view.findViewById(R.id.precio);
                textViewPrecio = (TextView) view.findViewById(R.id.cocina);
                imageView = (ImageView) view.findViewById(R.id.image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewNombre.getText() + "'";
            }
        }
    }

}
