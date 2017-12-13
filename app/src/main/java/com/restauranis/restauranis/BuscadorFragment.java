package com.restauranis.restauranis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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


public class BuscadorFragment extends Fragment {

    private TextView resultados, cocina_1, cocina_2, cocina_3, cocina_4, cocina_5, cocina_6, cocina_7, cocina_8;
    private String tipo, cocina_predeterminada, idCocina;
    public List<Restaurant> restaurantes = new ArrayList<>();
    private RecyclerView recyclerView;
    RequestQueue requestQueue, requestQueue2;
    private String localidad, nombre_restaurante, cocina, urlImagen, precio, valoracion, resultado, nombre_resultado;
    private int id_restaurante, id_resultado;
    private String url = "https://www.restauranis.com/consultas-app.php";
    private ProgressBar itemProgressBar;
    private int limit = 0;
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private AutoCompleteTextView restaurantes_buscador;
    private TableLayout cocinas;

    public BuscadorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        localidad = preferences.getString("Localidad", "");
        tipo = getArguments().getString("tipo");
        cocina_predeterminada = getArguments().getString("predeterminada");
        idCocina = getArguments().getString("idCocina");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_buscador, container, false);
        resultados = (TextView) rootView.findViewById(R.id.resultados);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.restaurantes);
        restaurantes_buscador = (AutoCompleteTextView) rootView.findViewById(R.id.autocomplete_buscador);
        cocinas = (TableLayout) rootView.findViewById(R.id.cocinas);
        cocina_1 = (TextView) rootView.findViewById(R.id.cocina_1);
        cocina_2 = (TextView) rootView.findViewById(R.id.cocina_2);
        cocina_3 = (TextView) rootView.findViewById(R.id.cocina_3);
        cocina_4 = (TextView) rootView.findViewById(R.id.cocina_4);
        cocina_5 = (TextView) rootView.findViewById(R.id.cocina_5);
        cocina_6 = (TextView) rootView.findViewById(R.id.cocina_6);
        cocina_7 = (TextView) rootView.findViewById(R.id.cocina_7);
        cocina_8 = (TextView) rootView.findViewById(R.id.cocina_8);

        if (tipo.equals("valorados")) {
            resultados.setText("Restaurantes MEJORES VALORADOS");
        } else if (tipo.equals("precio")) {
            resultados.setText("Restaurantes por Precio");
        } else if(tipo.equals("cocina")){
            resultados.setText("Tipos de cocina más buscados");
            loadCocinas();
            loadCocinasPredeterminadas();
            recyclerView.setVisibility(View.GONE);
            cocinas.setVisibility(View.VISIBLE);
        }else if(tipo.equals("predeterminada")) {
            resultados.setText("Restaurantes con cocina "+cocina_predeterminada);
            loadRestaurantesCocina();
        }else{
            resultados.setText("Restaurantes " + tipo);
        }
        if(!tipo.equals("cocina")){
            loadAutocompletar();
            loadRestaurantes();
        }

        itemProgressBar = (ProgressBar) rootView.findViewById(R.id.item_progress_bar);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //code for portrait mode
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);


        } else {
            //code for landscape mode
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4,
                    StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);
        }

        if(!tipo.equals("recomendados") && !tipo.equals("premium") && !tipo.equals("cocina"))
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    StaggeredGridLayoutManager mLayoutManager =
                            (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                    if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                        pastVisibleItems = firstVisibleItems[0];
                    }

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            addDataToList();
                        }
                    }
                }
            });

        return rootView;
    }

    private void loadRestaurantes() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA","response 7 "+response);
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

                            restaurantes.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerView.setAdapter(new BuscadorFragment.SimpleItemRecyclerViewAdapter(restaurantes));
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
                map.put("consulta", "7");
                map.put("tipo",tipo);
                map.put("limit", String.valueOf(limit));
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    public void loadCocinas(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA","response 8 "+response);
                try{
                    JSONArray j= new JSONArray(response);

                    String[] results = new String[j.length()];
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            resultado = obj.getString("resultado");
                            nombre_resultado = obj.getString("nombre");
                            id_resultado = obj.getInt("id");
                            results[i]=nombre_resultado;

                            restaurantes_buscador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                                    //... your stuff
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, results);
                    restaurantes_buscador.setAdapter(adapter);
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
                map.put("consulta", "8");
                map.put("buscando", "cocina");
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadCocinasPredeterminadas() {
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA","response 9 "+response);
                try{
                    JSONArray j= new JSONArray(response);

                    // Parsea json
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            final String cocina = obj.getString("nombre");
                            final String id = obj.getString("id");
                            switch (i){
                                case 0:
                                    cocina_1.setText(cocina);
                                    cocina_1.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 1:
                                    cocina_2.setText(cocina);
                                    cocina_2.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 2:
                                    cocina_3.setText(cocina);
                                    cocina_3.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 3:
                                    cocina_4.setText(cocina);
                                    cocina_4.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 4:
                                    cocina_5.setText(cocina);
                                    cocina_5.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 5:
                                    cocina_6.setText(cocina);
                                    cocina_6.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 6:
                                    cocina_7.setText(cocina);
                                    cocina_7.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                                case 7:
                                    cocina_8.setText(cocina);
                                    cocina_8.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(getContext(), Buscador.class);
                                            intent.putExtra("tipo", "predeterminada");
                                            intent.putExtra("predeterminada", cocina);
                                            intent.putExtra("idCocina", id);
                                            startActivity(intent);
                                        }
                                    });
                                    break;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerView.setAdapter(new BuscadorFragment.SimpleItemRecyclerViewAdapter(restaurantes));
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
                map.put("consulta", "9");
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private void loadRestaurantesCocina() {
        Log.d("AAAA","entra");
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA","response 10 "+response);
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

                            restaurantes.add(rest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    recyclerView.setAdapter(new BuscadorFragment.SimpleItemRecyclerViewAdapter(restaurantes));
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
                map.put("consulta", "10");
                map.put("idcocina",idCocina);
                map.put("localidad",localidad);
                return map;
            }
        };
        requestQueue.add(request);
    }

    public void loadAutocompletar(){
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AAAA","response 8 "+response);
                try{
                    JSONArray j= new JSONArray(response);

                    final String[] results = new String[j.length()];
                    for (int i = 0; i < j.length(); i++) {
                        try {
                            JSONObject obj = j.getJSONObject(i);
                            resultado = obj.getString("resultado");
                            nombre_resultado = obj.getString("nombre");
                            id_resultado = obj.getInt("id");
                            results[i]=nombre_resultado;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    restaurantes_buscador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                            Log.d("AAAA", String.valueOf(position));
                            Log.d("AAAA", String.valueOf(id));
                            Log.d("AAAA", String.valueOf(parent));
                            Log.d("AAAA", String.valueOf(id_resultado));
                            Log.d("AAAA", restaurantes_buscador.getText().toString());
                            /*Intent intent = new Intent(getContext(), Miniweb.class);
                            intent.putExtra("idrestaurante", results[position]);
                            startActivity(intent);*/
                        }
                    });
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, results);
                    restaurantes_buscador.setAdapter(adapter);
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
                map.put("consulta", "8");
                map.put("buscando", "restaurantes");
                return map;
            }
        };
        requestQueue.add(request);


    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<BuscadorFragment.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Restaurant> mValues;
        private Context mContext;

        public SimpleItemRecyclerViewAdapter(List<Restaurant> items) {
            this.mValues = items;
        }

        @Override
        public BuscadorFragment.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurantes, parent, false);
            return new BuscadorFragment.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BuscadorFragment.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.textViewNombre.setText(mValues.get(position).nombre);
            holder.textViewNombre.setTextSize(15);
            holder.textViewCocina.setText(mValues.get(position).cocina);
            holder.textViewCocina.setTextSize(13);
            holder.textViewPrecio.setText(mValues.get(position).precio + "€");
            holder.textViewPrecio.setTextSize(13);
            holder.textViewNota.setText(mValues.get(position).valoracion);
            Picasso.with(getContext()).load(mValues.get(position).urlImage).into(holder.imageView);

            holder.mView.setTag(mValues.get(position).id);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = (int) v.getTag();
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Miniweb.class);
                    intent.putExtra("idrestaurante", currentPos);
                    intent.putExtra("tipo", tipo);
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

    private void addDataToList() {
        itemProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                limit = limit+20;
                Log.d("AAAA", String.valueOf(limit));
                loadRestaurantes();
                itemProgressBar.setVisibility(View.GONE);
            }
        }, 1500);

    }
}
