package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Valoracion;
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

public class ValoracionDao {

    private final DatabaseReference referenciaBase;
    private final DatabaseReference referenciaValoraciones;

    public ValoracionDao() {
        referenciaBase = FirebaseDatabase.getInstance().getReference();
        referenciaValoraciones = referenciaBase.child("ratings");
    }

    public Task<Void> guardarValoracion(Valoracion valoracion) {
        TaskCompletionSource<Void> tarea = new TaskCompletionSource<>();

        yaExisteValoracion(valoracion.getIdCita(), valoracion.getIdPaciente(), new ExisteValoracionListener() {
            @Override
            public void resultado(boolean existe) {
                if (existe) {
                    tarea.setException(new Exception("Ya has valorado esta cita"));
                    return;
                }

                guardarNuevaValoracion(valoracion, tarea);
            }

            @Override
            public void error(String mensajeError) {
                tarea.setException(new Exception(mensajeError));
            }
        });

        return tarea.getTask();
    }

    private void guardarNuevaValoracion(Valoracion valoracion, TaskCompletionSource<Void> tarea) {
        if (valoracion.getId() == null || valoracion.getId().isEmpty()) {
            String key = referenciaValoraciones.push().getKey();
            valoracion.setId(key);
        }

        referenciaValoraciones.child(valoracion.getId()).setValue(valoracion).addOnCompleteListener(resultado -> {
            if (resultado.isSuccessful()) {
                recalcularMediaProfesional(valoracion.getIdProfesional(), tarea);
            } else if (resultado.getException() != null) {
                tarea.setException(resultado.getException());
            } else {
                tarea.setException(new Exception("No se pudo guardar la valoracion"));
            }
        });
    }

    public void yaExisteValoracion(String idCita, String idPaciente, ExisteValoracionListener listener) {
        referenciaValoraciones
                .orderByChild("appointmentId")
                .equalTo(idCita)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean existe = false;

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Valoracion valoracion = hijo.getValue(Valoracion.class);

                            if (valoracion != null && idPaciente.equals(valoracion.getIdPaciente())) {
                                existe = true;
                                break;
                            }
                        }

                        listener.resultado(existe);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    public void obtenerValoracionesPorProfesional(String idProfesional, ValoracionesListener listener) {
        referenciaValoraciones
                .orderByChild("professionalId")
                .equalTo(idProfesional)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Valoracion> valoraciones = new ArrayList<>();

                        for (DataSnapshot hijo : snapshot.getChildren()) {
                            Valoracion valoracion = hijo.getValue(Valoracion.class);

                            if (valoracion != null) {
                                if (valoracion.getId() == null || valoracion.getId().isEmpty()) {
                                    valoracion.setId(hijo.getKey());
                                }
                                valoraciones.add(valoracion);
                            }
                        }

                        listener.valoracionesEncontradas(valoraciones);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.error(error.getMessage());
                    }
                });
    }

    private void recalcularMediaProfesional(String idProfesional, TaskCompletionSource<Void> tarea) {
        obtenerValoracionesPorProfesional(idProfesional, new ValoracionesListener() {
            @Override
            public void valoracionesEncontradas(List<Valoracion> valoraciones) {
                double suma = 0;

                for (Valoracion valoracion : valoraciones) {
                    suma += valoracion.getEstrellas();
                }

                int cantidad = valoraciones.size();
                double media = cantidad == 0 ? 0 : suma / cantidad;

                Map<String, Object> datos = new HashMap<>();
                datos.put("averageStars", media);
                datos.put("ratingsCount", cantidad);

                referenciaBase.child("professionals").child(idProfesional).updateChildren(datos)
                        .addOnCompleteListener(resultado -> {
                            if (resultado.isSuccessful()) {
                                tarea.setResult(null);
                            } else if (resultado.getException() != null) {
                                tarea.setException(resultado.getException());
                            } else {
                                tarea.setException(new Exception("No se pudo actualizar la media"));
                            }
                        });
            }

            @Override
            public void error(String mensajeError) {
                tarea.setException(new Exception(mensajeError));
            }
        });
    }

    public interface ValoracionesListener {
        void valoracionesEncontradas(List<Valoracion> valoraciones);

        void error(String mensajeError);
    }

    public interface ExisteValoracionListener {
        void resultado(boolean existe);

        void error(String mensajeError);
    }
}
