package com.example.idoctor.dao;

import com.example.idoctor.models.Profesional;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class ProfesionalDao {

    public Task<Void> guardarProfesional(Profesional profesional) {
        return FirebaseDatabase.getInstance()
                .getReference("professionals")
                .child(profesional.getId())
                .setValue(profesional);
    }
}
