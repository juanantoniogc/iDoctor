package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Profesional;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void obtenerProfesionales(ProfesionalesListener listener) {
        FirebaseDatabase.getInstance()
                .getReference("professionals")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Profesional> profesionales = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Profesional profesional = hijo.getValue(Profesional.class);

                            if (profesional != null) {
                                if (profesional.getId() == null || profesional.getId().isEmpty()) {
                                    profesional.setId(hijo.getKey());
                                }
                                profesionales.add(profesional);
                            }
                        }

                        listener.profesionalesEncontrados(profesionales);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public interface ProfesionalesListener {
        void profesionalesEncontrados(List<Profesional> profesionales);

        void error(String mensajeError);
    }
}
