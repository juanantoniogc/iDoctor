package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemValoracionBinding;
import com.example.idoctor.models.Valoracion;

import java.util.List;

public class AdaptadorValoraciones extends RecyclerView.Adapter<AdaptadorValoraciones.ValoracionViewHolder> {

    private final List<Valoracion> valoraciones;
    private final OnValoracionClickListener listener;

    public AdaptadorValoraciones(List<Valoracion> valoraciones, OnValoracionClickListener listener) {
        this.valoraciones = valoraciones;
        this.listener = listener;
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
        holder.mostrarValoracion(valoraciones.get(position), listener);
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

        void mostrarValoracion(Valoracion valoracion, OnValoracionClickListener listener) {
            vista.txtEstrellas.setText("Estrellas: " + valoracion.getEstrellas() + "/5");
            vista.txtComentario.setText("Comentario: " + texto(valoracion.getComentario()));
            vista.txtPaciente.setText("Paciente: " + texto(valoracion.getIdPaciente()));
            vista.txtMomento.setText("Momento: " + texto(valoracion.getMomento()));
            vista.getRoot().setOnClickListener(view -> listener.valoracionPulsada(valoracion));
        }

        private String texto(String valor) {
            if (valor == null || valor.trim().isEmpty()) {
                return "Sin datos";
            }
            return valor;
        }
    }

    public interface OnValoracionClickListener {
        void valoracionPulsada(Valoracion valoracion);
    }
}
