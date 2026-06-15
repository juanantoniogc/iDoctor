package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.dao.ProfesionalDao;
import com.example.idoctor.databinding.ActivityDetalleCitaBinding;
import com.example.idoctor.models.Consulta;
import com.example.idoctor.models.Profesional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetalleCitaActivity extends AppCompatActivity {

    private ActivityDetalleCitaBinding vista;
    private AutenticacionDao autenticacionDao;
    private CitaDao citaDao;
    private ConsultaDao consultaDao;
    private ProfesionalDao profesionalDao;
    private String idCita;
    private String fecha;
    private String hora;
    private String idConsulta;
    private String idProfesional;
    private String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleCitaBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        citaDao = new CitaDao();
        consultaDao = new ConsultaDao();
        profesionalDao = new ProfesionalDao();

        recogerDatos();
        mostrarDatos();
        configurarBotonAccion();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void recogerDatos() {
        idCita = getIntent().getStringExtra("idCita");
        fecha = getIntent().getStringExtra("fecha");
        hora = getIntent().getStringExtra("hora");
        idConsulta = getIntent().getStringExtra("idConsulta");
        idProfesional = getIntent().getStringExtra("idProfesional");
        modo = getIntent().getStringExtra("modo");
    }

    private void mostrarDatos() {
        vista.txtNombreConsulta.setText("Cargando consulta...");
        vista.txtNombreProfesional.setText("Cargando profesional...");
        vista.txtFechaHora.setText(formatearFechaHora(fecha, hora));
        cargarNombreConsulta();
        cargarNombreProfesional();
    }

    private void cargarNombreConsulta() {
        if (idConsulta == null || idConsulta.trim().isEmpty()) {
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
        if (idProfesional == null || idProfesional.trim().isEmpty()) {
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

    private void configurarBotonAccion() {
        if ("cancelar".equals(modo)) {
            vista.btnAccionCita.setText("Cancelar cita");

            if (fechaYaHaPasado(fecha)) {
                vista.btnAccionCita.setText("No se puede cancelar");
                vista.btnAccionCita.setEnabled(false);
            }

            vista.btnAccionCita.setOnClickListener(view -> cancelarCita());
        } else {
            vista.btnAccionCita.setText("Reservar cita");
            vista.btnAccionCita.setOnClickListener(view -> reservarCita());
        }
    }

    private void reservarCita() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        if (idCita == null || idCita.trim().isEmpty()) {
            Toast.makeText(this, "No se encontro la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        String idPaciente = autenticacionDao.obtenerUsuarioActual().getUid();

        citaDao.reservarCita(idCita, idPaciente).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cita reservada", Toast.LENGTH_SHORT).show();
                volverAlMenuPaciente();
            } else {
                Toast.makeText(this, "No se pudo reservar la cita", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void volverAlMenuPaciente() {
        Intent intent = new Intent(this, MenuPacienteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void cancelarCita() {
        if (idCita == null || idCita.trim().isEmpty()) {
            Toast.makeText(this, "No se encontro la cita", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaYaHaPasado(fecha)) {
            Toast.makeText(this, "No puedes cancelar una cita pasada", Toast.LENGTH_SHORT).show();
            return;
        }

        citaDao.cancelarCita(idCita).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cita cancelada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "No se pudo cancelar la cita", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean fechaYaHaPasado(String fechaCita) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formato.setLenient(false);

        try {
            Date fechaParseada = formato.parse(fechaCita);

            if (fechaParseada == null) {
                return false;
            }

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);

            Calendar diaCita = Calendar.getInstance();
            diaCita.setTime(fechaParseada);

            return diaCita.before(hoy);
        } catch (ParseException e) {
            return false;
        }
    }

    private String obtenerTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
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
