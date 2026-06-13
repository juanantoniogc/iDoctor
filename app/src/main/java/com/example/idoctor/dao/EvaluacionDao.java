package com.example.idoctor.dao;

import com.example.idoctor.models.Evaluacion;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}
