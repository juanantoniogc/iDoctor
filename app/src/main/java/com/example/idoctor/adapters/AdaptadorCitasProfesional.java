package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemCitaProfesionalBinding;
import com.example.idoctor.models.Cita;

import java.util.List;

public class AdaptadorCitasProfesional extends RecyclerView.Adapter<AdaptadorCitasProfesional.CitaViewHolder> {

    private final List<Cita> citas;
    private final OnCitaProfesionalClickListener listener;

    public AdaptadorCitasProfesional(List<Cita> citas, OnCitaProfesionalClickListener listener) {
        this.citas = citas;
        this.listener = listener;
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
        holder.mostrarCita(citas.get(position), listener);
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

        void mostrarCita(Cita cita, OnCitaProfesionalClickListener listener) {
            vista.txtFecha.setText("Fecha: " + texto(cita.getFecha()));
            vista.txtHora.setText("Hora: " + texto(cita.getHora()));
            vista.txtPaciente.setText("Paciente: " + (estaVacio(cita.getIdPaciente()) ? "Sin reservar" : cita.getIdPaciente()));
            vista.txtEstado.setText(cita.isActiva() ? "Activa" : "No activa");

            vista.btnDetalle.setOnClickListener(view -> listener.verDetalle(cita));
            vista.btnEditar.setOnClickListener(view -> listener.editarCita(cita));
            vista.btnEliminar.setOnClickListener(view -> listener.eliminarCita(cita));
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
