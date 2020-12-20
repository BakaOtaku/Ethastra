package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class UserLogin extends AppCompatActivity {

    static String smsEncryptionPass, publicKey;
    EditText editTextPhone, editTextPassword;
    Button loginButton;
    private String BASE_URL = "https://offline-trading.herokuapp.com/login";
    private EnCryptor encryptor;
    private DeCryptor decryptor;
    String smsEnPass = null;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        editTextPhone = findViewById(R.id.editTextSignupPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.login_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            encryptor = new EnCryptor();
            decryptor = new DeCryptor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userLogin();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            storePassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signupBtnTemp(View view) {
        Intent intent = new Intent(this, UserSignup.class);
        startActivity(intent);
        finish();
    }

    private void storePassword() throws Exception {

        try {
            final byte[] encryptedText = encryptor.encryptText("world", smsEnPass );

            String encryptedPassword = Base64.encodeToString(encryptedText, Base64.DEFAULT);
            Utils.saveStringInSP(this, "password", encryptedPassword);
            Utils.saveStringInSP(this, "encruptionIV", Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT));
            Utils.saveStringInSP(this, "encryption", Base64.encodeToString(encryptor.getEncryption(), Base64.DEFAULT));
            smsEncryptionPass = decryptor.decryptData("world", encryptor.getEncryption(), encryptor.getIv());
            smsEnPass = null;

        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException ignored) {
        } catch (InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private void userLogin() {

        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        BASE_URL += "/"+phone+"/"+password;

        String temp = null;
        try {
            temp = run(BASE_URL);
            Log.i("check34532", "userLogin: "+BASE_URL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        smsEnPass = temp.substring(temp.indexOf(':')+1);
        publicKey = temp.substring(0,temp.indexOf(':'));

        Utils.saveStringInUserData(getApplicationContext(), "phone", phone);
        Utils.saveStringInUserData(getApplicationContext(), "pass", password);

        if (!temp.equals("Authentication failed")) {
            if (phone.length() != 10) {
                editTextPhone.requestFocus();
                Toast.makeText(UserLogin.this, "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password required");
                editTextPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("Password should be atleast 6 character long");
                editTextPassword.requestFocus();
                return;
            }
            Intent intent = new Intent(UserLogin.this, UserHome.class);
            startActivity(intent);
            finish();
        }else if (temp.equals("Authentication failed")){
            Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
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