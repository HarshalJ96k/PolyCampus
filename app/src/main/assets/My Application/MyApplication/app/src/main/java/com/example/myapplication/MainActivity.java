package com.polycampus.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);
        ImageView ivLogo;
        Animation animtranslate;


        ivLogo=findViewById(R.id.ivsplashscreen);
        animtranslate= AnimationUtils.loadAnimation(MainActivity.this,R.anim.lefttorighttranslate);
         ivLogo.startAnimation(animtranslate);
        ivLogo.setAnimation(animtranslate);
        Handler h= new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        },4000);

    }
}
