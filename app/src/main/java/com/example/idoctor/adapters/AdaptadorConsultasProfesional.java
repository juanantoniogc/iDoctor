package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemConsultaProfesionalBinding;
import com.example.idoctor.models.Consulta;

import java.util.List;

public class AdaptadorConsultasProfesional extends RecyclerView.Adapter<AdaptadorConsultasProfesional.ConsultaViewHolder> {

    private final List<Consulta> consultas;
    private final OnConsultaClickListener listener;

    public AdaptadorConsultasProfesional(List<Consulta> consultas, OnConsultaClickListener listener) {
        this.consultas = consultas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsultaProfesionalBinding vista = ItemConsultaProfesionalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ConsultaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaViewHolder holder, int position) {
        holder.mostrarConsulta(consultas.get(position));
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    class ConsultaViewHolder extends RecyclerView.ViewHolder {

        private final ItemConsultaProfesionalBinding vista;

        ConsultaViewHolder(ItemConsultaProfesionalBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarConsulta(Consulta consulta) {
            vista.txtTituloConsulta.setText(obtenerTexto(consulta.getTitulo()));
            vista.txtTelefono.setText("Telefono: " + obtenerTexto(consulta.getTelefono()));
            vista.txtCorreo.setText("Correo: " + obtenerTexto(consulta.getCorreo()));
            vista.txtTelefonoAuxiliar.setText("Telefono auxiliar: " + obtenerTexto(consulta.getTelefonoAuxiliar()));
            vista.txtObservaciones.setText("Observaciones: " + obtenerTexto(consulta.getObservaciones()));

            vista.btnEditar.setOnClickListener(view -> listener.editarConsulta(consulta));
            vista.btnHorarios.setOnClickListener(view -> listener.verHorarios(consulta));
            vista.btnEliminar.setOnClickListener(view -> listener.eliminarConsulta(consulta));
        }

        private String obtenerTexto(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return "Sin datos";
            }

            return texto;
        }
    }

    public interface OnConsultaClickListener {
        void editarConsulta(Consulta consulta);

        void verHorarios(Consulta consulta);

        void eliminarConsulta(Consulta consulta);
    }
}
