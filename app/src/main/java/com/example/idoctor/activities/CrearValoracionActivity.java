package com.example.idoctor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.dao.EvaluacionDao;
import com.example.idoctor.dao.ProfesionalDao;
import com.example.idoctor.dao.ValoracionDao;
import com.example.idoctor.databinding.ActivityCrearValoracionBinding;
import com.example.idoctor.models.Cita;
import com.example.idoctor.models.Profesional;
import com.example.idoctor.models.Valoracion;
import com.example.idoctor.validations.Validaciones;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearValoracionActivity extends AppCompatActivity {

    private ActivityCrearValoracionBinding vista;
    private AutenticacionDao autenticacionDao;
    private CitaDao citaDao;
    private EvaluacionDao evaluacionDao;
    private ProfesionalDao profesionalDao;
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
        citaDao = new CitaDao();
        evaluacionDao = new EvaluacionDao();
        profesionalDao = new ProfesionalDao();
        valoracionDao = new ValoracionDao();

        idCita = getIntent().getStringExtra("idCita");
        idProfesional = getIntent().getStringExtra("idProfesional");
        idPacienteCita = getIntent().getStringExtra("idPaciente");

        vista.txtDatosCita.setText("Cargando cita");
        vista.txtDatosProfesional.setText("Cargando profesional");
        cargarDatosVisibles();
        configurarLimpiezaErrores();

        vista.btnGuardarValoracion.setOnClickListener(view -> guardarValoracion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void cargarDatosVisibles() {
        if (estaVacio(idCita)) {
            vista.txtDatosCita.setText("Cita del Sin datos");
        } else {
            cargarCitaVisible();
        }

        if (estaVacio(idProfesional)) {
            vista.txtDatosProfesional.setText("Con el profesional Sin datos");
        } else {
            cargarProfesionalVisible();
        }
    }

    private void cargarCitaVisible() {
        citaDao.obtenerCitaPorId(idCita, new CitaDao.CitaListener() {
            @Override
            public void citaEncontrada(Cita cita) {
                vista.txtDatosCita.setText("Cita del " + formatearCita(cita));
            }

            @Override
            public void error(String mensajeError) {
                vista.txtDatosCita.setText("Cita del Sin datos");
            }
        });
    }

    private void cargarProfesionalVisible() {
        profesionalDao.obtenerProfesional(idProfesional, new ProfesionalDao.ProfesionalListener() {
            @Override
            public void profesionalEncontrado(Profesional profesional) {
                vista.txtDatosProfesional.setText("Con el profesional " + obtenerNombreProfesional(profesional));
            }

            @Override
            public void error(String mensajeError) {
                vista.txtDatosProfesional.setText("Con el profesional Sin datos");
            }
        });
    }

    private String formatearCita(Cita cita) {
        if (cita == null) {
            return "Sin datos";
        }

        return formatearFecha(cita.getFecha()) + " a las " + texto(cita.getHora());
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

    private String obtenerNombreProfesional(Profesional profesional) {
        if (profesional == null) {
            return "Sin datos";
        }

        String nombreCompleto = (texto(profesional.getNombre()) + " " + texto(profesional.getApellidos())).trim();
        if (nombreCompleto.equals("Sin datos Sin datos")) {
            return "Sin datos";
        }

        return nombreCompleto;
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.edtComentario);
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

        String comentario = vista.edtComentario.getText().toString().trim();
        vista.edtComentario.setError(null);
        if (!comentarioValido(comentario)) {
            return;
        }

        vista.btnGuardarValoracion.setEnabled(false);
        comprobarEvaluacionYGuardar(idPacienteActual, estrellas);
    }

    private boolean comentarioValido(String comentario) {
        if (TextUtils.isEmpty(comentario)) {
            return true;
        }

        if (!Validaciones.textoEntre(comentario, 3, 300)) {
            vista.edtComentario.setError("El comentario debe tener entre 3 y 300 caracteres");
            return false;
        }

        return true;
    }

    private void comprobarEvaluacionYGuardar(String idPacienteActual, int estrellas) {
        evaluacionDao.existeEvaluacionParaCita(idCita, new EvaluacionDao.ExisteEvaluacionListener() {
            @Override
            public void resultado(boolean existe) {
                if (!existe) {
                    vista.btnGuardarValoracion.setEnabled(true);
                    Toast.makeText(CrearValoracionActivity.this,
                            "Solo puedes valorar citas que tengan evaluacion",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                guardarValoracionEnFirebase(idPacienteActual, estrellas);
            }

            @Override
            public void error(String mensajeError) {
                vista.btnGuardarValoracion.setEnabled(true);
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
                vista.btnGuardarValoracion.setEnabled(true);
                Toast.makeText(this, tarea.getException().getMessage(), Toast.LENGTH_LONG).show();
            } else {
                vista.btnGuardarValoracion.setEnabled(true);
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
