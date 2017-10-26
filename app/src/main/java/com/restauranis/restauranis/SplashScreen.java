package com.restauranis.restauranis;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    // Duración en milisegundos que se mostrará el splash
    private final int DURACION_SPLASH = 4000; // 3 segundos
    private ProgressBar mProgress;
    private ObjectAnimator anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        anim = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        mostrarProgress();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = preferences.getString("Email", "");

         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress.setProgress(DURACION_SPLASH);
                if(!email.equalsIgnoreCase(""))
                {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SplashScreen.this, Register.class);
                    startActivity(intent);
                }
                finish();
            }
        },DURACION_SPLASH);
    }

    private void mostrarProgress(){
        //agregamos el tiempo de la animacion a mostrar
        anim.setDuration(14000);
        anim.setInterpolator(new DecelerateInterpolator());
        //iniciamos el progressbar
        anim.start();
    }
}
