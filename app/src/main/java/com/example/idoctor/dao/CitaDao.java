package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Cita;
import com.google.android.gms.tasks.Task;
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

    private final DatabaseReference referenciaCitas;

    public CitaDao() {
        referenciaCitas = FirebaseDatabase.getInstance().getReference("appointments");
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
}
