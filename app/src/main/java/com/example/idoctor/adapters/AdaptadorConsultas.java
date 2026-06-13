package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemConsultaBinding;
import com.example.idoctor.models.Consulta;

import java.util.List;

public class AdaptadorConsultas extends RecyclerView.Adapter<AdaptadorConsultas.ConsultaViewHolder> {

    private final List<Consulta> consultas;
    private final OnConsultaClickListener listener;

    public AdaptadorConsultas(List<Consulta> consultas, OnConsultaClickListener listener) {
        this.consultas = consultas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConsultaBinding vista = ItemConsultaBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ConsultaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaViewHolder holder, int position) {
        holder.mostrarConsulta(consultas.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    static class ConsultaViewHolder extends RecyclerView.ViewHolder {

        private final ItemConsultaBinding vista;

        ConsultaViewHolder(ItemConsultaBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarConsulta(Consulta consulta, OnConsultaClickListener listener) {
            vista.txtTituloConsulta.setText(obtenerTexto(consulta.getTitulo()));
            vista.txtTelefono.setText("Telefono: " + obtenerTexto(consulta.getTelefono()));
            vista.txtCorreo.setText("Correo: " + obtenerTexto(consulta.getCorreo()));
            vista.txtUrl.setText("Web: " + obtenerTexto(consulta.getUrl()));

            vista.getRoot().setOnClickListener(view -> listener.consultaPulsada(consulta));
        }

        private String obtenerTexto(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return "Sin datos";
            }

            return texto;
        }
    }

    public interface OnConsultaClickListener {
        void consultaPulsada(Consulta consulta);
    }
}
