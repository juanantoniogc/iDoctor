package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.databinding.ActivityDetalleEvaluacionBinding;
import com.example.idoctor.models.Cita;

public class DetalleEvaluacionActivity extends AppCompatActivity {

    private ActivityDetalleEvaluacionBinding vista;
    private CitaDao citaDao;
    private String idCita;
    private String idPaciente;
    private Cita citaEvaluada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleEvaluacionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        citaDao = new CitaDao();
        idCita = getIntent().getStringExtra("idCita");
        idPaciente = getIntent().getStringExtra("idPaciente");

        mostrarDatos();
        cargarCitaEvaluada();

        vista.btnValorarProfesional.setOnClickListener(view -> abrirValoracion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarDatos() {
        vista.txtDescripcion.setText(obtenerTexto(getIntent().getStringExtra("descripcion")));
        vista.txtExploracion.setText(obtenerTexto(getIntent().getStringExtra("exploracion")));
        vista.txtTratamiento.setText(obtenerTexto(getIntent().getStringExtra("tratamiento")));
    }

    private void cargarCitaEvaluada() {
        vista.btnValorarProfesional.setEnabled(false);
        vista.btnValorarProfesional.setText("Cargando cita");

        citaDao.obtenerCitaPorId(idCita, new CitaDao.CitaListener() {
            @Override
            public void citaEncontrada(Cita cita) {
                if (cita == null) {
                    vista.btnValorarProfesional.setText("No se encontro la cita");
                    return;
                }

                if (!obtenerTexto(idPaciente).equals(cita.getIdPaciente())) {
                    vista.btnValorarProfesional.setText("No puedes valorar esta cita");
                    return;
                }

                citaEvaluada = cita;
                vista.btnValorarProfesional.setText("Valorar profesional");
                vista.btnValorarProfesional.setEnabled(true);
            }

            @Override
            public void error(String mensajeError) {
                vista.btnValorarProfesional.setText("No se pudo cargar la cita");
                Toast.makeText(DetalleEvaluacionActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirValoracion() {
        if (citaEvaluada == null) {
            Toast.makeText(this, "No se encontro la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CrearValoracionActivity.class);
        intent.putExtra("idCita", citaEvaluada.getId());
        intent.putExtra("idProfesional", citaEvaluada.getIdProfesional());
        intent.putExtra("idPaciente", citaEvaluada.getIdPaciente());
        startActivity(intent);
    }

    private String obtenerTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Sin datos";
        }

        return texto;
    }
}
