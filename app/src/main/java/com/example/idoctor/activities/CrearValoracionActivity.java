package com.example.idoctor.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.EvaluacionDao;
import com.example.idoctor.dao.ValoracionDao;
import com.example.idoctor.databinding.ActivityCrearValoracionBinding;
import com.example.idoctor.models.Valoracion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearValoracionActivity extends AppCompatActivity {

    private ActivityCrearValoracionBinding vista;
    private AutenticacionDao autenticacionDao;
    private EvaluacionDao evaluacionDao;
    private ValoracionDao valoracionDao;
    private String idCita;
    private String idProfesional;
    private String idPacienteCita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityCrearValoracionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        evaluacionDao = new EvaluacionDao();
        valoracionDao = new ValoracionDao();

        idCita = getIntent().getStringExtra("idCita");
        idProfesional = getIntent().getStringExtra("idProfesional");
        idPacienteCita = getIntent().getStringExtra("idPaciente");

        vista.txtDatosCita.setText("Cita: " + texto(idCita));
        vista.txtDatosProfesional.setText("Profesional: " + texto(idProfesional));

        vista.btnGuardarValoracion.setOnClickListener(view -> guardarValoracion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void guardarValoracion() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            Toast.makeText(this, "No hay usuario iniciado", Toast.LENGTH_SHORT).show();
            return;
        }

        String idPacienteActual = autenticacionDao.obtenerUsuarioActual().getUid();

        if (estaVacio(idCita) || estaVacio(idProfesional) || estaVacio(idPacienteCita)) {
            Toast.makeText(this, "Faltan datos de la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!idPacienteActual.equals(idPacienteCita)) {
            Toast.makeText(this, "Solo puedes valorar tus propias citas", Toast.LENGTH_SHORT).show();
            return;
        }

        int estrellas = Math.round(vista.ratingEstrellas.getRating());
        if (estrellas < 1 || estrellas > 5) {
            Toast.makeText(this, "Elige entre 1 y 5 estrellas", Toast.LENGTH_SHORT).show();
            return;
        }

        comprobarEvaluacionYGuardar(idPacienteActual, estrellas);
    }

    private void comprobarEvaluacionYGuardar(String idPacienteActual, int estrellas) {
        evaluacionDao.existeEvaluacionParaCita(idCita, new EvaluacionDao.ExisteEvaluacionListener() {
            @Override
            public void resultado(boolean existe) {
                if (!existe) {
                    Toast.makeText(CrearValoracionActivity.this,
                            "Solo puedes valorar citas que tengan evaluacion",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                guardarValoracionEnFirebase(idPacienteActual, estrellas);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(CrearValoracionActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarValoracionEnFirebase(String idPacienteActual, int estrellas) {
        Valoracion valoracion = new Valoracion(
                "",
                idProfesional,
                idPacienteActual,
                idCita,
                estrellas,
                vista.edtComentario.getText().toString().trim(),
                obtenerMomentoActual()
        );

        valoracionDao.guardarValoracion(valoracion).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Valoracion guardada", Toast.LENGTH_SHORT).show();
                finish();
            } else if (tarea.getException() != null) {
                Toast.makeText(this, tarea.getException().getMessage(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No se pudo guardar la valoracion", Toast.LENGTH_SHORT).show();
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

    private String texto(String valor) {
        return estaVacio(valor) ? "Sin datos" : valor;
    }
}
