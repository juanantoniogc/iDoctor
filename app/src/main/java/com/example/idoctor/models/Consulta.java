package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Consulta {

    private String id;
    private String titulo;
    private String correo;
    private String telefono;
    private String url;
    private String idProfesional;

    public Consulta() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("title")
    public String getTitulo() {
        return titulo;
    }

    @PropertyName("title")
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @PropertyName("email")
    public String getCorreo() {
        return correo;
    }

    @PropertyName("email")
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @PropertyName("phone")
    public String getTelefono() {
        return telefono;
    }

    @PropertyName("phone")
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @PropertyName("professionalId")
    public String getIdProfesional() {
        return idProfesional;
    }

    @PropertyName("professionalId")
    public void setIdProfesional(String idProfesional) {
        this.idProfesional = idProfesional;
    }
}
