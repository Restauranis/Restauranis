package com.restauranis.restauranis;

import android.app.Activity;
import android.app.AlertDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

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
    private RelativeLayout no_gps;
    private HorizontalScrollView scrollCercanos;
    public LocationManager locationManager;
    private Location mLastLocation;
    boolean GpsStatus;
    private Button button_gps;
    private GoogleApiClient apiClient;
    private double latitud, longitud;
    private int ubicacion;

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
        no_gps = (RelativeLayout) findViewById(R.id.no_gps);
        scrollCercanos = (HorizontalScrollView) findViewById(R.id.scrollCercanos);
        button_gps = (Button) findViewById(R.id.button_gps);
        latitud = getIntent().getDoubleExtra("lat", 0);
        longitud = getIntent().getDoubleExtra("lon", 0);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (latitud != 0 && longitud != 0) {
            no_gps.setVisibility(View.GONE);
            Log.d("AAAA","lat1:"+latitud);
            Log.d("AAAA","lon1:"+longitud);
            loadCercanos();
        } else {
            if(GpsStatus==true){
                Log.d("AAAA","ubi:"+ubicacion);
                ubicacion = ubicacion+1;
            }
            scrollCercanos.setVisibility(View.GONE);
            button_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(30 * 1000);
                    locationRequest.setFastestInterval(5 * 1000);

                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);

                    builder.setAlwaysShow(true);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());

                    if (result != null) {
                        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                            @Override
                            public void onResult(LocationSettingsResult locationSettingsResult) {
                                final Status status = locationSettingsResult.getStatus();

                                switch (status.getStatusCode()) {
                                    case LocationSettingsStatusCodes.SUCCESS:
                                        // All location settings are satisfied. The client can initialize location
                                        // requests here.

                                        break;
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        // Location settings are not satisfied. But could be fixed by showing the user
                                        // a optionsDialog.
                                        try {
                                            // Show the optionsDialog by calling startResolutionForResult(),
                                            // and check the result in onActivityResult().
                                            if (status.hasResolution()) {
                                                status.startResolutionForResult(MainActivity.this, 1000);
                                            }
                                        } catch (IntentSender.SendIntentException e) {
                                            // Ignore the error.
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        // Location settings are not satisfied. However, we have no way to fix the
                                        // settings so we won't show the optionsDialog.
                                        break;
                                }
                            }
                        });
                    }
                }
            });

        }

        if (localidad.isEmpty()) {
            loadPersonalInfo();
        } else {
            localidad = preferences.getString("Localidad", "");
            nombre_usuario = preferences.getString("Nombre", "");
            telefono_usuario = preferences.getString("Telefono", "");
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray j = new JSONArray(response);

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
                    map.put("email", email);
                    return map;
                }
            };
            requestQueue.add(request);
        }

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
                intent.putExtra("lat", latitud);
                intent.putExtra("lon", longitud);
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
    protected void onStart() {
        super.onStart();
        if (apiClient != null) {
            apiClient.connect();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("AAAA", "Error grave al conectar con Google Play Services");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("AAAA","ubi2:"+ubicacion);
        if(ubicacion==1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);

                latitud = lastLocation.getLatitude();
                longitud = lastLocation.getLongitude();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("lat", latitud);
                intent.putExtra("lon", longitud);
                //intent.putExtra("Localidad",addresses.get(0).getLocality());
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK) && (requestCode == 1000)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                Log.d("AAAA",addresses.get(0).getLocality());
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("lat",mLastLocation.getLatitude());
                intent.putExtra("lon",mLastLocation.getLongitude());
                //intent.putExtra("Localidad",addresses.get(0).getLocality());
                finish();
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("AAAA", "Se ha interrumpido la conexión con Google Play Services");
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
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_cercanos) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "cercanos");
            intent.putExtra("lat", latitud);
            intent.putExtra("lon", longitud);
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
        } else if (id == R.id.nav_precio) {
            Intent intent = new Intent(MainActivity.this, Buscador.class);
            intent.putExtra("tipo", "precio");
            startActivity(intent);
        }
        //menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.user_loged));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPersonalInfo() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

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
                map.put("email", email);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadCercanos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

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
                map.put("localidad", localidad);
                map.put("lat", String.valueOf(latitud));
                map.put("lon", String.valueOf(longitud));
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadValorados() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

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
                map.put("localidad", localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadPrecio() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

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
                map.put("localidad", localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadNuevos() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

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
                map.put("localidad", localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadPremium() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

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
                map.put("localidad", localidad);
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
