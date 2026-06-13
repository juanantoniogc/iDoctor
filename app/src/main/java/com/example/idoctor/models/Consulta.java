package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Consulta {

    private String id;
    private String titulo;
    private String correo;
    private String telefono;
    private String url;
    private String idProfesional;
    private Direccion direccion;

    public Consulta() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Consulta(String id, String idProfesional, String titulo, String correo,
                    String telefono, String url, Direccion direccion) {
        this.id = id;
        this.idProfesional = idProfesional;
        this.titulo = titulo;
        this.correo = correo;
        this.telefono = telefono;
        this.url = url;
        this.direccion = direccion;
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

    @PropertyName("address")
    public Direccion getDireccion() {
        return direccion;
    }

    @PropertyName("address")
    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }
}
