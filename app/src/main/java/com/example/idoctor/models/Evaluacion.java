package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Evaluacion {

    private String id;
    private String idCita;
    private String idPaciente;
    private String descripcion;
    private String exploracion;
    private String tratamiento;
    private String momento;

    public Evaluacion() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Evaluacion(String id, String idCita, String idPaciente, String descripcion,
                      String exploracion, String tratamiento, String momento) {
        this.id = id;
        this.idCita = idCita;
        this.idPaciente = idPaciente;
        this.descripcion = descripcion;
        this.exploracion = exploracion;
        this.tratamiento = tratamiento;
        this.momento = momento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("appointmentId")
    public String getIdCita() {
        return idCita;
    }

    @PropertyName("appointmentId")
    public void setIdCita(String idCita) {
        this.idCita = idCita;
    }

    @PropertyName("patientId")
    public String getIdPaciente() {
        return idPaciente;
    }

    @PropertyName("patientId")
    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    @PropertyName("description")
    public String getDescripcion() {
        return descripcion;
    }

    @PropertyName("description")
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @PropertyName("exploration")
    public String getExploracion() {
        return exploracion;
    }

    @PropertyName("exploration")
    public void setExploracion(String exploracion) {
        this.exploracion = exploracion;
    }

    @PropertyName("treatment")
    public String getTratamiento() {
        return tratamiento;
    }

    @PropertyName("treatment")
    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
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
