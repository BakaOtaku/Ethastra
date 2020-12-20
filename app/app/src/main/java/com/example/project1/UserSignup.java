package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;

public class UserSignup extends AppCompatActivity {


    EditText userName, userPhone, userPassword;
    Button signup_btn;
    TextView registeredlogin;

    private String BASE_URL = "https://offline-trading.herokuapp.com/signup";

    OkHttpClient client = new OkHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        userName = findViewById(R.id.editTextSignupName);
        userPhone = findViewById(R.id.editTextSignupPhone);
        userPassword = findViewById(R.id.editTextTextSignupPassword);

        signup_btn = findViewById(R.id.signup_btn);
        registeredlogin = findViewById(R.id.registeredlogintextview);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignUp();
            }
        });

        registeredlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignup.this, UserLogin.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    private void userSignUp(){
        String name = userName.getText().toString().trim();
        String phone = userPhone.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        BASE_URL += "/"+name+"/"+phone+"/"+password;
        String s = null;
        try {
            s = run(BASE_URL);
            Log.i("check34", s);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Utils.saveStringInUserData(getApplicationContext(), "name", name);
        Utils.saveStringInUserData(getApplicationContext(), "phone", phone);
        Utils.saveStringInUserData(getApplicationContext(), "pass", password);

        if(s.equals("Success")) {

            if (name.isEmpty()) {
                userName.setError("Name is required");
                userName.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                userPhone.setError("Phone is required");
                userPhone.requestFocus();
                return;
            }

            if (phone.length() != 10) {
                userPhone.setError("Enter a valid Phone Number");
                userPhone.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                userPassword.setError("Password required");
                userPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                userPassword.setError("Password should be atleast 6 character long");
                userPassword.requestFocus();
                return;
            }

            startActivity(new Intent(UserSignup.this, UserLogin.class));
            finish();
        }else if(s.equals("Already exists")){
            Toast.makeText(this, "Already Registered", Toast.LENGTH_SHORT).show();
        }
    }

    public String run(String final_url) throws IOException {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(final_url)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
