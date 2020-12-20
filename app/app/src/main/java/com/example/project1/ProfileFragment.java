package com.example.project1;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.internal.Util;

public class ProfileFragment extends Fragment {

    TextView profile_phone, profilePublicKey;
    Button viewBalancebtn;
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";
    String smsString = null, encryptText = null, actualText =null;
    KeyStore keyStore = null;
    EnCryptor encryptor;
    String passforSMSEncruption ;

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        profile_phone = view.findViewById(R.id.profile_phone);
        profilePublicKey = view.findViewById(R.id.text_view_public_key);
        viewBalancebtn = view.findViewById(R.id.view_balance_btn);

        if(mContext != null) {
            String p = Utils.getStringFromUserData(mContext, "phone");
            String pk = UserLogin.publicKey;
            profile_phone.setText(p);
            profilePublicKey.setText(pk);
        }

        UserLogin userLogin = new UserLogin();
        passforSMSEncruption = userLogin.smsEncryptionPass;

        viewBalancebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryptText = "retrievebalance";
                try {
                    smsString = encrypt(encryptText, passforSMSEncruption);
                    passforSMSEncruption = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendSMS();
            }
        });

        return view;
    }

    private void sendSMS() {
        Uri uri = Uri.parse("smsto:+12517650405");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", smsString);
        startActivity(intent);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encodedBytes = cipher.doFinal(data.getBytes());
        String encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        return encryptedBase64Encoded;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        MessageDigest md  = MessageDigest.getInstance("MD5");
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        return key;
    }

    private String decrypt(String encryptedText, String password) throws  Exception{
        SecretKeySpec key =  generateKey(password);
        Log.i("check5", String.valueOf(key));
        byte[] decodedVal = Base64.decode(encryptedText, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE,key);
        String decryptedValText = new String(cipher.doFinal(decodedVal));
        Log.i("check7", decryptedValText);
        return decryptedValText;
    }

}
