package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Cita {

    private String id;
    private String idConsulta;
    private String idProfesional;
    private String idPaciente;
    private String fecha;
    private String hora;
    private boolean activa;

    public Cita() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Cita(String id, String idConsulta, String idProfesional, String idPaciente,
                String fecha, String hora, boolean activa) {
        this.id = id;
        this.idConsulta = idConsulta;
        this.idProfesional = idProfesional;
        this.idPaciente = idPaciente;
        this.fecha = fecha;
        this.hora = hora;
        this.activa = activa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("consultationId")
    public String getIdConsulta() {
        return idConsulta;
    }

    @PropertyName("consultationId")
    public void setIdConsulta(String idConsulta) {
        this.idConsulta = idConsulta;
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

    @PropertyName("date")
    public String getFecha() {
        return fecha;
    }

    @PropertyName("date")
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @PropertyName("time")
    public String getHora() {
        return hora;
    }

    @PropertyName("time")
    public void setHora(String hora) {
        this.hora = hora;
    }

    @PropertyName("isActive")
    public boolean isActiva() {
        return activa;
    }

    @PropertyName("isActive")
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
