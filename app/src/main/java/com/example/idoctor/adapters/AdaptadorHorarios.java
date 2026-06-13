package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemHorarioBinding;
import com.example.idoctor.models.Horario;

import java.util.List;

public class AdaptadorHorarios extends RecyclerView.Adapter<AdaptadorHorarios.HorarioViewHolder> {

    private final List<Horario> horarios;
    private final OnHorarioClickListener listener;

    public AdaptadorHorarios(List<Horario> horarios, OnHorarioClickListener listener) {
        this.horarios = horarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHorarioBinding vista = ItemHorarioBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HorarioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        holder.mostrarHorario(horarios.get(position));
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    class HorarioViewHolder extends RecyclerView.ViewHolder {

        private final ItemHorarioBinding vista;

        HorarioViewHolder(ItemHorarioBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarHorario(Horario horario) {
            vista.txtDiaSemana.setText(obtenerTexto(horario.getDiaSemana()));
            vista.txtHoras.setText(obtenerTexto(horario.getHoraInicio()) + " - " + obtenerTexto(horario.getHoraFin()));

            vista.btnEditar.setOnClickListener(view -> listener.editarHorario(horario));
            vista.btnEliminar.setOnClickListener(view -> listener.eliminarHorario(horario));
        }

        private String obtenerTexto(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return "Sin datos";
            }

            return texto;
        }
    }

    public interface OnHorarioClickListener {
        void editarHorario(Horario horario);

        void eliminarHorario(Horario horario);
    }
}
