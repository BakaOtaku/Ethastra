package com.example.project1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://offline-trading.herokuapp.com/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private RetrofitClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();

                                Request.Builder requestBuilder = original.newBuilder()
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }
                ).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }

//    public static final String SIGNUP = "signup";
//    public static final String LOGIN = "login";
//
//    public static final String NAME = "name";
//    public static final String PHONE = "phone";
//
//    private static RetrofitClient mInstance;
//    private Retrofit retrofit;
//
//    private RetrofitClient() {
////        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(
////                        new Interceptor() {
////                            @Override
////                            public Response intercept(Chain chain) throws IOException {
////                                Request original = chain.request();
////
////                                Request.Builder requestBuilder = original.newBuilder()
////                                        .method(original.method(), original.body());
////
////                                Request request = requestBuilder.build();
////                                return chain.proceed(request);
////                            }
////                        }
////                ).build();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
////                .client(okHttpClient)
//                .build();
//    }
//
//    public static synchronized RetrofitClient getInstance() {
//        if (mInstance == null) {
//            mInstance = new RetrofitClient();
//        }
//        return mInstance;
//    }
//
//    public Api getApi() {
//        return retrofit.create(Api.class);
//    }
}
