package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.databinding.ActivityDetalleCitaBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DetalleCitaActivity extends AppCompatActivity {

    private ActivityDetalleCitaBinding vista;
    private AutenticacionDao autenticacionDao;
    private CitaDao citaDao;
    private String idCita;
    private String fecha;
    private String modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleCitaBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        citaDao = new CitaDao();

        recogerDatos();
        mostrarDatos();
        configurarBotonAccion();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void recogerDatos() {
        idCita = getIntent().getStringExtra("idCita");
        fecha = getIntent().getStringExtra("fecha");
        modo = getIntent().getStringExtra("modo");
    }

    private void mostrarDatos() {
        vista.txtFecha.setText("Fecha: " + obtenerTexto(fecha));
        vista.txtHora.setText("Hora: " + obtenerTexto(getIntent().getStringExtra("hora")));
        vista.txtIdConsulta.setText("Consulta: " + obtenerTexto(getIntent().getStringExtra("idConsulta")));
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
                finish();
            } else {
                Toast.makeText(this, "No se pudo reservar la cita", Toast.LENGTH_SHORT).show();
            }
        });
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
}
