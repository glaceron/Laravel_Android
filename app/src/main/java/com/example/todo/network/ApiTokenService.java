package com.example.todo.network;

import com.example.todo.model.DeleteResponse;
import com.example.todo.model.Email;
import com.example.todo.model.AddResponse;
import com.example.todo.model.EmailResponse;
import com.example.todo.model.LogoutResponse;
import com.example.todo.model.Reserva;
import com.example.todo.model.GetReservasResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiTokenService {

    @POST("api/logout")
    Call<LogoutResponse> logout(
            //@Header("Authorization") String token
    );

    @GET("api/reservas")
    Call<GetReservasResponse> getReservas(
            //@Header("Authorization") String token
    );

    @POST("api/reservas")
    Call<AddResponse> createReserva(
            //@Header("Authorization") String token,
            @Body Reserva reserva);

    @PUT("api/reservas/{id}")
    Call<AddResponse> updateReserva(
            //@Header("Authorization") String token,
            @Body Reserva reserva,
            @Path("id") int id);

    @DELETE("api/reservas/{id}")
    Call<DeleteResponse> deleteReserva(
            //@Header("Authorization") String token,
            @Path("id") int id);

    @POST("api/email")
    Call<EmailResponse> sendEmail(@Body Email email);
}

