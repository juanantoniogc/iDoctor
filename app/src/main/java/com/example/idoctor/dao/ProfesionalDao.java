package com.example.idoctor.dao;

import com.example.idoctor.models.Profesional;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfesionalDao {

    public Task<Void> guardarProfesional(Profesional profesional) {
        Map<String, Object> datosProfesional = new HashMap<>();
        datosProfesional.put("id", profesional.getId());
        datosProfesional.put("name", profesional.getNombre());
        datosProfesional.put("surname", profesional.getApellidos());
        datosProfesional.put("email", profesional.getCorreo());
        datosProfesional.put("phone", profesional.getTelefono());
        datosProfesional.put("photo", profesional.getFoto());
        datosProfesional.put("collegiateNumber", profesional.getNumeroColegiado());
        datosProfesional.put("specialty", profesional.getEspecialidad());
        datosProfesional.put("description", profesional.getDescripcion());

        return FirebaseDatabase.getInstance()
                .getReference("professionals")
                .child(profesional.getId())
                .setValue(datosProfesional);
    }
}
