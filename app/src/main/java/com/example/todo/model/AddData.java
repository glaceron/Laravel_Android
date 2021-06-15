

package com.example.todo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private Integer user;
    @SerializedName("dia")
    @Expose
    private Integer dia;
    @SerializedName("mes")
    @Expose
    private Integer mes;
    @SerializedName("hora_comienzo")
    @Expose
    private String hora_comienzo;
    @SerializedName("hora_fin")
    @Expose
    private String hora_fin;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
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

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
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
}
