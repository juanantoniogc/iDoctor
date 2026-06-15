package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemCitaBinding;
import com.example.idoctor.models.Consulta;
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

public class AdaptadorCitas extends RecyclerView.Adapter<AdaptadorCitas.CitaViewHolder> {

    private final List<Cita> citas;
    private final OnCitaClickListener listener;
    private final Map<String, String> nombresConsultas;

    public AdaptadorCitas(List<Cita> citas, OnCitaClickListener listener) {
        this.citas = citas;
        this.listener = listener;
        this.nombresConsultas = new HashMap<>();
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
        holder.mostrarCita(citas.get(position), listener, nombresConsultas);
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

        void mostrarCita(Cita cita, OnCitaClickListener listener, Map<String, String> nombresConsultas) {
            vista.txtTituloConsulta.setTag(cita.getIdConsulta());
            vista.txtTituloConsulta.setText(obtenerTexto(nombresConsultas.get(cita.getIdConsulta())));
            vista.txtFechaHora.setText(formatearFechaHora(cita.getFecha(), cita.getHora()));
            vista.txtEstado.setText(cita.isActiva() ? "Activa" : "No activa");
            cargarNombreConsulta(cita.getIdConsulta(), nombresConsultas);

            vista.getRoot().setOnClickListener(view -> listener.citaPulsada(cita));
        }

        private void cargarNombreConsulta(String idConsulta, Map<String, String> nombresConsultas) {
            if (idConsulta == null || idConsulta.trim().isEmpty() || nombresConsultas.containsKey(idConsulta)) {
                return;
            }

            FirebaseDatabase.getInstance().getReference("consultations").child(idConsulta)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Consulta consulta = snapshot.getValue(Consulta.class);
                            nombresConsultas.put(idConsulta, consulta == null ? "Sin datos" : obtenerTexto(consulta.getTitulo()));

                            if (idConsulta.equals(vista.txtTituloConsulta.getTag())) {
                                vista.txtTituloConsulta.setText(nombresConsultas.get(idConsulta));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            nombresConsultas.put(idConsulta, "Sin datos");

                            if (idConsulta.equals(vista.txtTituloConsulta.getTag())) {
                                vista.txtTituloConsulta.setText("Sin datos");
                            }
                        }
                    });
        }

        private String formatearFechaHora(String fecha, String hora) {
            SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            entrada.setLenient(false);

            try {
                return salida.format(entrada.parse(fecha)) + " a las " + obtenerTexto(hora);
            } catch (ParseException | NullPointerException e) {
                return obtenerTexto(fecha) + " a las " + obtenerTexto(hora);
            }
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
