package com.example.idoctor.dao;

import com.example.idoctor.models.Paciente;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class PacienteDao {

    public Task<Void> guardarPaciente(Paciente paciente) {
        return FirebaseDatabase.getInstance()
                .getReference("patients")
                .child(paciente.getId())
                .setValue(paciente);
    }
}
