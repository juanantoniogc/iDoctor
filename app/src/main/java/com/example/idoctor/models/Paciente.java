package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Paciente {

    private String id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;
    private String foto;
    private String dni;
    private String numeroTarjetaSanitaria;

    public Paciente() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Paciente(String id, String nombre, String apellidos, String correo, String telefono,
                    String foto, String dni, String numeroTarjetaSanitaria) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.foto = foto;
        this.dni = dni;
        this.numeroTarjetaSanitaria = numeroTarjetaSanitaria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @PropertyName("photo")
    public String getFoto() {
        return foto;
    }

    @PropertyName("photo")
    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    @PropertyName("healthInsuranceNumber")
    public String getNumeroTarjetaSanitaria() {
        return numeroTarjetaSanitaria;
    }

    @PropertyName("healthInsuranceNumber")
    public void setNumeroTarjetaSanitaria(String numeroTarjetaSanitaria) {
        this.numeroTarjetaSanitaria = numeroTarjetaSanitaria;
    }
}
