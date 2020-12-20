package com.example.project1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Api {

    @POST("signup")
    Call<SignUpResponse> signup(@Body UserSignUpRequest request);

    @POST("login")
    Call<LoginResponse> login(
            @Body UserLoginRequest request
    );

}
