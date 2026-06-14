package com.example.idoctor.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.databinding.ActivityFormularioCitaBinding;
import com.example.idoctor.models.Cita;
import com.example.idoctor.models.Consulta;
import com.example.idoctor.validations.Validaciones;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FormularioCitaActivity extends AppCompatActivity {

    private ActivityFormularioCitaBinding vista;
    private AutenticacionDao autenticacionDao;
    private ConsultaDao consultaDao;
    private CitaDao citaDao;
    private List<Consulta> consultas;
    private Consulta consultaSeleccionada;
    private String idCita;
    private String idPaciente;
    private String idConsultaEditando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityFormularioCitaBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        consultaDao = new ConsultaDao();
        citaDao = new CitaDao();
        consultas = new ArrayList<>();

        cargarDatosSiEdita();
        cargarConsultas();
        configurarLimpiezaErrores();

        vista.edtFecha.setOnClickListener(view -> elegirFecha());
        vista.edtHora.setOnClickListener(view -> elegirHora());
        vista.btnGuardar.setOnClickListener(view -> guardarCita());
        vista.btnCancelar.setOnClickListener(view -> finish());
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.spConsultas);
        Validaciones.limpiarErrorAlCambiar(vista.edtFecha);
        Validaciones.limpiarErrorAlCambiar(vista.edtHora);
    }

    private void cargarDatosSiEdita() {
        idCita = getIntent().getStringExtra("idCita");
        idPaciente = getIntent().getStringExtra("idPaciente");
        idConsultaEditando = getIntent().getStringExtra("idConsulta");

        vista.edtFecha.setText(getIntent().getStringExtra("fecha"));
        vista.edtHora.setText(getIntent().getStringExtra("hora"));
        vista.cbCitaActiva.setChecked(getIntent().getBooleanExtra("activa", true));
    }

    private void cargarConsultas() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();
        consultaDao.obtenerConsultasPorProfesional(idProfesional, new ConsultaDao.ConsultasListener() {
            @Override
            public void consultasEncontradas(List<Consulta> consultasEncontradas) {
                consultas.clear();
                consultas.addAll(consultasEncontradas);
                configurarSelectorConsultas();
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(FormularioCitaActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSelectorConsultas() {
        List<String> titulos = new ArrayList<>();

        for (Consulta consulta : consultas) {
            titulos.add(consulta.getTitulo());
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                titulos
        );

        vista.spConsultas.setAdapter(adaptador);

        if (!consultas.isEmpty()) {
            int posicion = obtenerPosicionConsultaEditando();
            consultaSeleccionada = consultas.get(posicion);
            vista.spConsultas.setText(titulos.get(posicion), false);
        }

        vista.spConsultas.setOnItemClickListener((parent, view, position, id) -> {
            consultaSeleccionada = consultas.get(position);
        });
    }

    private int obtenerPosicionConsultaEditando() {
        if (TextUtils.isEmpty(idConsultaEditando)) {
            return 0;
        }

        for (int i = 0; i < consultas.size(); i++) {
            if (idConsultaEditando.equals(consultas.get(i).getId())) {
                return i;
            }
        }

        return 0;
    }

    private void elegirFecha() {
        Calendar calendario = Calendar.getInstance();
        DatePickerDialog dialogo = new DatePickerDialog(
                this,
                (view, anio, mes, dia) -> {
                    String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", anio, mes + 1, dia);
                    vista.edtFecha.setText(fecha);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );
        dialogo.show();
    }

    private void elegirHora() {
        Calendar calendario = Calendar.getInstance();
        TimePickerDialog dialogo = new TimePickerDialog(
                this,
                (view, hora, minuto) -> {
                    String horaTexto = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto);
                    vista.edtHora.setText(horaTexto);
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
        );
        dialogo.show();
    }

    private void guardarCita() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        Consulta consulta = obtenerConsultaSeleccionada();
        String fecha = vista.edtFecha.getText().toString().trim();
        String hora = vista.edtHora.getText().toString().trim();

        limpiarErrores();

        if (!formularioValido(consulta, fecha, hora)) {
            return;
        }

        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();
        String paciente = TextUtils.isEmpty(idPaciente) ? "" : idPaciente;

        if (!TextUtils.isEmpty(paciente) && !Validaciones.idSimpleValido(paciente)) {
            Toast.makeText(this, "El paciente no tiene un formato valido", Toast.LENGTH_SHORT).show();
            return;
        }

        vista.btnGuardar.setEnabled(false);
        Cita cita = new Cita(
                idCita,
                consulta.getId(),
                idProfesional,
                paciente,
                fecha,
                hora,
                vista.cbCitaActiva.isChecked()
        );

        citaDao.guardarCita(cita).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Cita guardada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                vista.btnGuardar.setEnabled(true);
                Toast.makeText(this, "No se pudo guardar la cita", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean formularioValido(Consulta consulta, String fecha, String hora) {
        boolean valido = true;

        if (consulta == null) {
            vista.spConsultas.setError("Selecciona una consulta");
            valido = false;
        }

        if (TextUtils.isEmpty(fecha)) {
            vista.edtFecha.setError("La fecha es obligatoria");
            valido = false;
        } else if (!Validaciones.fechaValida(fecha)) {
            vista.edtFecha.setError("La fecha debe tener formato yyyy-MM-dd");
            valido = false;
        } else if (!Validaciones.fechaNoPasada(fecha)) {
            vista.edtFecha.setError("La fecha no puede ser anterior a hoy");
            valido = false;
        }

        if (TextUtils.isEmpty(hora)) {
            vista.edtHora.setError("La hora es obligatoria");
            valido = false;
        } else if (!Validaciones.horaValida(hora)) {
            vista.edtHora.setError("La hora debe tener formato HH:mm");
            valido = false;
        }

        return valido;
    }

    private void limpiarErrores() {
        vista.spConsultas.setError(null);
        vista.edtFecha.setError(null);
        vista.edtHora.setError(null);
    }

    private Consulta obtenerConsultaSeleccionada() {
        if (consultaSeleccionada != null) {
            return consultaSeleccionada;
        }

        String tituloSeleccionado = vista.spConsultas.getText().toString().trim();
        for (Consulta consulta : consultas) {
            if (tituloSeleccionado.equals(consulta.getTitulo())) {
                return consulta;
            }
        }

        return null;
    }
}
