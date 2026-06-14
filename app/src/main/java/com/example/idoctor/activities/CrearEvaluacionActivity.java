package com.example.idoctor.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.EvaluacionDao;
import com.example.idoctor.databinding.ActivityCrearEvaluacionBinding;
import com.example.idoctor.models.Evaluacion;
import com.example.idoctor.validations.Validaciones;

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
        configurarLimpiezaErrores();

        vista.btnGuardarEvaluacion.setOnClickListener(view -> guardarEvaluacion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.edtDescripcion);
        Validaciones.limpiarErrorAlCambiar(vista.edtExploracion);
        Validaciones.limpiarErrorAlCambiar(vista.edtTratamiento);
    }

    private void guardarEvaluacion() {
        String descripcion = vista.edtDescripcion.getText().toString().trim();
        String exploracion = vista.edtExploracion.getText().toString().trim();
        String tratamiento = vista.edtTratamiento.getText().toString().trim();

        if (estaVacio(idCita) || estaVacio(idPaciente)) {
            Toast.makeText(this, "Faltan datos de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        limpiarErrores();

        if (!formularioValido(descripcion, exploracion, tratamiento)) {
            return;
        }

        vista.btnGuardarEvaluacion.setEnabled(false);
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
                vista.btnGuardarEvaluacion.setEnabled(true);
                Toast.makeText(this, "No se pudo guardar la evaluacion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean formularioValido(String descripcion, String exploracion, String tratamiento) {
        boolean valido = true;

        if (!Validaciones.textoEntre(descripcion, 20, 1000)) {
            vista.edtDescripcion.setError("La descripcion debe tener entre 20 y 1000 caracteres");
            valido = false;
        }

        if (!Validaciones.textoEntre(exploracion, 20, 1000)) {
            vista.edtExploracion.setError("La exploracion debe tener entre 20 y 1000 caracteres");
            valido = false;
        }

        if (!Validaciones.textoEntre(tratamiento, 20, 1000)) {
            vista.edtTratamiento.setError("El tratamiento debe tener entre 20 y 1000 caracteres");
            valido = false;
        }

        return valido;
    }

    private void limpiarErrores() {
        vista.edtDescripcion.setError(null);
        vista.edtExploracion.setError(null);
        vista.edtTratamiento.setError(null);
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
