package com.restauranis.restauranis;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joan on 23/10/2017.
 */

public class Register extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private EditText editText_email, editText_password, email_forgot, email_register, password_register, confirm_password_register, nombre_register, localidad_register;
    private TextView textRegister, textOlvidado, text_forgot;
    private Button forgot_button;
    RequestQueue requestQueue;
    String fira_sans = "font/fira_sans_regular.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        textRegister = (TextView) findViewById(R.id.text_register);
        editText_email = (EditText) findViewById(R.id.email);
        editText_password = (EditText) findViewById(R.id.password);
        email_register = (EditText) findViewById(R.id.email_register);
        password_register = (EditText) findViewById(R.id.password_register);
        confirm_password_register = (EditText) findViewById(R.id.confirm_password_register);
        textOlvidado = (TextView) findViewById(R.id.olvidado);
        nombre_register = (EditText) findViewById(R.id.nombre_register);
        localidad_register = (EditText) findViewById(R.id.localidad);
        final Button buttonLogin = (Button) findViewById(R.id.login);
        final Button buttonRegister = (Button) findViewById(R.id.register);

        Typeface TF = Typeface.createFromAsset(getAssets(),fira_sans);
        textRegister.setTypeface(TF);
        editText_email.setTypeface(TF);
        editText_password.setTypeface(TF);
        email_register.setTypeface(TF);
        password_register.setTypeface(TF);
        confirm_password_register.setTypeface(TF);
        textOlvidado.setTypeface(TF);
        localidad_register.setTypeface(TF);

        localidad_register.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });

        editText_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    editText_password.performClick();
                    login();
                    return true;
                }
                return false;
            }
        });

        localidad_register.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    localidad_register.performClick();
                    register();
                    return true;
                }
                return false;
            }
        });

        textOlvidado.setPaintFlags(textOlvidado.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textOlvidado.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //DO you work here
                //View view = View.inflate(Register.this, R.layout.forgot_pass, null);
                final Dialog dialog = new Dialog(Register.this);
                dialog.setContentView(R.layout.forgot_pass);
                dialog.setTitle("Contraseña olvidada");
                dialog.show();

                forgot_button = (Button) dialog.findViewById(R.id.button_forgot_pass);
                email_forgot = (EditText) dialog.findViewById(R.id.email_forgot);
                text_forgot = (TextView) dialog.findViewById(R.id.text_forgot);
                forgot_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        //OnCLick Stuff
                        if (email_forgot.getText().toString().equals("")) {
                            email_forgot.setError("Email incorrecto");
                        } else {
                            forgot();
                        }
                    }
                });
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register();
            }
        });

        requestQueue = Volley.newRequestQueue(this);
    }

    private void login() {
        String url = "https://www.restauranis.com/consultas-app.php";
        final String email = editText_email.getText().toString();
        final String password = editText_password.getText().toString();
        if (email.equals("")) {
            editText_email.setError("El email no puede estar vacío");
        } else if (password.length() < 6) {
            editText_password.setError("La contraseña debe tener al menos 6 caracteres");
        } else {

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("1")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Register.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Email", email);
                        editor.apply();
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                    } else if (response.equals("2")) {
                        editText_email.setError("Email incorrecto");
                    } else if (response.equals("3")) {
                        editText_password.setError("Contraseña incorrecta");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    editText_email.setError("Error en el Servidor. Pruebelo de nuevo por favor");
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("consulta", "0");
                    map.put("email", email);
                    map.put("password", password);

                    return map;
                }
            };
            requestQueue.add(request);
        }
    }

    private void register() {
        String url = "https://www.restauranis.com/consultas-app.php";
        final String email = email_register.getText().toString();
        final String password = password_register.getText().toString();
        final String confirm_password = confirm_password_register.getText().toString();
        final String nombre = nombre_register.getText().toString();
        final String localidad = localidad_register.getText().toString();

        /*String mPhoneNumber = null;

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }
        mPhoneNumber = tMgr.getLine1Number();
        //final String mPhoneNumber = tMgr.getLine1Number();
        Log.d("AAAA",mPhoneNumber);*/
        if(email.equals("")) {
            email_register.setError("Email incorrecto");
            email_register.requestFocus();
        }else if(password.length()<6){
            password_register.setError("La contraseña debe tener al menos 6 dígitos");
            password_register.requestFocus();
        }else if(!confirm_password.equals(password)){
            confirm_password_register.setError("Las contraseñas no coinciden");
            confirm_password_register.requestFocus();
        }else if(localidad.equals("")){
            localidad_register.setError("Es necesario saber la localidad");
            localidad_register.requestFocus();
        }else {

            //final String finalMPhoneNumber = mPhoneNumber;
            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("1")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Register.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Email", email);
                        editor.apply();
                        Intent intent = new Intent(Register.this, MainActivity.class);
                        startActivity(intent);
                    } else if (response.equals("2")) {
                        email_register.setError("Este email ya existe en la base de datos");
                        email_register.requestFocus();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    email_register.setError("Error en el Servidor. Pruebelo de nuevo por favor");
                    email_register.requestFocus();
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("consulta", "1");
                    map.put("email", email);
                    map.put("nombre", nombre);
                    //map.put("telefono", finalMPhoneNumber);
                    map.put("pass", password);

                    return map;
                }
            };
            requestQueue.add(request);
        }
    }

    private void forgot(){
        String url = "https://www.restauranis.com/consultas-app.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {

                    String subject = "Reestablecer contraseña";
                    String url = "https://restauranis/new_password/" + email_forgot.getText().toString();
                    String message = "Para reestablecer tu contraseña pulsa aqui: " + url;

                    //Creating SendMail object
                    EnvioMail sm = new EnvioMail(Register.this, email_forgot.getText().toString(), subject, message);

                    //Executing sendmail to send email
                    sm.execute();

                    email_forgot.setVisibility(View.INVISIBLE);
                    forgot_button.setVisibility(View.INVISIBLE);
                    text_forgot.setText("Email enviado! Revise su correo para reestablecer la contraseña.");
                    text_forgot.setVisibility(View.VISIBLE);

                } else if (response.equals("2")) {
                    email_forgot.setError("Este email no existe en nuestra base de datos");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                email_forgot.setError("Error en el Servidor. Pruebelo de nuevo por favor");
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("consulta", "2");
                map.put("email", email_forgot.getText().toString());

                return map;
            }
        };
        requestQueue.add(request);
    }

}
