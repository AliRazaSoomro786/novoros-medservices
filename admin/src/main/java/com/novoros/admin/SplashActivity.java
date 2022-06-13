package com.novoros.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.novoros.common.R.layout.activity_splash);


        Executors.newSingleThreadScheduledExecutor().schedule(() ->{
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3000, TimeUnit.MILLISECONDS);
    }
}
