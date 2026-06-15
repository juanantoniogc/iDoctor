package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.R;
import com.example.idoctor.databinding.ItemProfesionalBinding;
import com.example.idoctor.models.Profesional;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class AdaptadorProfesionales extends RecyclerView.Adapter<AdaptadorProfesionales.ProfesionalViewHolder> {

    private final List<Profesional> profesionales;
    private final OnProfesionalClickListener listener;

    public AdaptadorProfesionales(List<Profesional> profesionales, OnProfesionalClickListener listener) {
        this.profesionales = profesionales;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfesionalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProfesionalBinding vista = ItemProfesionalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ProfesionalViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfesionalViewHolder holder, int position) {
        Profesional profesional = profesionales.get(position);
        holder.mostrarProfesional(profesional);
    }

    @Override
    public int getItemCount() {
        return profesionales.size();
    }

    class ProfesionalViewHolder extends RecyclerView.ViewHolder {

        private final ItemProfesionalBinding vista;

        ProfesionalViewHolder(ItemProfesionalBinding vista) {
            super(vista.getRoot());
            this.vista = vista;
        }

        void mostrarProfesional(Profesional profesional) {
            String nombreCompleto = obtenerTexto(profesional.getNombre()) + " " + obtenerTexto(profesional.getApellidos());

            vista.txtNombreProfesional.setText(nombreCompleto.trim());
            vista.txtCorreo.setText("Correo: " + obtenerTexto(profesional.getCorreo()));
            vista.txtTelefono.setText("Telefono: " + obtenerTexto(profesional.getTelefono()));
            vista.txtValoraciones.setText(String.format(Locale.getDefault(),
                    "%.1f estrellas (%d valoraciones)",
                    profesional.getMediaEstrellas(),
                    profesional.getNumeroValoraciones()));
            vista.txtEspecialidad.setText(obtenerTexto(profesional.getEspecialidad()));
            cargarFoto(profesional.getFoto());

            vista.getRoot().setOnClickListener(view -> listener.onProfesionalClick(profesional));
        }

        private void cargarFoto(String foto) {
            if (foto == null || foto.trim().isEmpty()) {
                vista.imgFotoProfesional.setImageResource(R.drawable.bg_profile_initial);
                vista.txtSinImagen.setVisibility(android.view.View.VISIBLE);
                return;
            }

            vista.txtSinImagen.setVisibility(android.view.View.GONE);
            Picasso.get()
                    .load(foto)
                    .placeholder(R.drawable.bg_profile_initial)
                    .error(R.drawable.bg_profile_initial)
                    .into(vista.imgFotoProfesional, new Callback() {
                        @Override
                        public void onSuccess() {
                            vista.txtSinImagen.setVisibility(android.view.View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            vista.txtSinImagen.setVisibility(android.view.View.VISIBLE);
                        }
                    });
        }

        private String obtenerTexto(String texto) {
            if (texto == null || texto.trim().isEmpty()) {
                return "Sin datos";
            }

            return texto;
        }
    }

    public interface OnProfesionalClickListener {
        void onProfesionalClick(Profesional profesional);
    }
}
