package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Usuario {

    private String id;
    private String correo;
    private String nombre;
    private String apellidos;
    private String foto;
    private String rol;

    public Usuario() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Usuario(String id, String correo, String nombre, String apellidos, String foto, String rol) {
        this.id = id;
        this.correo = correo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.foto = foto;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("email")
    public String getCorreo() {
        return correo;
    }

    @PropertyName("email")
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @PropertyName("name")
    public String getNombre() {
        return nombre;
    }

    @PropertyName("name")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @PropertyName("surname")
    public String getApellidos() {
        return apellidos;
    }

    @PropertyName("surname")
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    @PropertyName("photo")
    public String getFoto() {
        return foto;
    }

    @PropertyName("photo")
    public void setFoto(String foto) {
        this.foto = foto;
    }

    @PropertyName("role")
    public String getRol() {
        return rol;
    }

    @PropertyName("role")
    public void setRol(String rol) {
        this.rol = rol;
    }
}
