package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemValoracionBinding;
import com.example.idoctor.models.Paciente;
import com.example.idoctor.models.Usuario;
import com.example.idoctor.models.Valoracion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdaptadorValoraciones extends RecyclerView.Adapter<AdaptadorValoraciones.ValoracionViewHolder> {

    private final List<Valoracion> valoraciones;
    private final OnValoracionClickListener listener;
    private final Map<String, String> nombresPacientes;

    public AdaptadorValoraciones(List<Valoracion> valoraciones, OnValoracionClickListener listener) {
        this.valoraciones = valoraciones;
        this.listener = listener;
        this.nombresPacientes = new HashMap<>();
    }

    @NonNull
    @Override
    public ValoracionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemValoracionBinding vista = ItemValoracionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ValoracionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ValoracionViewHolder holder, int position) {
        holder.mostrarValoracion(valoraciones.get(position), listener, nombresPacientes);
    }

    @Override
    public int getItemCount() {
        return valoraciones.size();
    }

    static class ValoracionViewHolder extends RecyclerView.ViewHolder {

        private final ItemValoracionBinding vista;

        ValoracionViewHolder(ItemValoracionBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarValoracion(Valoracion valoracion, OnValoracionClickListener listener,
                               Map<String, String> nombresPacientes) {
            vista.ratingEstrellas.setRating(valoracion.getEstrellas());
            vista.txtComentario.setText("Comentario: " + texto(valoracion.getComentario()));
            vista.txtPaciente.setTag(valoracion.getIdPaciente());
            vista.txtPaciente.setText("Paciente: " + texto(nombresPacientes.get(valoracion.getIdPaciente())));
            vista.txtMomento.setText(formatearMomento(valoracion.getMomento()));
            cargarNombrePaciente(valoracion.getIdPaciente(), nombresPacientes);
            vista.getRoot().setOnClickListener(view -> listener.valoracionPulsada(valoracion));
        }

        private void cargarNombrePaciente(String idPaciente, Map<String, String> nombresPacientes) {
            if (idPaciente == null || idPaciente.trim().isEmpty() || nombresPacientes.containsKey(idPaciente)) {
                return;
            }

            FirebaseDatabase.getInstance().getReference("patients").child(idPaciente)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Paciente paciente = snapshot.getValue(Paciente.class);
                                nombresPacientes.put(idPaciente, nombreCompleto(paciente));
                                mostrarNombreSiSigueEnFila(idPaciente, nombresPacientes);
                            } else {
                                cargarNombreUsuario(idPaciente, nombresPacientes);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            cargarNombreUsuario(idPaciente, nombresPacientes);
                        }
                    });
        }

        private void cargarNombreUsuario(String idPaciente, Map<String, String> nombresPacientes) {
            FirebaseDatabase.getInstance().getReference("users").child(idPaciente)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            nombresPacientes.put(idPaciente, nombreCompleto(usuario));
                            mostrarNombreSiSigueEnFila(idPaciente, nombresPacientes);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            nombresPacientes.put(idPaciente, "Sin datos");
                            mostrarNombreSiSigueEnFila(idPaciente, nombresPacientes);
                        }
                    });
        }

        private void mostrarNombreSiSigueEnFila(String idPaciente, Map<String, String> nombresPacientes) {
            if (idPaciente.equals(vista.txtPaciente.getTag())) {
                vista.txtPaciente.setText("Paciente: " + texto(nombresPacientes.get(idPaciente)));
            }
        }

        private String nombreCompleto(Paciente paciente) {
            if (paciente == null) {
                return "Sin datos";
            }

            String nombre = valorSeguro(paciente.getNombre());
            String apellidos = valorSeguro(paciente.getApellidos());
            String nombreCompleto = (nombre + " " + apellidos).trim();
            return nombreCompleto.isEmpty() ? "Sin datos" : nombreCompleto;
        }

        private String nombreCompleto(Usuario usuario) {
            if (usuario == null) {
                return "Sin datos";
            }

            String nombre = valorSeguro(usuario.getNombre());
            String apellidos = valorSeguro(usuario.getApellidos());
            String nombreCompleto = (nombre + " " + apellidos).trim();
            return nombreCompleto.isEmpty() ? "Sin datos" : nombreCompleto;
        }

        private String formatearMomento(String momento) {
            if (momento == null || momento.trim().isEmpty()) {
                return "Valorado en fecha no disponible";
            }

            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat formatoSalida = new SimpleDateFormat("'Valorado el' d 'de' MMMM 'de' yyyy 'a las' HH:mm", new Locale("es", "ES"));

            try {
                return formatoSalida.format(formatoEntrada.parse(momento));
            } catch (ParseException e) {
                return "Valorado el " + momento;
            }
        }

        private String texto(String valor) {
            if (valor == null || valor.trim().isEmpty()) {
                return "Sin datos";
            }
            return valor;
        }

        private String valorSeguro(String valor) {
            return valor == null ? "" : valor.trim();
        }
    }

    public interface OnValoracionClickListener {
        void valoracionPulsada(Valoracion valoracion);
    }
}
