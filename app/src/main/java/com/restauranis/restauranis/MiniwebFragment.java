package com.restauranis.restauranis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toolbar;

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

import java.text.Normalizer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MiniwebFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private Restaurant restaurant;
    RequestQueue requestQueue;
    private String url = "https://www.restauranis.com/consultas-miniweb-app.php";
    private String email, localidad, nombre_usuario, nombre_restaurante, cocina, urlImagen, precio, direccion, telefono, caracteristico, lat, lon, valoracion, llamar, fecha_solicitud, personas_solicitud, presupuesto_solicitud, comentarios_solicitud, comentarios;
    private int id_restaurante;
    private TextView textViewNombre, textViewDireccion, textViewLocalidad, textViewTelefono, textViewCocina, textViewCaracteristico, textViewValoracion, carta, menu_1, menu_2, menu_3, menu_4, menu_5, textViewComentarios;
    private EditText editText_fecha_solicitud, editText_personas_solicitud, editText_presupuesto_solicitud, editText_comentarios_solicitud;
    String fira_sans_regular = "font/fira_sans_regular.ttf";
    String fira_sans_semibold = "font/fira_sans_semibold.ttf";
    String fira_sans_light = "font/fira_sans_light.ttf";
    private int mYear, mMonth, mDay, numMenus, numCartas;
    static final int DATE_ID = 0;
    Calendar C = Calendar.getInstance();
    private ImageView icono_solicitud, icono_como_llegar, compartir_miniweb, llamar_miniweb, icono_comentarios, icono_reservar;
    private Button solicitud_grupos;
    private ScrollView scrollView;

    public MiniwebFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MiniwebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MiniwebFragment newInstance(String param1, String param2) {
        MiniwebFragment fragment = new MiniwebFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestQueue = Volley.newRequestQueue(this);
        id_restaurante = getArguments().getInt("idrestaurante");
        final Activity activity = this.getActivity();
        final CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) activity.findViewById(R.id.miniweb_toolbar);

        requestQueue = Volley.newRequestQueue(activity);
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
                            nombre_restaurante = obj.getString("nombre");
                            direccion = obj.getString("direccion");
                            localidad = obj.getString("localidad");
                            telefono = obj.getString("telefonoReservas");
                            llamar = obj.getString("telefonoReservas");
                            cocina = obj.getString("cocina");
                            caracteristico = obj.getString("caracteristico").replace(",", " · ");
                            caracteristico = caracteristico.replace("[", "");
                            caracteristico = caracteristico.replace("]", "");
                            caracteristico = caracteristico.replace("\"", "");
                            lat = obj.getString("lat");
                            lon = obj.getString("lon");
                            valoracion = obj.getString("valoracion");
                            numMenus = obj.getInt("numMenus");
                            numCartas = obj.getInt("numCarta");

                            ImageView background = (ImageView) activity.findViewById(R.id.background_miniweb);
                            textViewNombre = (TextView) activity.findViewById(R.id.nombre_restaurante);
                            textViewDireccion = (TextView) activity.findViewById(R.id.direccion);
                            textViewLocalidad = (TextView) activity.findViewById(R.id.localidad_miniweb);
                            textViewTelefono = (TextView) activity.findViewById(R.id.telefono_reservas);
                            textViewCocina = (TextView) activity.findViewById(R.id.cocina);
                            textViewCaracteristico = (TextView) activity.findViewById(R.id.caracteristico);
                            icono_reservar = (ImageView) activity.findViewById(R.id.icono_reservar);
                            icono_solicitud = (ImageView) activity.findViewById(R.id.icono_solicitud);
                            icono_como_llegar = (ImageView) activity.findViewById(R.id.icono_como_llegar);
                            icono_comentarios = (ImageView) activity.findViewById(R.id.icono_comentarios);
                            compartir_miniweb = (ImageView) activity.findViewById(R.id.compartir_miniweb);
                            editText_fecha_solicitud = (EditText) activity.findViewById(R.id.fecha_solicitud);
                            editText_personas_solicitud = (EditText) activity.findViewById(R.id.personas_solicitud);
                            editText_presupuesto_solicitud = (EditText) activity.findViewById(R.id.presupuesto_solicitud);
                            editText_comentarios_solicitud = (EditText) activity.findViewById(R.id.comentarios_solicitud);
                            textViewValoracion = (TextView) activity.findViewById(R.id.valoracion);
                            llamar_miniweb = (ImageView) activity.findViewById(R.id.llamar_miniweb);
                            carta = (TextView) activity.findViewById(R.id.carta);
                            menu_1 = (TextView) activity.findViewById(R.id.menu_1);
                            menu_2 = (TextView) activity.findViewById(R.id.menu_2);
                            menu_3 = (TextView) activity.findViewById(R.id.menu_3);
                            menu_4 = (TextView) activity.findViewById(R.id.menu_4);
                            menu_5 = (TextView) activity.findViewById(R.id.menu_5);
                            scrollView = (ScrollView) activity.findViewById(R.id.scrollView);

                            final NestedScrollView scroller = (NestedScrollView) activity.findViewById(R.id.miniweb_details);
                            if (scroller != null) {

                                scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                                        Log.d("AAAA", "asdf"+scroller.getTop());

                                        if(scroller.getTop()<250){
                                            appBarLayout.setTitle(nombre_restaurante);
                                        }else if(scroller.getTop()>250){
                                            if (appBarLayout.getTitle()!=""){
                                                appBarLayout.setTitle("");
                                            }
                                        }
                                    }
                                });
                            }

                            solicitud_grupos = (Button) activity.findViewById(R.id.solicitar_solicitud);
                            solicitud_grupos.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                    nombre_usuario = preferences.getString("Nombre", "");
                                    telefono = preferences.getString("Telefono", "");
                                    email = preferences.getString("Email", "");
                                    if(editText_fecha_solicitud.getText().toString().isEmpty()){
                                        editText_fecha_solicitud.setError("Este campo no puede estar vacío");
                                    }
                                    else if(editText_personas_solicitud.getText().toString().isEmpty()){
                                        editText_personas_solicitud.requestFocus();
                                        editText_personas_solicitud.setError("Este campo no puede estar vacío");
                                    }else{
                                        fecha_solicitud = editText_fecha_solicitud.getText().toString();
                                        personas_solicitud = editText_personas_solicitud.getText().toString();
                                        presupuesto_solicitud = editText_presupuesto_solicitud.getText().toString();
                                        comentarios_solicitud = editText_comentarios_solicitud.getText().toString();
                                        solicitarMenu();
                                    }
                                }
                            });
                            llamar_miniweb.setOnClickListener(new View.OnClickListener(){

                                public void onClick(View v){
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + llamar));
                                    startActivity(intent);
                                }
                            });
                            icono_como_llegar.setOnClickListener(new View.OnClickListener(){

                                public void onClick(View v){
                                    String label = nombre_restaurante;
                                    String uriBegin = "geo:" + lat + "," + lon;
                                    String query = lat + "," + lon + "(" + label + ")";
                                    String encodedQuery = Uri.encode(query);
                                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                                    Uri uri = Uri.parse(uriString);
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                            compartir_miniweb.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(Intent.EXTRA_SUBJECT, "Restauranis");
                                    String sAux = "\nHe visto el restaurante "+nombre_restaurante+" en la aplicación de Restauranis: \n";
                                    sAux = sAux + "https://www.restauranis.com/restaurante-"+nombreUrl(nombre_restaurante)+"-"+localidad+"\n¿Qué opinas?";
                                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                                    startActivity(Intent.createChooser(i, "Compartir con..."));
                                }
                            });
                            if(numCartas>0){
                                carta.setText("Carta");
                                carta.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        getCarta();
                                    }
                                });
                            }else{
                                carta.setVisibility(View.GONE);
                            }
                            if(numMenus>0){
                                switch (numMenus){
                                    case 1:
                                        menu_1.setText("Menu 1");
                                        menu_2.setVisibility(View.GONE);
                                        menu_3.setVisibility(View.GONE);
                                        menu_4.setVisibility(View.GONE);
                                        menu_5.setVisibility(View.GONE);
                                        break;
                                    case 2:
                                        menu_1.setText("Menu 1");
                                        menu_2.setText("Menu 2");
                                        menu_3.setVisibility(View.GONE);
                                        menu_4.setVisibility(View.GONE);
                                        menu_5.setVisibility(View.GONE);
                                        break;
                                    case 3:
                                        menu_1.setText("Menu 1");
                                        menu_2.setText("Menu 2");
                                        menu_3.setText("Menu 3");
                                        menu_4.setVisibility(View.GONE);
                                        menu_5.setVisibility(View.GONE);
                                        break;
                                    case 4:
                                        menu_1.setText("Menu 1");
                                        menu_2.setText("Menu 2");
                                        menu_3.setText("Menu 3");
                                        menu_4.setText("Menu 4");
                                        menu_5.setVisibility(View.GONE);
                                        break;
                                    case 5:
                                        menu_1.setText("Menu 1");
                                        menu_2.setText("Menu 2");
                                        menu_3.setText("Menu 3");
                                        menu_4.setText("Menu 4");
                                        menu_5.setText("Menu 5");
                                        break;
                                }
                                menu_1.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        getMenu(0);
                                    }
                                });
                                menu_2.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        getMenu(1);
                                    }
                                });
                                menu_3.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        getMenu(2);
                                    }
                                });
                                menu_4.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        getMenu(3);
                                    }
                                });
                                menu_5.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        getMenu(4);
                                    }
                                });
                            }else{
                                menu_1.setVisibility(View.GONE);
                                menu_2.setVisibility(View.GONE);
                                menu_3.setVisibility(View.GONE);
                                menu_4.setVisibility(View.GONE);
                                menu_5.setVisibility(View.GONE);
                            }
                            if(numCartas==0 && numMenus==0){
                                TextView no_menu = (TextView) activity.findViewById(R.id.no_menus);
                                no_menu.setText("El restaurante aún no ha publicado ninguna carta ni menú");
                            }

                            icono_solicitud.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    scrollView.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            //X,Y are scroll positions untill where you want scroll down
                                            scrollView.scrollTo(0, solicitud_grupos.getTop());
                                        }
                                    });
                                }
                            });

                            icono_comentarios.setOnClickListener(new View.OnClickListener(){
                               public void onClick(View v){
                                   scrollView.scrollTo(0, scrollView.getBottom());
                                   scrollView.post(new Runnable()
                                   {
                                       @Override
                                       public void run()
                                       {
                                           scrollView.scrollTo(0, scrollView.getBottom());
                                       }
                                   });
                               }
                            });

                            mYear = C.get(Calendar.YEAR);
                            mMonth = C.get(Calendar.MONTH);
                            mDay = C.get(Calendar.DAY_OF_MONTH);
                            editText_fecha_solicitud.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //activity.showDialog(DATE_ID);
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
                                            editText_fecha_solicitud.setText(a);
                                        }
                                    };
                                    DatePickerDialog d = new DatePickerDialog(activity, dpd, mYear ,mMonth, mDay);
                                    d.show();

                                }
                            });

                            Typeface TF = Typeface.createFromAsset(activity.getAssets(),fira_sans_regular);
                            Typeface TF2 = Typeface.createFromAsset(activity.getAssets(),fira_sans_semibold);
                            Typeface TF3 = Typeface.createFromAsset(activity.getAssets(),fira_sans_light);
                            textViewNombre.setTypeface(TF2);
                            textViewDireccion.setTypeface(TF3);
                            textViewLocalidad.setTypeface(TF3);
                            textViewTelefono.setTypeface(TF2);
                            textViewCocina.setTypeface(TF);
                            textViewCaracteristico.setTypeface(TF);
                            textViewValoracion.setTypeface(TF2);

                            Picasso.with(getActivity()).load(urlImagen).into(background);
                            textViewNombre.setText(nombre_restaurante);
                            textViewDireccion.setText(direccion);
                            textViewLocalidad.setText(localidad);
                            telefono = telefono.substring(0, 3) + " " + telefono.substring(3, 6) + " " + telefono.substring(6, 9) + " C. Reservas";
                            SpannableString ss1=  new SpannableString(telefono);
                            ss1.setSpan(new RelativeSizeSpan(1.5f), 0,11, 0); // set size
                            textViewTelefono.setText(ss1);
                            textViewCocina.setText(cocina);
                            textViewCaracteristico.setText(caracteristico);
                            textViewValoracion.setText(valoracion);

                            loadComentarios();

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

        if (appBarLayout != null) {
            appBarLayout.setTitle(nombre_restaurante);
        }
    }

    private void getCarta() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    // Parsea json
                    TableLayout texto_menu = (TableLayout) getActivity().findViewById(R.id.texto_menu);
                    texto_menu.setVisibility(View.GONE);
                    TableLayout texto_carta = (TableLayout) getActivity().findViewById(R.id.texto_carta);
                    texto_carta.setVisibility(View.VISIBLE);
                    TextView titulo_carta = (TextView) getActivity().findViewById(R.id.titulo_carta);
                    SpannableString content = new SpannableString("Carta");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    titulo_carta.setText(content);
                    TextView platos_carta = (TextView) getActivity().findViewById(R.id.platos_carta);
                    platos_carta.setText(response);
                }
        }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
        }) {
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> map = new HashMap<String, String>();
            map.put("consulta", "2");
            map.put("idrestaurante", String.valueOf(id_restaurante));
            return map;
            }
        };
        requestQueue.add(request);
    }

    private void solicitarMenu() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA","entra");
                editText_fecha_solicitud.setVisibility(View.GONE);
                editText_personas_solicitud.setVisibility(View.GONE);
                editText_presupuesto_solicitud.setVisibility(View.GONE);
                editText_comentarios_solicitud.setVisibility(View.GONE);
                solicitud_grupos.setVisibility(View.GONE);
                TextView subtitulo = (TextView) getActivity().findViewById(R.id.subtitulo_solicitud);
                subtitulo.setText("En estos momentos estamos tramitando su solicitud. En breve recibirá un correo electrónico de confirmación o una llamada para acabar de tramitar la reserva.\n\nMuchas gracias por confiar en Restauranis");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("consulta", "4");
                map.put("idrestaurante", String.valueOf(id_restaurante));
                map.put("cliente", nombre_usuario);
                map.put("telefono", telefono);
                map.put("email", email);
                map.put("presupuesto", presupuesto_solicitud);
                map.put("personas", personas_solicitud);
                map.put("comentarios", comentarios_solicitud);
                map.put("fecha", fecha_solicitud);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void getMenu(final int menu) {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray j = new JSONArray(response);

                    // Parsea json
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            // Parsea json
                            TableLayout texto_carta = (TableLayout) getActivity().findViewById(R.id.texto_carta);
                            texto_carta.setVisibility(View.GONE);
                            TableLayout texto_menu = (TableLayout) getActivity().findViewById(R.id.texto_menu);
                            texto_menu.setVisibility(View.VISIBLE);
                            TextView nombre_menu = (TextView) getActivity().findViewById(R.id.nombre_menu);
                            SpannableString content = new SpannableString(obj.getString("nombreMenu"));
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            nombre_menu.setText(content);
                            TextView primeros_platos = (TextView) getActivity().findViewById(R.id.primeros_platos);
                            primeros_platos.setText(obj.getString("primerosPlatos").replace("<br/>", "\n"));
                            TextView segundos_platos = (TextView) getActivity().findViewById(R.id.segundos_platos);
                            segundos_platos.setText(obj.getString("segundosPlatos").replace("<br/>", "\n"));
                            TextView postres = (TextView) getActivity().findViewById(R.id.postres);
                            postres.setText(obj.getString("postres").replace("<br/>", "\n"));
                            TextView extras = (TextView) getActivity().findViewById(R.id.extras);
                            extras.setText(obj.getString("extras").replace("<br/>", "\n"));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (JSONException e) {
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
                map.put("consulta", "3");
                map.put("idrestaurante", String.valueOf(id_restaurante));
                map.put("menu", String.valueOf(menu));
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadComentarios() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA",response);
                try {
                    JSONArray j = new JSONArray(response);
                    // Parsea json
                    for (int i = 0; i <= j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            // Parsea json
                            comentarios = obj.getString("comentarios");
                            textViewComentarios = (TextView) getActivity().findViewById(R.id.titulo_comentarios);
                            textViewComentarios.setText("Comentarios ("+comentarios+")");

                            if(comentarios.equals("0")){
                                TableLayout tl = (TableLayout) getActivity().findViewById(R.id.tabla_comentarios);
                                /* Create a new row to be added. */
                                TableRow tr = new TableRow(getContext());
                                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                /* Create a Button to be the row-content. */
                                TextView a = new TextView(getContext());
                                a.setText("Se el primero en opinar!");
                                a.setTextSize(16);
                                a.setGravity(Gravity.CENTER);
                                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                                a.setLayoutParams(params);
                                tr.addView(a);
                                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            }else{
                                String nombre = obj.getString("usuario");
                                String texto = obj.getString("texto");
                                String fecha = obj.getString("fecha");
                                String comida = obj.getString("comida");
                                String ambiente = obj.getString("ambiente");
                                String servicio = obj.getString("servicio");
                                String precio = obj.getString("precio");

                                /* Find Tablelayout defined in main.xml */
                                TableLayout tl = (TableLayout) getActivity().findViewById(R.id.tabla_comentarios);
                                /* Create a new row to be added. */
                                TableRow tr = new TableRow(getContext());
                                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                /* Create a Button to be the row-content. */
                                TextView a = new TextView(getContext());
                                a.setText(nombre+"\n"+fecha);
                                a.setGravity(Gravity.CENTER);
                                a.setBackgroundResource(R.color.gris);
                                a.setTextColor(getResources().getColor(R.color.blanco));
                                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 225);
                                params.setMargins(0,20,0,0);
                                a.setLayoutParams(params);
                                a.setPadding(10,10,10,10);

                                TextView b = new TextView(getContext());
                                String valoracion = "COMIDA: "+comida+" | PRECIO: "+precio+" | AMBIENTE: "+ambiente+" | SERVICIO: "+servicio+"\n"+texto;
                                SpannableString ss1=  new SpannableString(valoracion);
                                b.setTextSize(10);
                                ss1.setSpan(new RelativeSizeSpan(1.4f), 50,valoracion.length(), 0); // set size
                                b.setText(ss1);
                                TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                                params2.setMargins(0,-50,0,0);
                                b.setLayoutParams(params2);
                                b.setPadding(10,0,10,10);

                                /* Add Button to row. */
                                tr.addView(a);
                                tr.addView(b);
                                /* Add row to TableLayout. */
                                //tr.setBackgroundResource(R.drawable.sf_gradient_03);
                                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }catch (JSONException e) {
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
                map.put("consulta", "5");
                map.put("idrestaurante", String.valueOf(id_restaurante));
                return map;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_miniweb, container, false);
        return rootView;
    }

    private String nombreUrl(String nombre){
        nombre = nombre.replace(" ", "-");
        nombre = nombre.replace("-&-", "-and-");
        nombre = nombre.replace("'", "_");
        nombre = nombre.replace("?", "");
        nombre = nombre.replace("+", "");
        nombre = nombre.replace("??", "");
        nombre = nombre.replace("'", "");
        nombre = nombre.replace("!", "");
        nombre = nombre.replace("ñ", "n");
        nombre = nombre.replace("¿", "");
        nombre = nombre.toLowerCase();
        nombre = Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return nombre;
    }

}
