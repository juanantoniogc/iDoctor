package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorHorarios;
import com.example.idoctor.dao.HorarioDao;
import com.example.idoctor.databinding.ActivityListaHorariosBinding;
import com.example.idoctor.models.Horario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListaHorariosActivity extends AppCompatActivity {

    private ActivityListaHorariosBinding vista;
    private HorarioDao horarioDao;
    private AdaptadorHorarios adaptadorHorarios;
    private List<Horario> horarios;
    private String idConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityListaHorariosBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        horarioDao = new HorarioDao();
        horarios = new ArrayList<>();
        idConsulta = getIntent().getStringExtra("idConsulta");

        if (idConsulta == null || idConsulta.trim().isEmpty()) {
            Toast.makeText(this, "No se encontro la consulta", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        configurarLista();
        configurarBotones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHorarios();
    }

    private void configurarLista() {
        adaptadorHorarios = new AdaptadorHorarios(horarios, new AdaptadorHorarios.OnHorarioClickListener() {
            @Override
            public void editarHorario(Horario horario) {
                abrirFormulario(horario);
            }

            @Override
            public void eliminarHorario(Horario horario) {
                confirmarEliminarHorario(horario);
            }
        });

        vista.rvHorarios.setLayoutManager(new LinearLayoutManager(this));
        vista.rvHorarios.setAdapter(adaptadorHorarios);
    }

    private void configurarBotones() {
        vista.btnNuevoHorario.setOnClickListener(view -> abrirFormulario(null));
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void cargarHorarios() {
        horarioDao.obtenerHorariosPorConsulta(idConsulta, new HorarioDao.HorariosListener() {
            @Override
            public void horariosEncontrados(List<Horario> horariosEncontrados) {
                Collections.sort(horariosEncontrados, (primero, segundo) -> {
                    int comparacionDia = Integer.compare(
                            posicionDia(primero.getDiaSemana()),
                            posicionDia(segundo.getDiaSemana())
                    );

                    if (comparacionDia != 0) {
                        return comparacionDia;
                    }

                    return textoSeguro(primero.getHoraInicio()).compareTo(textoSeguro(segundo.getHoraInicio()));
                });

                horarios.clear();
                horarios.addAll(horariosEncontrados);
                adaptadorHorarios.notifyDataSetChanged();
                vista.txtSinHorarios.setVisibility(horarios.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(ListaHorariosActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int posicionDia(String diaSemana) {
        if (diaSemana == null) {
            return 99;
        }

        switch (diaSemana.trim().toLowerCase()) {
            case "lunes":
                return 1;
            case "martes":
                return 2;
            case "miercoles":
            case "miércoles":
                return 3;
            case "jueves":
                return 4;
            case "viernes":
                return 5;
            case "sabado":
            case "sábado":
                return 6;
            case "domingo":
                return 7;
            default:
                return 99;
        }
    }

    private String textoSeguro(String texto) {
        return texto == null ? "" : texto;
    }

    private void abrirFormulario(Horario horario) {
        Intent intent = new Intent(this, FormularioHorarioActivity.class);
        intent.putExtra("idConsulta", idConsulta);

        if (horario != null) {
            intent.putExtra("idHorario", horario.getId());
            intent.putExtra("diaSemana", horario.getDiaSemana());
            intent.putExtra("horaInicio", horario.getHoraInicio());
            intent.putExtra("horaFin", horario.getHoraFin());
        }

        startActivity(intent);
    }

    private void confirmarEliminarHorario(Horario horario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar horario")
                .setMessage("Quieres eliminar este horario?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarHorario(horario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarHorario(Horario horario) {
        horarioDao.eliminarHorario(horario.getId()).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Horario eliminado", Toast.LENGTH_SHORT).show();
                cargarHorarios();
            } else {
                Toast.makeText(this, "No se pudo eliminar el horario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
