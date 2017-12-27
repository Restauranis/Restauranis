package com.restauranis.restauranis;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    // Duración en milisegundos que se mostrará el splash
    private final int DURACION_SPLASH = 3000; // 3 segundos
    private ProgressBar mProgress;
    private TextView textView;
    private ObjectAnimator anim;
    String fira_sans = "font/fira_sans_regular.ttf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.cargando);

        Typeface TF = Typeface.createFromAsset(getAssets(),fira_sans);
        textView.setTypeface(TF);

        Intent intent = getIntent();
        final Uri data = intent.getData();


        anim = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        mostrarProgress();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = preferences.getString("Email", "");

         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.setProgress(DURACION_SPLASH);
                if(!isOnlineNet()){
                    AlertDialog alertDialog = new AlertDialog.Builder(SplashScreen.this).create();

                    alertDialog.setTitle("Error de Conexión");

                    alertDialog.setMessage("Parece que su dispositivo no tiene conexión a Internet. Por favor intenta acceder en un rato. \n\nMuchas gracias y disculpe las molestias");

                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    alertDialog.show();
                }
                else if(data!=null) {
                    String url = data.toString();
                    Intent intent = new Intent(SplashScreen.this, NewPassword.class);
                    intent.putExtra("email",url);
                    startActivity(intent);
                    finish();

                }else if(!email.equalsIgnoreCase(""))
                {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashScreen.this, Register.class);
                    startActivity(intent);
                    finish();
                }
            }
        },DURACION_SPLASH);
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private void mostrarProgress(){
        //agregamos el tiempo de la animacion a mostrar
        anim.setDuration(12000);
        anim.setInterpolator(new DecelerateInterpolator());
        //iniciamos el progressbar
        anim.start();
    }
}
