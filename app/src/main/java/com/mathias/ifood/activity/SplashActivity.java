package com.mathias.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.mathias.ifood.R;

public class SplashActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               abrirAutenticacao();
            }
        }, 3000 );
    }



    private void abrirAutenticacao() {
        Intent i = new Intent(SplashActivity.this,AutenticacaoActivity.class);
        startActivity(i);
        finish();
    }
}