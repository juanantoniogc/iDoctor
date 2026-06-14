package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Valoracion {

    private String id;
    private String idProfesional;
    private String idPaciente;
    private String idCita;
    private int estrellas;
    private String comentario;
    private String momento;

    public Valoracion() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Valoracion(String id, String idProfesional, String idPaciente, String idCita,
                      int estrellas, String comentario, String momento) {
        this.id = id;
        this.idProfesional = idProfesional;
        this.idPaciente = idPaciente;
        this.idCita = idCita;
        this.estrellas = estrellas;
        this.comentario = comentario;
        this.momento = momento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("professionalId")
    public String getIdProfesional() {
        return idProfesional;
    }

    @PropertyName("professionalId")
    public void setIdProfesional(String idProfesional) {
        this.idProfesional = idProfesional;
    }

    @PropertyName("patientId")
    public String getIdPaciente() {
        return idPaciente;
    }

    @PropertyName("patientId")
    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    @PropertyName("appointmentId")
    public String getIdCita() {
        return idCita;
    }

    @PropertyName("appointmentId")
    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

    @PropertyName("stars")
    public int getEstrellas() {
        return estrellas;
    }

    @PropertyName("stars")
    public void setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }

    @PropertyName("comment")
    public String getComentario() {
        return comentario;
    }

    @PropertyName("comment")
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @PropertyName("moment")
    public String getMomento() {
        return momento;
    }

    @PropertyName("moment")
    public void setMomento(String momento) {
        this.momento = momento;
    }
}
