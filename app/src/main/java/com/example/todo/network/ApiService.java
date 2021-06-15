package com.example.todo.network;

import com.example.todo.model.LoginResponse;
import com.example.todo.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("api/register")
    Call<RegisterResponse> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("confirmPassword") String confirmPassword);

    //@POST("api/register")
    //Call<RegisterResponse>register(@Body User user);

    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password);

    //@POST("api/login")
    //Call<LoginResponse>login(@Body User user);

}

