package com.example.idoctor.models;

import com.google.firebase.database.PropertyName;

public class Horario {

    private String id;
    private String idConsulta;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public Horario() {
        // Constructor vacio necesario para Firebase Realtime Database.
    }

    public Horario(String id, String idConsulta, String diaSemana, String horaInicio, String horaFin) {
        this.id = id;
        this.idConsulta = idConsulta;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
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

    @PropertyName("dayOfWeek")
    public String getDiaSemana() {
        return diaSemana;
    }

    @PropertyName("dayOfWeek")
    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    @PropertyName("startTime")
    public String getHoraInicio() {
        return horaInicio;
    }

    @PropertyName("startTime")
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    @PropertyName("endTime")
    public String getHoraFin() {
        return horaFin;
    }

    @PropertyName("endTime")
    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
