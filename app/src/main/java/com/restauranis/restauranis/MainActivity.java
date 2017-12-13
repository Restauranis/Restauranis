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
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
    private String email, localidad, nombre_usuario, nombre_restaurante, cocina, urlImagen, precio, telefono_usuario, valoracion, valoraciones_user;
    private int id_restaurante;
    private String url = "https://www.restauranis.com/consultas-app.php";
    private TextView nombre_user, valoraciones;
    public List<Restaurant> restaurantesCercanos = new ArrayList<>();
    public List<Restaurant> restaurantesValorados = new ArrayList<>();
    public List<Restaurant> restaurantesPrecio = new ArrayList<>();
    public List<Restaurant> restaurantesNuevos = new ArrayList<>();
    public List<Restaurant> restaurantesPremium = new ArrayList<>();
    private RecyclerView recyclerViewCercanos, recyclerViewValorados, recyclerViewPrecio, recyclerViewNuevos, recyclerViewPremium;
    private ImageView masCercanos, masValorados, masPrecio, masNuevos, masPremium;

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
        recyclerViewCercanos = (RecyclerView) findViewById(R.id.cercanos);
        recyclerViewValorados = (RecyclerView) findViewById(R.id.valorados);
        recyclerViewPrecio = (RecyclerView) findViewById(R.id.precio);
        recyclerViewNuevos = (RecyclerView) findViewById(R.id.nuevos);
        recyclerViewPremium = (RecyclerView) findViewById(R.id.premium);

        if(localidad.isEmpty()){
            loadPersonalInfo();
        }else{
            localidad = preferences.getString("Localidad", "");
            nombre_usuario =preferences.getString("Nombre", "");
            telefono_usuario =preferences.getString("Telefono", "");
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONArray j= new JSONArray(response);

                        // Parsea json
                        for (int i = 0; i < j.length(); i++) {
                            try {
                                JSONObject obj = j.getJSONObject(i);
                                valoraciones_user = obj.getString("valoraciones");
                                valoraciones.setText(valoraciones_user);
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
                    Map<String, String> map = new HashMap<>();
                    map.put("consulta", "2");
                    map.put("email",email);
                    return map;
                }
            };
            requestQueue.add(request);
        }
        loadCercanos();
        loadValorados();
        loadPrecio();
        loadNuevos();
        loadPremium();

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(10,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewCercanos.setLayoutManager(staggeredGridLayoutManager);
        StaggeredGridLayoutManager staggeredGridLayoutManager2 = new StaggeredGridLayoutManager(10,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewValorados.setLayoutManager(staggeredGridLayoutManager2);
        StaggeredGridLayoutManager staggeredGridLayoutManager3 = new StaggeredGridLayoutManager(10,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewPrecio.setLayoutManager(staggeredGridLayoutManager3);
        StaggeredGridLayoutManager staggeredGridLayoutManager4 = new StaggeredGridLayoutManager(10,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewNuevos.setLayoutManager(staggeredGridLayoutManager4);
        StaggeredGridLayoutManager staggeredGridLayoutManager5 = new StaggeredGridLayoutManager(10,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerViewPremium.setLayoutManager(staggeredGridLayoutManager5);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        nombre_user = (TextView) headerLayout.findViewById(R.id.nombre_user);
        valoraciones = (TextView) headerLayout.findViewById(R.id.textView);
        nombre_user.setText(nombre_usuario);
        valoraciones.setText(valoraciones_user);


        masCercanos = (ImageView) findViewById(R.id.mas_cercanos);
        masValorados = (ImageView) findViewById(R.id.mas_valorados);
        masPrecio = (ImageView) findViewById(R.id.mas_precio);
        masNuevos = (ImageView) findViewById(R.id.mas_nuevos);
        masPremium = (ImageView) findViewById(R.id.mas_premium);

        masCercanos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Buscador.class);
                intent.putExtra("tipo", "cercanos");
                startActivity(intent);
            }
        });
        masValorados.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Buscador.class);
                intent.putExtra("tipo", "valorados");
                startActivity(intent);
            }
        });
        masPrecio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Buscador.class);
                intent.putExtra("tipo", "precio");
                startActivity(intent);
            }
        });
        masNuevos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Buscador.class);
                intent.putExtra("tipo", "nuevos");
                startActivity(intent);
            }
        });
        masPremium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Buscador.class);
                intent.putExtra("tipo", "premium");
                startActivity(intent);
            }
        });
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
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "recomendados");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    @NonNull
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_cercanos) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "cercanos");
            startActivity(intent);
        } else if (id == R.id.nav_cocina) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "cocina");
            startActivity(intent);
        } else if (id == R.id.nav_recomendados) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "valorados");
            startActivity(intent);
        } else if (id == R.id.nav_nuevos) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "nuevos");
            startActivity(intent);
        } else if (id == R.id.nav_premium) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "premium");
            startActivity(intent);
        }else if(id == R.id.nav_precio) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "precio");
            startActivity(intent);
        }
        //menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.user_loged));
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
                            valoraciones_user = obj.getString("valoraciones");
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
                Map<String, String> map = new HashMap<>();
                map.put("consulta", "2");
                map.put("email",email);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadCercanos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                            valoracion = obj.getString("valoracion");
                            Restaurant rest = new Restaurant(id_restaurante, nombre_restaurante, urlImagen, precio, cocina, valoracion);

                            restaurantesCercanos.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerViewCercanos.setAdapter(new SimpleItemRecyclerViewAdapter(restaurantesCercanos));
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
                Map<String, String> map = new HashMap<>();
                map.put("consulta", "1");
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadValorados() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                            valoracion = obj.getString("valoracion");
                            Restaurant rest = new Restaurant(id_restaurante, nombre_restaurante, urlImagen, precio, cocina, valoracion);

                            restaurantesValorados.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerViewValorados.setAdapter(new SimpleItemRecyclerViewAdapter2(restaurantesValorados));
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
                Map<String, String> map = new HashMap<>();
                map.put("consulta", "3");
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadPrecio() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                            valoracion = obj.getString("valoracion");
                            Restaurant rest = new Restaurant(id_restaurante, nombre_restaurante, urlImagen, precio, cocina, valoracion);

                            restaurantesPrecio.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerViewPrecio.setAdapter(new SimpleItemRecyclerViewAdapter3(restaurantesPrecio));
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
                Map<String, String> map = new HashMap<>();
                map.put("consulta", "4");
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadNuevos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                            valoracion = obj.getString("valoracion");
                            Restaurant rest = new Restaurant(id_restaurante, nombre_restaurante, urlImagen, precio, cocina, valoracion);

                            restaurantesNuevos.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerViewNuevos.setAdapter(new SimpleItemRecyclerViewAdapter4(restaurantesNuevos));
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
                Map<String, String> map = new HashMap<>();
                map.put("consulta", "5");
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadPremium() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                            valoracion = obj.getString("valoracion");
                            Restaurant rest = new Restaurant(id_restaurante, nombre_restaurante, urlImagen, precio, cocina, valoracion);

                            restaurantesPremium.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerViewPremium.setAdapter(new SimpleItemRecyclerViewAdapter5(restaurantesPremium));
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
                Map<String, String> map = new HashMap<>();
                map.put("consulta", "6");
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
                        .inflate(R.layout.cercanos, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewNombre.setTextSize(15);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewCocina.setTextSize(13);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            holder.textViewPrecio.setTextSize(13);
            holder.textViewNota.setText(mValues.get(position).valoracion);
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
            public final TextView textViewNombre, textViewCocina, textViewPrecio, textViewNota;
            public final ImageView imageView;
            public Restaurant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewNombre = (TextView) view.findViewById(R.id.title);
                textViewCocina = (TextView) view.findViewById(R.id.precio);
                textViewPrecio = (TextView) view.findViewById(R.id.cocina);
                textViewNota = (TextView) view.findViewById(R.id.nota);
                imageView = (ImageView) view.findViewById(R.id.image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewNombre.getText() + "'";
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter2
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter2.ViewHolder> {

        private final List<Restaurant> mValues;
        private Context mContext;

        public SimpleItemRecyclerViewAdapter2(List<Restaurant> items) {
            this.mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.valorados, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewNombre.setTextSize(15);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewCocina.setTextSize(13);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            holder.textViewPrecio.setTextSize(13);
            holder.textViewNota.setText(mValues.get(position).valoracion);
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
            public final TextView textViewNombre, textViewCocina, textViewPrecio, textViewNota;
            public final ImageView imageView;
            public Restaurant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewNombre = (TextView) view.findViewById(R.id.title);
                textViewCocina = (TextView) view.findViewById(R.id.precio);
                textViewPrecio = (TextView) view.findViewById(R.id.cocina);
                textViewNota = (TextView) view.findViewById(R.id.nota);
                imageView = (ImageView) view.findViewById(R.id.image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewNombre.getText() + "'";
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter3
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter3.ViewHolder> {

        private final List<Restaurant> mValues;
        private Context mContext;

        public SimpleItemRecyclerViewAdapter3(List<Restaurant> items) {
            this.mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.precio, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewNombre.setTextSize(15);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewCocina.setTextSize(13);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            holder.textViewPrecio.setTextSize(13);
            holder.textViewNota.setText(mValues.get(position).valoracion);
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
            public final TextView textViewNombre, textViewCocina, textViewPrecio, textViewNota;
            public final ImageView imageView;
            public Restaurant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewNombre = (TextView) view.findViewById(R.id.title);
                textViewCocina = (TextView) view.findViewById(R.id.precio);
                textViewPrecio = (TextView) view.findViewById(R.id.cocina);
                textViewNota = (TextView) view.findViewById(R.id.nota);
                imageView = (ImageView) view.findViewById(R.id.image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewNombre.getText() + "'";
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter4
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter4.ViewHolder> {

        private final List<Restaurant> mValues;
        private Context mContext;

        public SimpleItemRecyclerViewAdapter4(List<Restaurant> items) {
            this.mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nuevos, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewNombre.setTextSize(15);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewCocina.setTextSize(13);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            holder.textViewPrecio.setTextSize(13);
            holder.textViewNota.setText(mValues.get(position).valoracion);
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
            public final TextView textViewNombre, textViewCocina, textViewPrecio, textViewNota;
            public final ImageView imageView;
            public Restaurant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewNombre = (TextView) view.findViewById(R.id.title);
                textViewCocina = (TextView) view.findViewById(R.id.precio);
                textViewPrecio = (TextView) view.findViewById(R.id.cocina);
                textViewNota = (TextView) view.findViewById(R.id.nota);
                imageView = (ImageView) view.findViewById(R.id.image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewNombre.getText() + "'";
            }
        }
    }

    public class SimpleItemRecyclerViewAdapter5
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter5.ViewHolder> {

        private final List<Restaurant> mValues;
        private Context mContext;

        public SimpleItemRecyclerViewAdapter5(List<Restaurant> items) {
            this.mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.premium, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewNombre.setTextSize(15);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewCocina.setTextSize(13);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            holder.textViewPrecio.setTextSize(13);
            holder.textViewNota.setText(mValues.get(position).valoracion);
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
            public final TextView textViewNombre, textViewCocina, textViewPrecio, textViewNota;
            public final ImageView imageView;
            public Restaurant mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                textViewNombre = (TextView) view.findViewById(R.id.title);
                textViewCocina = (TextView) view.findViewById(R.id.precio);
                textViewPrecio = (TextView) view.findViewById(R.id.cocina);
                textViewNota = (TextView) view.findViewById(R.id.nota);
                imageView = (ImageView) view.findViewById(R.id.image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + textViewNombre.getText() + "'";
            }
        }
    }
}
