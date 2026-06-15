package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Horario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HorarioDao {

    private final DatabaseReference referenciaHorarios;

    public HorarioDao() {
        referenciaHorarios = FirebaseDatabase.getInstance().getReference("timetables");
    }

    public void obtenerHorariosPorConsulta(String idConsulta, HorariosListener listener) {
        referenciaHorarios
                .orderByChild("consultationId")
                .equalTo(idConsulta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Horario> horarios = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Horario horario = hijo.getValue(Horario.class);

                            if (horario != null) {
                                if (horario.getId() == null || horario.getId().isEmpty()) {
                                    horario.setId(hijo.getKey());
                                }
                                horarios.add(horario);
                            }
                        }

                        listener.horariosEncontrados(horarios);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public Task<Void> guardarHorario(Horario horario) {
        if (horario.getId() == null || horario.getId().isEmpty()) {
            String key = referenciaHorarios.push().getKey();
            horario.setId(key);
        }

        return referenciaHorarios
                .child(horario.getId())
                .setValue(horario);
    }

    public void existeHorarioParaDia(String idConsulta, String diaSemana, String idHorarioActual,
                                     ExisteHorarioListener listener) {
        obtenerHorariosPorConsulta(idConsulta, new HorariosListener() {
            @Override
            public void horariosEncontrados(List<Horario> horarios) {
                for (Horario horario : horarios) {
                    boolean mismoHorario = idHorarioActual != null
                            && !idHorarioActual.isEmpty()
                            && idHorarioActual.equals(horario.getId());

                    if (!mismoHorario && diaSemana.equalsIgnoreCase(horario.getDiaSemana())) {
                        listener.resultado(true);
                        return;
                    }
                }

                listener.resultado(false);
            }

            @Override
            public void error(String mensajeError) {
                listener.error(mensajeError);
            }
        });
    }

    public Task<Void> eliminarHorario(String idHorario) {
        return referenciaHorarios
                .child(idHorario)
                .removeValue();
    }

    public interface HorariosListener {
        void horariosEncontrados(List<Horario> horarios);

        void error(String mensajeError);
    }

    public interface ExisteHorarioListener {
        void resultado(boolean existe);

        void error(String mensajeError);
    }
}
