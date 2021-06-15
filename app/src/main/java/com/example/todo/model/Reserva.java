package com.example.todo.model;

/**
 * Created by paco
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Reserva implements Serializable {
    private int id;
    private int user;
    private int dia;
    private int mes;
    private String hora_comienzo;
    private String hora_fin;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Reserva(int dia, int mes, String hora_comienzo,String hora_fin) {
        super();
        this.dia = dia;
        this.mes = mes;
        this.hora_comienzo = hora_comienzo;
        this.hora_fin = hora_fin;

    }

    public Reserva() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getHora_comienzo() {
        return hora_comienzo;
    }

    public void setHora_comienzo(String hora_comienzo) {
        this.hora_comienzo = hora_comienzo;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return  hora_comienzo;
    }
}
