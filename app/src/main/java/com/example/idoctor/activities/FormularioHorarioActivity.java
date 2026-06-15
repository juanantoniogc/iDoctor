package com.example.idoctor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.HorarioDao;
import com.example.idoctor.databinding.ActivityFormularioHorarioBinding;
import com.example.idoctor.models.Horario;
import com.example.idoctor.validations.Validaciones;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Locale;

public class FormularioHorarioActivity extends AppCompatActivity {

    private ActivityFormularioHorarioBinding vista;
    private HorarioDao horarioDao;
    private String idConsulta;
    private String idHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityFormularioHorarioBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        horarioDao = new HorarioDao();
        idConsulta = getIntent().getStringExtra("idConsulta");
        idHorario = getIntent().getStringExtra("idHorario");

        configurarDiasSemana();
        configurarSelectoresHora();
        cargarDatosSiEdita();
        configurarLimpiezaErrores();

        vista.btnGuardar.setOnClickListener(view -> guardarHorario());
        vista.btnCancelar.setOnClickListener(view -> finish());
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.spDiaSemana);
        Validaciones.limpiarErrorAlCambiar(vista.edtHoraInicio);
        Validaciones.limpiarErrorAlCambiar(vista.edtHoraFin);
    }

    private void configurarDiasSemana() {
        String[] diasSemana = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, diasSemana);
        vista.spDiaSemana.setAdapter(adaptador);
        vista.spDiaSemana.setText(diasSemana[0], false);
    }

    private void configurarSelectoresHora() {
        vista.edtHoraInicio.setOnClickListener(view -> mostrarSelectorHora(true));
        vista.edtHoraFin.setOnClickListener(view -> mostrarSelectorHora(false));
    }

    private void mostrarSelectorHora(boolean esHoraInicio) {
        MaterialTimePicker selectorHora = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(9)
                .setMinute(0)
                .setTitleText(esHoraInicio ? "Hora de inicio" : "Hora de fin")
                .build();

        selectorHora.addOnPositiveButtonClickListener(view -> {
            String hora = String.format(Locale.getDefault(), "%02d:%02d", selectorHora.getHour(), selectorHora.getMinute());

            if (esHoraInicio) {
                vista.edtHoraInicio.setText(hora);
            } else {
                vista.edtHoraFin.setText(hora);
            }
        });

        selectorHora.show(getSupportFragmentManager(), esHoraInicio ? "horaInicio" : "horaFin");
    }

    private void cargarDatosSiEdita() {
        String diaSemana = getIntent().getStringExtra("diaSemana");
        String horaInicio = getIntent().getStringExtra("horaInicio");
        String horaFin = getIntent().getStringExtra("horaFin");

        seleccionarDiaSemana(diaSemana);
        vista.edtHoraInicio.setText(horaInicio);
        vista.edtHoraFin.setText(horaFin);
    }

    private void seleccionarDiaSemana(String diaSemana) {
        if (diaSemana == null) {
            return;
        }

        for (int i = 0; i < vista.spDiaSemana.getAdapter().getCount(); i++) {
            if (diaSemana.equals(vista.spDiaSemana.getAdapter().getItem(i).toString())) {
                vista.spDiaSemana.setText(diaSemana, false);
                return;
            }
        }
    }

    private void guardarHorario() {
        String diaSemana = vista.spDiaSemana.getText().toString().trim();
        String horaInicio = vista.edtHoraInicio.getText().toString().trim();
        String horaFin = vista.edtHoraFin.getText().toString().trim();

        if (TextUtils.isEmpty(idConsulta)) {
            Toast.makeText(this, "No se encontro la consulta", Toast.LENGTH_SHORT).show();
            return;
        }

        limpiarErrores();

        if (!formularioValido(diaSemana, horaInicio, horaFin)) {
            return;
        }

        comprobarDiaYGuardar(diaSemana, horaInicio, horaFin);
    }

    private void comprobarDiaYGuardar(String diaSemana, String horaInicio, String horaFin) {
        vista.btnGuardar.setEnabled(false);

        horarioDao.existeHorarioParaDia(idConsulta, diaSemana, idHorario, new HorarioDao.ExisteHorarioListener() {
            @Override
            public void resultado(boolean existe) {
                if (existe) {
                    vista.btnGuardar.setEnabled(true);
                    vista.spDiaSemana.setError("Ya existe un horario para este dia");
                    Toast.makeText(FormularioHorarioActivity.this,
                            "Esta consulta ya tiene horario para " + diaSemana,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                guardarHorarioEnFirebase(diaSemana, horaInicio, horaFin);
            }

            @Override
            public void error(String mensajeError) {
                vista.btnGuardar.setEnabled(true);
                Toast.makeText(FormularioHorarioActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarHorarioEnFirebase(String diaSemana, String horaInicio, String horaFin) {
        Horario horario = new Horario(idHorario, idConsulta, diaSemana, horaInicio, horaFin);
        horarioDao.guardarHorario(horario).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Horario guardado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                vista.btnGuardar.setEnabled(true);
                Toast.makeText(this, "No se pudo guardar el horario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean formularioValido(String diaSemana, String horaInicio, String horaFin) {
        boolean valido = true;

        if (TextUtils.isEmpty(diaSemana)) {
            vista.spDiaSemana.setError("Selecciona un dia");
            valido = false;
        } else if (!diaValido(diaSemana)) {
            vista.spDiaSemana.setError("Selecciona un dia valido");
            valido = false;
        }

        if (TextUtils.isEmpty(horaInicio)) {
            vista.edtHoraInicio.setError("La hora de inicio es obligatoria");
            valido = false;
        } else if (!Validaciones.horaValida(horaInicio)) {
            vista.edtHoraInicio.setError("La hora debe tener formato HH:mm");
            valido = false;
        }

        if (TextUtils.isEmpty(horaFin)) {
            vista.edtHoraFin.setError("La hora de fin es obligatoria");
            valido = false;
        } else if (!Validaciones.horaValida(horaFin)) {
            vista.edtHoraFin.setError("La hora debe tener formato HH:mm");
            valido = false;
        }

        if (Validaciones.horaValida(horaInicio) && Validaciones.horaValida(horaFin)) {
            int inicio = Validaciones.horaAMinutos(horaInicio);
            int fin = Validaciones.horaAMinutos(horaFin);

            if (fin <= inicio) {
                vista.edtHoraFin.setError("La hora de fin debe ser mayor que la de inicio");
                valido = false;
            } else if (fin - inicio < 10) {
                vista.edtHoraFin.setError("El horario debe durar al menos 10 minutos");
                valido = false;
            }
        }

        return valido;
    }

    private boolean diaValido(String diaSemana) {
        for (int i = 0; i < vista.spDiaSemana.getAdapter().getCount(); i++) {
            if (diaSemana.equals(vista.spDiaSemana.getAdapter().getItem(i).toString())) {
                return true;
            }
        }

        return false;
    }

    private void limpiarErrores() {
        vista.spDiaSemana.setError(null);
        vista.edtHoraInicio.setError(null);
        vista.edtHoraFin.setError(null);
    }
}
