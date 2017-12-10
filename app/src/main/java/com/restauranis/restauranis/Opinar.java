package com.restauranis.restauranis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Opinar extends AppCompatActivity {

    private int id_restaurante;
    private String urlImagen, nombre_restaurante, comentarios, email;
    private ImageView background;
    private TextView textViewNombre;
    private NumberPicker numberpicker_comida, numberpicker_ambiente, numberpicker_precio, numberpicker_servicio;
    private int comida, ambiente, precio, servicio;
    private Button dejar_valoracion;
    private EditText editTextComentarios;
    RequestQueue requestQueue;
    private String url = "https://www.restauranis.com/consultas-miniweb-app.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opinar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("Email", "");

        id_restaurante = getIntent().getIntExtra("idrestaurante",0);
        urlImagen = getIntent().getStringExtra("foto");
        nombre_restaurante = getIntent().getStringExtra("nombre");
        background = (ImageView) findViewById(R.id.background_opinar);
        textViewNombre = (TextView) findViewById(R.id.nombre_restaurante_opinion);

        Picasso.with(this).load(urlImagen).into(background);
        textViewNombre.setText(nombre_restaurante);
        dejar_valoracion = (Button) findViewById(R.id.dejar_valoracion);
        editTextComentarios = (EditText) findViewById(R.id.comentarios);

        numberpicker_comida = (NumberPicker)findViewById(R.id.numberPicker_comida);
        numberpicker_ambiente = (NumberPicker)findViewById(R.id.numberPicker_ambiente);
        numberpicker_precio = (NumberPicker)findViewById(R.id.numberPicker_precio);
        numberpicker_servicio = (NumberPicker)findViewById(R.id.numberPicker_servicio);

        numberpicker_comida.setMinValue(1);
        numberpicker_comida.setMaxValue(10);
        numberpicker_ambiente.setMinValue(1);
        numberpicker_ambiente.setMaxValue(10);
        numberpicker_precio.setMinValue(1);
        numberpicker_precio.setMaxValue(10);
        numberpicker_servicio.setMinValue(1);
        numberpicker_servicio.setMaxValue(10);

        numberpicker_comida.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                comida = newVal;
            }
        });
        numberpicker_ambiente.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ambiente = newVal;
            }
        });
        numberpicker_precio.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                precio = newVal;
            }
        });
        numberpicker_servicio.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                servicio = newVal;
            }
        });

        dejar_valoracion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                comentarios = editTextComentarios.getText().toString();
                if(ambiente==0){
                    ambiente=1;
                }
                if(servicio==0){
                    servicio=1;
                }
                if(precio==0){
                    precio=1;
                }
                if(comida==0){
                    comida=1;
                }
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        editTextComentarios.setVisibility(View.GONE);
                        TableLayout tableLayout = (TableLayout) findViewById(R.id.valoraciones);
                        tableLayout.setVisibility(View.GONE);
                        TextView textView = (TextView) findViewById(R.id.descripcion_valoracion);
                        TextView textView2 = (TextView) findViewById(R.id.descripcion_comentario);
                        textView2.setVisibility(View.GONE);
                        dejar_valoracion.setVisibility(View.GONE);
                        textView.setText("¡Muchas gracias por colaborar y confiar en Restauranis!");
                        textView.setTextSize(22);

                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_opinar);
                        Button bt = new Button(getApplicationContext());
                        bt.setText("VOLVER");
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.addRule(RelativeLayout.BELOW, textView.getId());
                        params.setMargins(25,10,25,10);
                        bt.setLayoutParams(params);
                        bt.setBackgroundResource(R.color.gris);
                        bt.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                        relativeLayout.addView(bt);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("consulta", "6");
                        map.put("idrestaurante", String.valueOf(id_restaurante));
                        map.put("ambiente", String.valueOf(ambiente));
                        map.put("precio", String.valueOf(precio));
                        map.put("servicio", String.valueOf(servicio));
                        map.put("comida", String.valueOf(comida));
                        map.put("comentarios", comentarios);
                        map.put("usuario", email);
                        return map;
                    }
                };
                requestQueue.add(request);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // using a fragment transaction.
        Intent intent = new Intent(this, Miniweb.class);
        intent.putExtra("idrestaurante", id_restaurante);
        startActivity(intent);
    }

}
