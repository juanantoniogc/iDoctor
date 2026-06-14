package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Cita;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class CitaDao {

    private final DatabaseReference referenciaBase;
    private final DatabaseReference referenciaCitas;

    public CitaDao() {
        referenciaBase = FirebaseDatabase.getInstance().getReference();
        referenciaCitas = referenciaBase.child("appointments");
    }

    public void obtenerClavesCitasPorConsulta(String idConsulta, CitasExistentesListener listener) {
        referenciaCitas
                .orderByChild("consultationId")
                .equalTo(idConsulta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Set<String> claves = new HashSet<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Cita cita = hijo.getValue(Cita.class);

                            if (cita != null && cita.getFecha() != null && cita.getHora() != null) {
                                claves.add(cita.getFecha() + "|" + cita.getHora());
                            }
                        }

                        listener.citasEncontradas(claves);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public Task<Void> guardarCita(Cita cita) {
        if (cita.getId() == null || cita.getId().isEmpty()) {
            String key = referenciaCitas.push().getKey();
            cita.setId(key);
        }

        return referenciaCitas
                .child(cita.getId())
                .setValue(cita);
    }

    public void obtenerCitasDisponiblesPorConsulta(String idConsulta, CitasListener listener) {
        referenciaCitas
                .orderByChild("consultationId")
                .equalTo(idConsulta)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Cita> citas = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Cita cita = hijo.getValue(Cita.class);

                            if (cita != null && cita.isActiva() && estaVacio(cita.getIdPaciente())) {
                                if (estaVacio(cita.getId())) {
                                    cita.setId(hijo.getKey());
                                }

                                citas.add(cita);
                            }
                        }

                        listener.citasEncontradas(citas);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public void obtenerCitasPorPaciente(String idPaciente, CitasListener listener) {
        referenciaCitas
                .orderByChild("patientId")
                .equalTo(idPaciente)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Cita> citas = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Cita cita = hijo.getValue(Cita.class);

                            if (cita != null) {
                                if (estaVacio(cita.getId())) {
                                    cita.setId(hijo.getKey());
                                }

                                citas.add(cita);
                            }
                        }

                        listener.citasEncontradas(citas);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public void obtenerCitasPorProfesional(String idProfesional, CitasListener listener) {
        referenciaCitas
                .orderByChild("professionalId")
                .equalTo(idProfesional)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Cita> citas = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Cita cita = hijo.getValue(Cita.class);

                            if (cita != null) {
                                if (estaVacio(cita.getId())) {
                                    cita.setId(hijo.getKey());
                                }

                                citas.add(cita);
                            }
                        }

                        listener.citasEncontradas(citas);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public void obtenerCitaPorId(String idCita, CitaListener listener) {
        referenciaCitas
                .child(idCita)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Cita cita = snapshot.getValue(Cita.class);

                        if (cita != null && estaVacio(cita.getId())) {
                            cita.setId(snapshot.getKey());
                        }

                        listener.citaEncontrada(cita);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public Task<Void> reservarCita(String idCita, String idPaciente) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("patientId", idPaciente);
        datos.put("isActive", true);

        return referenciaCitas.child(idCita).updateChildren(datos);
    }

    public Task<Void> cancelarCita(String idCita) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("patientId", "");
        datos.put("isActive", true);

        return referenciaCitas.child(idCita).updateChildren(datos);
    }

    public Task<Void> eliminarCitaConEvaluaciones(String idCita) {
        TaskCompletionSource<Void> tarea = new TaskCompletionSource<>();
        Map<String, Object> datosParaBorrar = new HashMap<>();
        datosParaBorrar.put("appointments/" + idCita, null);

        referenciaBase.child("evaluations")
                .orderByChild("appointmentId")
                .equalTo(idCita)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            datosParaBorrar.put("evaluations/" + hijo.getKey(), null);
                        }

                        borrarValoracionesDeCita(idCita, datosParaBorrar, tarea);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tarea.setException(error.toException());
                    }
                });

        return tarea.getTask();
    }

    private void borrarValoracionesDeCita(String idCita, Map<String, Object> datosParaBorrar,
                                          TaskCompletionSource<Void> tarea) {
        referenciaBase.child("ratings")
                .orderByChild("appointmentId")
                .equalTo(idCita)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            datosParaBorrar.put("ratings/" + hijo.getKey(), null);
                        }

                        referenciaBase.updateChildren(datosParaBorrar).addOnCompleteListener(resultado -> {
                            if (resultado.isSuccessful()) {
                                tarea.setResult(null);
                            } else if (resultado.getException() != null) {
                                tarea.setException(resultado.getException());
                            } else {
                                tarea.setException(new Exception("No se pudo eliminar la cita"));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tarea.setException(error.toException());
                    }
                });
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public interface CitasExistentesListener {
        void citasEncontradas(Set<String> claves);

        void error(String mensajeError);
    }

    public interface CitasListener {
        void citasEncontradas(List<Cita> citas);

        void error(String mensajeError);
    }

    public interface CitaListener {
        void citaEncontrada(Cita cita);

        void error(String mensajeError);
    }
}
