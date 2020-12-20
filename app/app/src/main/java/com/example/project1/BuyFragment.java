package com.example.project1;

import android.content.ActivityNotFoundException;
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
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BuyFragment extends Fragment {

    Button sendSMSToBuy;
    EditText amountToBuy;
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";
    private String smsStringToBuy, enSMSstring = null;
    private String passforSMSEncruption;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy,container,false);

        sendSMSToBuy = view.findViewById(R.id.send_sms_btn_to_buy);
        amountToBuy = view.findViewById(R.id.et_amount_to_buy);

        UserLogin userLogin = new UserLogin();

        passforSMSEncruption = userLogin.smsEncryptionPass;
        sendSMSToBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsStringToBuy = amountToBuy.getText().toString().trim();
                try {
                    enSMSstring = encrypt(smsStringToBuy, passforSMSEncruption).trim();
                } catch (Exception e) {
                e.printStackTrace();
            }
                sendSMS(enSMSstring);
            }
        });
        return view;
    }

    private void sendSMS(String smsString) {
        Uri uri = Uri.parse("smsto:+12517650405");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", smsString);
        startActivity(intent);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encodedBytes = cipher.doFinal(data.getBytes());
        String encryptedBase64Encoded =  Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        return encryptedBase64Encoded;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = password.getBytes("UTF-8");
        md.update(bytes, 0, bytes.length);
        byte[] key = md.digest();
        return new SecretKeySpec(key, "AES");
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
