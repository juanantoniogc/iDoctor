package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Evaluacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EvaluacionDao {

    private final DatabaseReference referenciaEvaluaciones;

    public EvaluacionDao() {
        referenciaEvaluaciones = FirebaseDatabase.getInstance().getReference("evaluations");
    }

    public Task<Void> guardarEvaluacion(Evaluacion evaluacion) {
        if (evaluacion.getId() == null || evaluacion.getId().isEmpty()) {
            String key = referenciaEvaluaciones.push().getKey();
            evaluacion.setId(key);
        }

        return referenciaEvaluaciones
                .child(evaluacion.getId())
                .setValue(evaluacion);
    }

    public void obtenerEvaluacionesPorPaciente(String idPaciente, EvaluacionesListener listener) {
        referenciaEvaluaciones
                .orderByChild("patientId")
                .equalTo(idPaciente)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Evaluacion> evaluaciones = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Evaluacion evaluacion = hijo.getValue(Evaluacion.class);

                            if (evaluacion != null) {
                                if (evaluacion.getId() == null || evaluacion.getId().trim().isEmpty()) {
                                    evaluacion.setId(hijo.getKey());
                                }

                                evaluaciones.add(evaluacion);
                            }
                        }

                        listener.evaluacionesEncontradas(evaluaciones);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public interface EvaluacionesListener {
        void evaluacionesEncontradas(List<Evaluacion> evaluaciones);

        void error(String mensajeError);
    }
}
