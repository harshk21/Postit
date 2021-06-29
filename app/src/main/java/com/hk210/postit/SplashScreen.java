package com.hk210.postit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;



public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    SharedPreferences sharedPreferences;
    Boolean firstTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh_screen);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean("firstTime",true);

        if(firstTime){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;
                    editor.putBoolean("firstTime",firstTime);
                    editor.apply();
                    Intent i = new Intent(SplashScreen.this,AnimationActivity.class);
                    startActivity(i);
                    finish();
                }
            },SPLASH_TIME_OUT);}
        else{
            Intent u = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(u);
            finish();

        }
    }

}
