package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.dao.ProfesionalDao;
import com.example.idoctor.databinding.ActivityDetalleCitaProfesionalBinding;
import com.example.idoctor.models.Consulta;
import com.example.idoctor.models.Profesional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetalleCitaProfesionalActivity extends AppCompatActivity {

    private ActivityDetalleCitaProfesionalBinding vista;
    private ConsultaDao consultaDao;
    private ProfesionalDao profesionalDao;
    private String idCita;
    private String idPaciente;
    private String idConsulta;
    private String idProfesional;
    private String fecha;
    private String hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleCitaProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        consultaDao = new ConsultaDao();
        profesionalDao = new ProfesionalDao();

        recogerDatos();
        mostrarDatos();

        vista.btnCrearEvaluacion.setOnClickListener(view -> abrirCrearEvaluacion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void recogerDatos() {
        idCita = getIntent().getStringExtra("idCita");
        idPaciente = getIntent().getStringExtra("idPaciente");
        idConsulta = getIntent().getStringExtra("idConsulta");
        idProfesional = getIntent().getStringExtra("idProfesional");
        fecha = getIntent().getStringExtra("fecha");
        hora = getIntent().getStringExtra("hora");
    }

    private void mostrarDatos() {
        vista.txtNombreConsulta.setText("Cargando consulta...");
        vista.txtNombreProfesional.setText("Cargando profesional...");
        vista.txtFechaHora.setText(formatearFechaHora(fecha, hora));
        cargarNombreConsulta();
        cargarNombreProfesional();
    }

    private void cargarNombreConsulta() {
        if (estaVacio(idConsulta)) {
            vista.txtNombreConsulta.setText("Sin datos");
            return;
        }

        consultaDao.obtenerConsulta(idConsulta, new ConsultaDao.ConsultaListener() {
            @Override
            public void consultaEncontrada(Consulta consulta) {
                vista.txtNombreConsulta.setText(obtenerTexto(consulta == null ? null : consulta.getTitulo()));
            }

            @Override
            public void error(String mensajeError) {
                vista.txtNombreConsulta.setText("Sin datos");
            }
        });
    }

    private void cargarNombreProfesional() {
        if (estaVacio(idProfesional)) {
            vista.txtNombreProfesional.setText("Sin datos");
            return;
        }

        profesionalDao.obtenerProfesional(idProfesional, new ProfesionalDao.ProfesionalListener() {
            @Override
            public void profesionalEncontrado(Profesional profesional) {
                vista.txtNombreProfesional.setText(nombreCompleto(profesional));
            }

            @Override
            public void error(String mensajeError) {
                vista.txtNombreProfesional.setText("Sin datos");
            }
        });
    }

    private void abrirCrearEvaluacion() {
        if (estaVacio(idPaciente)) {
            Toast.makeText(this, "Esta cita todavia no tiene paciente", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CrearEvaluacionActivity.class);
        intent.putExtra("idCita", idCita);
        intent.putExtra("idPaciente", idPaciente);
        intent.putExtra("fecha", fecha);
        intent.putExtra("hora", hora);
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

    private String nombreCompleto(Profesional profesional) {
        if (profesional == null) {
            return "Sin datos";
        }

        String nombre = profesional.getNombre() == null ? "" : profesional.getNombre().trim();
        String apellidos = profesional.getApellidos() == null ? "" : profesional.getApellidos().trim();
        String nombreCompleto = (nombre + " " + apellidos).trim();
        return nombreCompleto.isEmpty() ? "Sin datos" : nombreCompleto;
    }

    private String formatearFechaHora(String fecha, String hora) {
        String horaTexto = obtenerTexto(hora);
        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        entrada.setLenient(false);

        try {
            Date fechaParseada = entrada.parse(fecha);
            if (fechaParseada == null) {
                return obtenerTexto(fecha) + " a las " + horaTexto;
            }
            return salida.format(fechaParseada) + " a las " + horaTexto;
        } catch (ParseException e) {
            return obtenerTexto(fecha) + " a las " + horaTexto;
        }
    }
}
