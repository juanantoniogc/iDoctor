package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.databinding.ActivityDetalleCitaProfesionalBinding;

public class DetalleCitaProfesionalActivity extends AppCompatActivity {

    private ActivityDetalleCitaProfesionalBinding vista;
    private String idCita;
    private String idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleCitaProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        recogerDatos();
        mostrarDatos();

        vista.btnCrearEvaluacion.setOnClickListener(view -> abrirCrearEvaluacion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void recogerDatos() {
        idCita = getIntent().getStringExtra("idCita");
        idPaciente = getIntent().getStringExtra("idPaciente");
    }

    private void mostrarDatos() {
        vista.txtFecha.setText("Fecha: " + obtenerTexto(getIntent().getStringExtra("fecha")));
        vista.txtHora.setText("Hora: " + obtenerTexto(getIntent().getStringExtra("hora")));
        vista.txtIdConsulta.setText("Consulta: " + obtenerTexto(getIntent().getStringExtra("idConsulta")));
        vista.txtIdPaciente.setText("Paciente: " + obtenerTexto(idPaciente));
    }

    private void abrirCrearEvaluacion() {
        if (estaVacio(idPaciente)) {
            Toast.makeText(this, "Esta cita todavia no tiene paciente", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CrearEvaluacionActivity.class);
        intent.putExtra("idCita", idCita);
        intent.putExtra("idPaciente", idPaciente);
        startActivity(intent);
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private String obtenerTexto(String texto) {
        if (estaVacio(texto)) {
            return "Sin datos";
        }

        return texto;
    }
}
