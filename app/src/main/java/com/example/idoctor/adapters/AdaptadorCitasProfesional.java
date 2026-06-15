package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemCitaProfesionalBinding;
import com.example.idoctor.models.Cita;
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

public class AdaptadorCitasProfesional extends RecyclerView.Adapter<AdaptadorCitasProfesional.CitaViewHolder> {

    private final List<Cita> citas;
    private final OnCitaProfesionalClickListener listener;
    private final Map<String, String> nombresPacientes;

    public AdaptadorCitasProfesional(List<Cita> citas, OnCitaProfesionalClickListener listener) {
        this.citas = citas;
        this.listener = listener;
        this.nombresPacientes = new HashMap<>();
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCitaProfesionalBinding vista = ItemCitaProfesionalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CitaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        holder.mostrarCita(citas.get(position), listener, nombresPacientes);
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {

        private final ItemCitaProfesionalBinding vista;

        CitaViewHolder(ItemCitaProfesionalBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarCita(Cita cita, OnCitaProfesionalClickListener listener, Map<String, String> nombresPacientes) {
            vista.txtFecha.setText("Fecha: " + formatearFecha(cita.getFecha()));
            vista.txtHora.setText("Hora: " + texto(cita.getHora()));
            String idPaciente = cita.getIdPaciente() == null ? "" : cita.getIdPaciente().trim();
            vista.txtPaciente.setTag(idPaciente);
            vista.txtPaciente.setText("Paciente: " + textoPaciente(idPaciente, nombresPacientes));
            cargarNombrePaciente(idPaciente, nombresPacientes);
            vista.txtEstado.setText(cita.isActiva() ? "Activa" : "No activa");

            vista.btnDetalle.setOnClickListener(view -> listener.verDetalle(cita));
            vista.btnEditar.setOnClickListener(view -> listener.editarCita(cita));
            vista.btnEliminar.setOnClickListener(view -> listener.eliminarCita(cita));
        }

        private void cargarNombrePaciente(String idPaciente, Map<String, String> nombresPacientes) {
            if (estaVacio(idPaciente) || nombresPacientes.containsKey(idPaciente)) {
                return;
            }

            FirebaseDatabase.getInstance().getReference("patients").child(idPaciente)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            guardarYMostrarNombre(idPaciente, nombreCompleto(snapshot), nombresPacientes);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            guardarYMostrarNombre(idPaciente, "Sin datos", nombresPacientes);
                        }
                    });
        }

        private void guardarYMostrarNombre(String idPaciente, String nombre, Map<String, String> nombresPacientes) {
            nombresPacientes.put(idPaciente, nombre);
            if (idPaciente.equals(vista.txtPaciente.getTag())) {
                vista.txtPaciente.setText("Paciente: " + nombre);
            }
        }

        private String textoPaciente(String idPaciente, Map<String, String> nombresPacientes) {
            if (estaVacio(idPaciente)) {
                return "Sin reservar";
            }

            if (nombresPacientes.containsKey(idPaciente)) {
                return texto(nombresPacientes.get(idPaciente));
            }

            return "Cargando...";
        }

        private String nombreCompleto(DataSnapshot snapshot) {
            String nombre = snapshot.child("name").getValue(String.class);
            String apellidos = snapshot.child("surname").getValue(String.class);
            String nombreSeguro = nombre == null ? "" : nombre.trim();
            String apellidosSeguro = apellidos == null ? "" : apellidos.trim();
            String nombreCompleto = (nombreSeguro + " " + apellidosSeguro).trim();
            return nombreCompleto.isEmpty() ? "Sin datos" : nombreCompleto;
        }

        private String formatearFecha(String fecha) {
            SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            entrada.setLenient(false);

            try {
                return salida.format(entrada.parse(fecha));
            } catch (ParseException | NullPointerException e) {
                return texto(fecha);
            }
        }

        private String texto(String valor) {
            return estaVacio(valor) ? "Sin datos" : valor;
        }

        private boolean estaVacio(String valor) {
            return valor == null || valor.trim().isEmpty();
        }
    }

    public interface OnCitaProfesionalClickListener {
        void verDetalle(Cita cita);

        void editarCita(Cita cita);

        void eliminarCita(Cita cita);
    }
}
