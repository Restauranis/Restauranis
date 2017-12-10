package com.restauranis.restauranis;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Reservar extends AppCompatActivity {

    private int id_restaurante;
    private String urlImagen, nombre_restaurante, email, fecha, hora, personas;
    private ImageView background;
    private TextView textViewNombre, textViewFecha, textViewHora, textViewPersonas, textViewRealizada;
    private TableLayout table_fecha, table_hora, table_personas;
    RequestQueue requestQueue;
    private String url = "https://www.restauranis.com/consultas-miniweb-app.php";
    private int mYear, mMonth, mDay, hour, minut;
    Calendar C = Calendar.getInstance();
    private Button reservar;
    private View separador1, separador2, separador3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestQueue = Volley.newRequestQueue(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("Email", "");

        id_restaurante = getIntent().getIntExtra("idrestaurante", 0);
        urlImagen = getIntent().getStringExtra("foto");
        nombre_restaurante = getIntent().getStringExtra("nombre");
        background = (ImageView) findViewById(R.id.background_opinar);
        textViewNombre = (TextView) findViewById(R.id.nombre_restaurante_opinion);
        textViewFecha = (TextView) findViewById(R.id.texto_fecha);
        textViewHora = (TextView) findViewById(R.id.texto_hora);
        textViewPersonas = (TextView) findViewById(R.id.texto_personas);
        reservar = (Button) findViewById(R.id.button_reservar);
        textViewRealizada = (TextView) findViewById(R.id.reserva_realizada);
        separador1 = findViewById(R.id.separador2);
        separador2 = findViewById(R.id.separador3);
        separador3 = findViewById(R.id.separador4);

        Picasso.with(this).load(urlImagen).into(background);
        textViewNombre.setText(nombre_restaurante);

        table_fecha = (TableLayout) findViewById(R.id.table_fecha);
        table_hora = (TableLayout) findViewById(R.id.table_hora);
        table_personas = (TableLayout) findViewById(R.id.table_personas);

        mYear = C.get(Calendar.YEAR);
        mMonth = C.get(Calendar.MONTH);
        mDay = C.get(Calendar.DAY_OF_MONTH);
        table_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewFecha.setError(null);
                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int s=monthOfYear+1;
                        String day;
                        if(dayOfMonth<10){
                            day = "0"+dayOfMonth;
                        }else{
                            day = String.valueOf(dayOfMonth);
                        }
                        String a = day+"/"+s+"/"+year;
                        textViewFecha.setText(a);
                    }
                };
                DatePickerDialog d = new DatePickerDialog(Reservar.this, dpd, mYear ,mMonth, mDay);
                d.show();

            }
        });

        hour = C.get(Calendar.HOUR_OF_DAY);
        minut = C.get(Calendar.MINUTE);
        table_hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewHora.setError(null);
                TimePickerDialog.OnTimeSetListener dpd = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textViewHora.setText( hourOfDay + ":" + minute);
                    }
                };
                TimePickerDialog d = new TimePickerDialog(Reservar.this, dpd, hour ,minut, true);
                d.show();
            }
        });

        table_personas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                textViewPersonas.setError(null);
                numberPickerDialog();
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                fecha = textViewFecha.getText().toString();
                hora = textViewHora.getText().toString();
                personas = textViewPersonas.getText().toString();
                if(!fecha.contains("/")){
                    textViewFecha.setError("Selecciona una fecha");
                }
                else if(!hora.contains(":")){
                    textViewHora.setError("Selecciona una hora");
                }
                else if(!isNumeric(personas)){
                    textViewPersonas.setError("Selecciona las personas");
                }else{
                    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            table_fecha.setVisibility(View.GONE);
                            table_hora.setVisibility(View.GONE);
                            table_personas.setVisibility(View.GONE);
                            separador1.setVisibility(View.GONE);
                            separador2.setVisibility(View.GONE);
                            separador3.setVisibility(View.GONE);
                            reservar.setVisibility(View.GONE);
                            textViewRealizada.setText("En estos momentos estamos tramitando su solicitud. En breve recibirá un correo electrónico de confirmación o una llamada para acabar de tramitar la reserva.\n\nMuchas gracias por confiar en Restauranis\n");
                            textViewRealizada.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("consulta", "7");
                            map.put("idrestaurante", String.valueOf(id_restaurante));
                            map.put("fecha", String.valueOf(fecha));
                            map.put("hora", String.valueOf(hora));
                            map.put("personas", String.valueOf(personas));
                            map.put("usuario", email);
                            return map;
                        }
                    };
                    requestQueue.add(request);
                }
            }
        });
    }

    private static boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }

    private void numberPickerDialog(){
        NumberPicker numberPicker = new NumberPicker(Reservar.this);
        numberPicker.setMaxValue(20);
        numberPicker.setMinValue(0);

        NumberPicker.OnValueChangeListener changeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textViewPersonas.setText(""+newVal);
            }
        };

        numberPicker.setOnValueChangedListener(changeListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(Reservar.this).setView(numberPicker);
        builder.setTitle("Número de Personas");
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    @Override
    public void onBackPressed() {
        // using a fragment transaction.
        Intent intent = new Intent(this, Miniweb.class);
        intent.putExtra("idrestaurante", id_restaurante);
        startActivity(intent);
    }

}
