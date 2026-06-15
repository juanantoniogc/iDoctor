package com.example.idoctor.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.idoctor.databinding.ItemEvaluacionBinding;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.models.Cita;
import com.example.idoctor.models.Evaluacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdaptadorEvaluaciones extends RecyclerView.Adapter<AdaptadorEvaluaciones.EvaluacionViewHolder> {

    private final List<Evaluacion> evaluaciones;
    private final OnEvaluacionClickListener listener;
    private final CitaDao citaDao;
    private final Map<String, String> textosCitas;

    public AdaptadorEvaluaciones(List<Evaluacion> evaluaciones, OnEvaluacionClickListener listener) {
        this.evaluaciones = evaluaciones;
        this.listener = listener;
        citaDao = new CitaDao();
        textosCitas = new HashMap<>();
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
        holder.mostrarEvaluacion(evaluaciones.get(position), listener, citaDao, textosCitas);
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

        void mostrarEvaluacion(Evaluacion evaluacion, OnEvaluacionClickListener listener, CitaDao citaDao,
                               Map<String, String> textosCitas) {
            String idCita = evaluacion.getIdCita();
            vista.txtMomento.setTag(idCita);
            if (textosCitas.containsKey(idCita)) {
                vista.txtMomento.setText("Cita del " + obtenerTexto(textosCitas.get(idCita)));
            } else {
                vista.txtMomento.setText("Cargando cita");
            }
            vista.txtDescripcion.setText("Descripcion: " + obtenerTexto(evaluacion.getDescripcion()));

            if (!estaVacio(idCita) && !textosCitas.containsKey(idCita)) {
                citaDao.obtenerCitaPorId(idCita, new CitaDao.CitaListener() {
                    @Override
                    public void citaEncontrada(Cita cita) {
                        String textoCita = formatearCita(cita);
                        textosCitas.put(idCita, textoCita);

                        if (idCita.equals(vista.txtMomento.getTag())) {
                            vista.txtMomento.setText("Cita del " + textoCita);
                        }
                    }

                    @Override
                    public void error(String mensajeError) {
                        textosCitas.put(idCita, "Sin datos");

                        if (idCita.equals(vista.txtMomento.getTag())) {
                            vista.txtMomento.setText("Cita del Sin datos");
                        }
                    }
                });
            }

            vista.getRoot().setOnClickListener(view -> listener.evaluacionPulsada(evaluacion));
        }

        private String formatearCita(Cita cita) {
            if (cita == null) {
                return "Sin datos";
            }

            String fecha = formatearFecha(cita.getFecha());
            String hora = obtenerTexto(cita.getHora());

            if ("Sin datos".equals(fecha) && "Sin datos".equals(hora)) {
                return "Sin datos";
            }

            return fecha + " a las " + hora;
        }

        private String formatearFecha(String fecha) {
            if (estaVacio(fecha)) {
                return "Sin datos";
            }

            try {
                SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return formatoSalida.format(formatoEntrada.parse(fecha));
            } catch (ParseException | NullPointerException error) {
                return fecha;
            }
        }

        private String obtenerTexto(String texto) {
            if (estaVacio(texto)) {
                return "Sin datos";
            }

            return texto;
        }

        private boolean estaVacio(String texto) {
            return texto == null || texto.trim().isEmpty();
        }
    }

    public interface OnEvaluacionClickListener {
        void evaluacionPulsada(Evaluacion evaluacion);
    }
}
