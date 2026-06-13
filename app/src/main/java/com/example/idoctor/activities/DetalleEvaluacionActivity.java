package com.example.idoctor.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.databinding.ActivityDetalleEvaluacionBinding;

public class DetalleEvaluacionActivity extends AppCompatActivity {

    private ActivityDetalleEvaluacionBinding vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleEvaluacionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        mostrarDatos();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarDatos() {
        vista.txtMomento.setText("Fecha: " + obtenerTexto(getIntent().getStringExtra("momento")));
        vista.txtIdCita.setText("Cita: " + obtenerTexto(getIntent().getStringExtra("idCita")));
        vista.txtDescripcion.setText(obtenerTexto(getIntent().getStringExtra("descripcion")));
        vista.txtExploracion.setText(obtenerTexto(getIntent().getStringExtra("exploracion")));
        vista.txtTratamiento.setText(obtenerTexto(getIntent().getStringExtra("tratamiento")));
    }

    private String obtenerTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Sin datos";
        }

        return texto;
    }
}
