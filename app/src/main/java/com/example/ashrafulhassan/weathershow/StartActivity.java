package com.example.ashrafulhassan.weathershow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Hassan M Ashraful on 4/17/2018.
 * Jr Software Developer
 * Innovizz Technology
 */

public class StartActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private ImageView splash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        getSupportActionBar().hide();
        splash = findViewById(R.id.splash);
        /*Glide.with(getApplicationContext())
                .load(R.drawable.inno).asGif()
                .into(splash);*/

        Glide.with(getApplicationContext()).load(R.drawable.innovizz)
                .fitCenter()
                .into(splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);

    }
}
