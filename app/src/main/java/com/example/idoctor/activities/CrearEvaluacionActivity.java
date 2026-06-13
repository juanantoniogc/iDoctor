package com.example.idoctor.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.EvaluacionDao;
import com.example.idoctor.databinding.ActivityCrearEvaluacionBinding;
import com.example.idoctor.models.Evaluacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearEvaluacionActivity extends AppCompatActivity {

    private ActivityCrearEvaluacionBinding vista;
    private EvaluacionDao evaluacionDao;
    private String idCita;
    private String idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityCrearEvaluacionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        evaluacionDao = new EvaluacionDao();
        idCita = getIntent().getStringExtra("idCita");
        idPaciente = getIntent().getStringExtra("idPaciente");

        vista.txtDatosCita.setText("Cita: " + obtenerTexto(idCita));
        vista.txtDatosPaciente.setText("Paciente: " + obtenerTexto(idPaciente));

        vista.btnGuardarEvaluacion.setOnClickListener(view -> guardarEvaluacion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void guardarEvaluacion() {
        String descripcion = vista.edtDescripcion.getText().toString().trim();
        String exploracion = vista.edtExploracion.getText().toString().trim();
        String tratamiento = vista.edtTratamiento.getText().toString().trim();

        if (estaVacio(idCita) || estaVacio(idPaciente)) {
            Toast.makeText(this, "Faltan datos de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion.isEmpty() || exploracion.isEmpty() || tratamiento.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Evaluacion evaluacion = new Evaluacion(
                "",
                idCita,
                idPaciente,
                descripcion,
                exploracion,
                tratamiento,
                obtenerMomentoActual()
        );

        evaluacionDao.guardarEvaluacion(evaluacion).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Evaluacion guardada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "No se pudo guardar la evaluacion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String obtenerMomentoActual() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formato.format(new Date());
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
