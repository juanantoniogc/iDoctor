package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Profesional {

    private String id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;
    private String foto;
    private String numeroColegiado;
    private String especialidad;
    private String descripcion;
    private double mediaEstrellas;
    private int numeroValoraciones;

    public Profesional() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Profesional(String id, String nombre, String apellidos, String correo, String telefono,
                       String foto, String numeroColegiado, String especialidad, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.foto = foto;
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
        this.descripcion = descripcion;
        this.mediaEstrellas = 0;
        this.numeroValoraciones = 0;
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

    @PropertyName("collegiateNumber")
    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    @PropertyName("collegiateNumber")
    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    @PropertyName("specialty")
    public String getEspecialidad() {
        return especialidad;
    }

    @PropertyName("specialty")
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @PropertyName("description")
    public String getDescripcion() {
        return descripcion;
    }

    @PropertyName("description")
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @PropertyName("averageStars")
    public double getMediaEstrellas() {
        return mediaEstrellas;
    }

    @PropertyName("averageStars")
    public void setMediaEstrellas(double mediaEstrellas) {
        this.mediaEstrellas = mediaEstrellas;
    }

    @PropertyName("ratingsCount")
    public int getNumeroValoraciones() {
        return numeroValoraciones;
    }

    @PropertyName("ratingsCount")
    public void setNumeroValoraciones(int numeroValoraciones) {
        this.numeroValoraciones = numeroValoraciones;
    }
}
