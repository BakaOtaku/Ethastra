package com.example.project1;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SendFragment extends Fragment {

    private static final String AES_MODE = "AES/ECB/PKCS5Padding";

    EditText etSenderPublicKey, etAmount;
    Context context;
    Button sendBtn;
    String smsString = null, encryptText = null, actualText =null;
    KeyStore keyStore = null;
    EnCryptor encryptor;
    String passforSMSEncruption ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send,container,false);

        etSenderPublicKey = view.findViewById(R.id.et_sender_public_key);
        etAmount = view.findViewById(R.id.et_amount_to_send);
        sendBtn = view.findViewById(R.id.send_sms_btn);
        try {
            encryptor = new EnCryptor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserLogin userLogin = new UserLogin();
        passforSMSEncruption = userLogin.smsEncryptionPass;

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryptText = etSenderPublicKey.getText().toString().trim() + ":" + etAmount.getText().toString().trim();
                try {
                    smsString = encrypt(encryptText, passforSMSEncruption).trim();
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
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
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
        byte[] decodedVal = Base64.decode(encryptedText, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE,key);
        String decryptedValText = new String(cipher.doFinal(decodedVal));
        return decryptedValText;
    }

}
