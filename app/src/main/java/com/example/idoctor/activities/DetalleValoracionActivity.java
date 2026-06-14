package com.example.idoctor.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.databinding.ActivityDetalleValoracionBinding;

public class DetalleValoracionActivity extends AppCompatActivity {

    private ActivityDetalleValoracionBinding vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleValoracionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        mostrarDatos();
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarDatos() {
        int estrellas = getIntent().getIntExtra("estrellas", 0);

        vista.txtEstrellas.setText("Estrellas: " + estrellas + "/5");
        vista.txtComentario.setText("Comentario: " + texto(getIntent().getStringExtra("comentario")));
        vista.txtMomento.setText("Momento: " + texto(getIntent().getStringExtra("momento")));
        vista.txtPaciente.setText("Paciente: " + texto(getIntent().getStringExtra("idPaciente")));
        vista.txtCita.setText("Cita: " + texto(getIntent().getStringExtra("idCita")));
    }

    private String texto(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "Sin datos";
        }

        return valor;
    }
}
