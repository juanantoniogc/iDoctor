package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Consulta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConsultaDao {

    public void obtenerConsultasPorProfesional(String idProfesional, ConsultasListener listener) {
        FirebaseDatabase.getInstance()
                .getReference("consultations")
                .orderByChild("professionalId")
                .equalTo(idProfesional)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Consulta> consultas = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Consulta consulta = hijo.getValue(Consulta.class);

                            if (consulta != null) {
                                if (consulta.getId() == null || consulta.getId().isEmpty()) {
                                    consulta.setId(hijo.getKey());
                                }
                                consultas.add(consulta);
                            }
                        }

                        listener.consultasEncontradas(consultas);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public interface ConsultasListener {
        void consultasEncontradas(List<Consulta> consultas);

        void error(String mensajeError);
    }
}
