package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    SharedPreferences onBoardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

        if(isFirstTime){
            SharedPreferences.Editor editor = onBoardingScreen.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            startActivity(new Intent(MainActivity.this, OnBoardingActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(MainActivity.this, UserLogin.class));
            finish();
        }

    }

}