package com.example.project1;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    public Utils() {

    }

    static void saveStringInUserData(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences("UserData", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    static String getStringFromUserData(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences("UserData", MODE_PRIVATE);
        return sp.getString(key, null);
    }

    static  void saveStringInSP(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences("SP", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
    static String getStringFromSP(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences("SP", MODE_PRIVATE);
        return sp.getString(key, null);
    }
}
