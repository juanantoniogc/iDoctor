package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Consulta;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultaDao {

    private final DatabaseReference referenciaBase;
    private final DatabaseReference referenciaConsultas;

    public ConsultaDao() {
        referenciaBase = FirebaseDatabase.getInstance().getReference();
        referenciaConsultas = referenciaBase.child("consultations");
    }

    public void obtenerConsultasPorProfesional(String idProfesional, ConsultasListener listener) {
        referenciaConsultas
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

    public Task<Void> guardarConsulta(Consulta consulta) {
        if (consulta.getId() == null || consulta.getId().isEmpty()) {
            String key = referenciaConsultas.push().getKey();
            consulta.setId(key);
        }

        return referenciaConsultas
                .child(consulta.getId())
                .setValue(consulta);
    }

    public void obtenerConsulta(String idConsulta, ConsultaListener listener) {
        referenciaConsultas
                .child(idConsulta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Consulta consulta = snapshot.getValue(Consulta.class);
                            if (consulta != null && (consulta.getId() == null || consulta.getId().isEmpty())) {
                                consulta.setId(snapshot.getKey());
                            }
                            listener.consultaEncontrada(consulta);
                        } else {
                            listener.consultaEncontrada(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public Task<Void> eliminarConsulta(String idConsulta) {
        TaskCompletionSource<Void> tarea = new TaskCompletionSource<>();
        Map<String, Object> datosParaBorrar = new HashMap<>();
        datosParaBorrar.put("consultations/" + idConsulta, null);

        borrarHorariosDeConsulta(idConsulta, datosParaBorrar, tarea);
        return tarea.getTask();
    }

    private void borrarHorariosDeConsulta(String idConsulta, Map<String, Object> datosParaBorrar,
                                          TaskCompletionSource<Void> tarea) {
        referenciaBase.child("timetables")
                .orderByChild("consultationId")
                .equalTo(idConsulta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            datosParaBorrar.put("timetables/" + hijo.getKey(), null);
                        }

                        borrarCitasDeConsulta(idConsulta, datosParaBorrar, tarea);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tarea.setException(error.toException());
                    }
                });
    }

    private void borrarCitasDeConsulta(String idConsulta, Map<String, Object> datosParaBorrar,
                                       TaskCompletionSource<Void> tarea) {
        referenciaBase.child("appointments")
                .orderByChild("consultationId")
                .equalTo(idConsulta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> idsCitas = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            datosParaBorrar.put("appointments/" + hijo.getKey(), null);
                            idsCitas.add(hijo.getKey());
                        }

                        borrarEvaluacionesDeCitas(idsCitas, datosParaBorrar, tarea);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tarea.setException(error.toException());
                    }
                });
    }

    private void borrarEvaluacionesDeCitas(List<String> idsCitas, Map<String, Object> datosParaBorrar,
                                           TaskCompletionSource<Void> tarea) {
        if (idsCitas.isEmpty()) {
            ejecutarBorrado(datosParaBorrar, tarea);
            return;
        }

        final int[] consultasTerminadas = {0};

        for (String idCita : idsCitas) {
            referenciaBase.child("evaluations")
                    .orderByChild("appointmentId")
                    .equalTo(idCita)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot hijo : snapshot.getChildren()) {
                                datosParaBorrar.put("evaluations/" + hijo.getKey(), null);
                            }

                            consultasTerminadas[0]++;
                            if (consultasTerminadas[0] == idsCitas.size()) {
                                ejecutarBorrado(datosParaBorrar, tarea);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            tarea.setException(error.toException());
                        }
                    });
        }
    }

    private void ejecutarBorrado(Map<String, Object> datosParaBorrar, TaskCompletionSource<Void> tarea) {
        referenciaBase.updateChildren(datosParaBorrar).addOnCompleteListener(resultado -> {
            if (resultado.isSuccessful()) {
                tarea.setResult(null);
            } else if (resultado.getException() != null) {
                tarea.setException(resultado.getException());
            } else {
                tarea.setException(new Exception("No se pudo eliminar la consulta"));
            }
        });
    }

    public interface ConsultasListener {
        void consultasEncontradas(List<Consulta> consultas);

        void error(String mensajeError);
    }

    public interface ConsultaListener {
        void consultaEncontrada(Consulta consulta);

        void error(String mensajeError);
    }
}
