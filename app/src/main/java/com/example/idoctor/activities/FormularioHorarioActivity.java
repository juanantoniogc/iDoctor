package com.example.idoctor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.HorarioDao;
import com.example.idoctor.databinding.ActivityFormularioHorarioBinding;
import com.example.idoctor.models.Horario;
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

        vista.btnGuardar.setOnClickListener(view -> guardarHorario());
        vista.btnCancelar.setOnClickListener(view -> finish());
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

        if (TextUtils.isEmpty(diaSemana) || TextUtils.isEmpty(horaInicio) || TextUtils.isEmpty(horaFin)) {
            Toast.makeText(this, "Rellena dia, hora de inicio y hora de fin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (convertirHoraAMinutos(horaFin) <= convertirHoraAMinutos(horaInicio)) {
            Toast.makeText(this, "La hora final no puede ser inferior o igual a la hora de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        Horario horario = new Horario(idHorario, idConsulta, diaSemana, horaInicio, horaFin);
        horarioDao.guardarHorario(horario).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Horario guardado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "No se pudo guardar el horario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int convertirHoraAMinutos(String hora) {
        String[] partes = hora.split(":");

        if (partes.length != 2) {
            return -1;
        }

        try {
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);
            return horas * 60 + minutos;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
