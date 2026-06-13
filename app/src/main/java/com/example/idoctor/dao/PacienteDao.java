package com.example.idoctor.dao;

import com.example.idoctor.models.Paciente;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PacienteDao {

    public Task<Void> guardarPaciente(Paciente paciente) {
        Map<String, Object> datosPaciente = new HashMap<>();
        datosPaciente.put("id", paciente.getId());
        datosPaciente.put("name", paciente.getNombre());
        datosPaciente.put("surname", paciente.getApellidos());
        datosPaciente.put("email", paciente.getCorreo());
        datosPaciente.put("phone", paciente.getTelefono());
        datosPaciente.put("photo", paciente.getFoto());
        datosPaciente.put("dni", paciente.getDni());
        datosPaciente.put("healthInsuranceNumber", paciente.getNumeroTarjetaSanitaria());

        return FirebaseDatabase.getInstance()
                .getReference("patients")
                .child(paciente.getId())
                .setValue(datosPaciente);
    }
}
