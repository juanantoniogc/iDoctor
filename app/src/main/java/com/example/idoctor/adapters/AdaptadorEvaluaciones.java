package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemEvaluacionBinding;
import com.example.idoctor.models.Evaluacion;

import java.util.List;

public class AdaptadorEvaluaciones extends RecyclerView.Adapter<AdaptadorEvaluaciones.EvaluacionViewHolder> {

    private final List<Evaluacion> evaluaciones;
    private final OnEvaluacionClickListener listener;

    public AdaptadorEvaluaciones(List<Evaluacion> evaluaciones, OnEvaluacionClickListener listener) {
        this.evaluaciones = evaluaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EvaluacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEvaluacionBinding vista = ItemEvaluacionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EvaluacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluacionViewHolder holder, int position) {
        holder.mostrarEvaluacion(evaluaciones.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return evaluaciones.size();
    }

    static class EvaluacionViewHolder extends RecyclerView.ViewHolder {

        private final ItemEvaluacionBinding vista;

        EvaluacionViewHolder(ItemEvaluacionBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarEvaluacion(Evaluacion evaluacion, OnEvaluacionClickListener listener) {
            vista.txtMomento.setText("Fecha: " + obtenerTexto(evaluacion.getMomento()));
            vista.txtDescripcion.setText("Descripcion: " + obtenerTexto(evaluacion.getDescripcion()));
            vista.txtIdCita.setText("Cita: " + obtenerTexto(evaluacion.getIdCita()));

            vista.getRoot().setOnClickListener(view -> listener.evaluacionPulsada(evaluacion));
        }

        private String obtenerTexto(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return "Sin datos";
            }

            return texto;
        }
    }

    public interface OnEvaluacionClickListener {
        void evaluacionPulsada(Evaluacion evaluacion);
    }
}
