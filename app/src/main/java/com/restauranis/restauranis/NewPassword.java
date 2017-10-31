package com.restauranis.restauranis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class NewPassword extends AppCompatActivity {

    private TextView textNewPassword;
    private EditText password_recupera, confirm_password;
    private Button button_recupera;
    RequestQueue requestQueue;
    String fira_sans = "font/fira_sans_regular.ttf";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_password);

        String email = getIntent().getStringExtra("email");
        email = email.substring(33, email.length());

        textNewPassword = (TextView) findViewById(R.id.text_recupera);
        password_recupera = (EditText) findViewById(R.id.password_recupera);
        confirm_password = (EditText) findViewById(R.id.confirm_password_recupera);
        button_recupera = (Button) findViewById(R.id.recupera);

        Typeface TF = Typeface.createFromAsset(getAssets(),fira_sans);
        textNewPassword.setTypeface(TF);
        password_recupera.setTypeface(TF);
        confirm_password.setTypeface(TF);

        textNewPassword.setText("Recuperación contraseña para "+email);

        confirm_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    confirm_password.performClick();
                    recupera();
                    return true;
                }
                return false;
            }
        });
        button_recupera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recupera();
            }
        });

        requestQueue = Volley.newRequestQueue(this);
    }

    private void recupera() {
        String url = "https://www.restauranis.com/consultas-app.php";
        final String password = password_recupera.getText().toString();
        final String confirm = confirm_password.getText().toString();
        String email = getIntent().getStringExtra("email");
        email = email.substring(33, email.length());
        if(password.length()<6){
            password_recupera.setError("La contraseña debe tener al menos 6 dígitos");
            password_recupera.requestFocus();
        }else if(!confirm.equals(password)){
            confirm_password.setError("Las contraseñas no coinciden");
            confirm_password.requestFocus();
        }else {
            final String finalEmail = email;
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("AAA",response);
                    if (response.equals("1")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NewPassword.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Email", finalEmail);
                        editor.apply();
                        Intent intent = new Intent(NewPassword.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        confirm_password.setError("Error en el Servidor. Pruébelo de nuevo por favor");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    confirm_password.setError("Error en el Servidor. Pruébelo de nuevo por favor");
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    Log.d("AAA", finalEmail);
                    map.put("consulta", "4");
                    map.put("email", finalEmail);
                    map.put("password", password);

                    return map;
                }
            };
            requestQueue.add(request);
        }
    }
}
