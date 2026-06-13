package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemCitaBinding;
import com.example.idoctor.models.Cita;

import java.util.List;

public class AdaptadorCitas extends RecyclerView.Adapter<AdaptadorCitas.CitaViewHolder> {

    private final List<Cita> citas;
    private final OnCitaClickListener listener;

    public AdaptadorCitas(List<Cita> citas, OnCitaClickListener listener) {
        this.citas = citas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCitaBinding vista = ItemCitaBinding.inflate(
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

        private final ItemCitaBinding vista;

        CitaViewHolder(ItemCitaBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarCita(Cita cita, OnCitaClickListener listener) {
            vista.txtFecha.setText("Fecha: " + obtenerTexto(cita.getFecha()));
            vista.txtHora.setText("Hora: " + obtenerTexto(cita.getHora()));
            vista.txtEstado.setText(cita.isActiva() ? "Activa" : "No activa");

            vista.getRoot().setOnClickListener(view -> listener.citaPulsada(cita));
        }

        private String obtenerTexto(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return "Sin datos";
            }

            return texto;
        }
    }

    public interface OnCitaClickListener {
        void citaPulsada(Cita cita);
    }
}
