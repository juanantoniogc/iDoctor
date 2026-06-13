package com.example.idoctor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.HorarioDao;
import com.example.idoctor.databinding.ActivityFormularioHorarioBinding;
import com.example.idoctor.models.Horario;

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
        cargarDatosSiEdita();

        vista.btnGuardar.setOnClickListener(view -> guardarHorario());
        vista.btnCancelar.setOnClickListener(view -> finish());
    }

    private void configurarDiasSemana() {
        String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diasSemana);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vista.spDiaSemana.setAdapter(adaptador);
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

        for (int i = 0; i < vista.spDiaSemana.getCount(); i++) {
            if (diaSemana.equals(vista.spDiaSemana.getItemAtPosition(i).toString())) {
                vista.spDiaSemana.setSelection(i);
                return;
            }
        }
    }

    private void guardarHorario() {
        String diaSemana = vista.spDiaSemana.getSelectedItem().toString();
        String horaInicio = vista.edtHoraInicio.getText().toString().trim();
        String horaFin = vista.edtHoraFin.getText().toString().trim();

        if (TextUtils.isEmpty(idConsulta)) {
            Toast.makeText(this, "No se encontro la consulta", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(horaInicio) || TextUtils.isEmpty(horaFin)) {
            Toast.makeText(this, "Rellena hora de inicio y hora de fin", Toast.LENGTH_SHORT).show();
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
}
