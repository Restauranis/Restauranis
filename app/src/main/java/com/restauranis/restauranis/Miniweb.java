package com.restauranis.restauranis;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class Miniweb extends AppCompatActivity {

    private Restaurant restaurant;
    RequestQueue requestQueue;
    private String url = "https://www.restauranis.com/consultas-miniweb-app.php";
    private String email, localidad, nombre_usuario, nombre_restaurante, cocina, urlImagen, precio;
    private int id_restaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.miniweb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.miniweb_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        id_restaurante = getIntent().getIntExtra("idrestaurante",0);
        requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray j= new JSONArray(response);

                    // Parsea json
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            urlImagen = obj.getString("foto");
                            Log.d("AAAA",urlImagen);
                            ImageView background = (ImageView) findViewById(R.id.background_miniweb);
                            Picasso.with(getBaseContext()).load(urlImagen).into(background);

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
                map.put("consulta", "1");
                map.put("idrestaurante", String.valueOf(id_restaurante));
                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
    }
}
