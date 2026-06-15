package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorCitas;
import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.dao.EvaluacionDao;
import com.example.idoctor.databinding.ActivityMisCitasBinding;
import com.example.idoctor.models.Cita;
import com.example.idoctor.models.Evaluacion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MisCitasActivity extends AppCompatActivity {

    private ActivityMisCitasBinding vista;
    private AutenticacionDao autenticacionDao;
    private CitaDao citaDao;
    private EvaluacionDao evaluacionDao;
    private AdaptadorCitas adaptadorCitas;
    private List<Cita> citas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityMisCitasBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        citaDao = new CitaDao();
        evaluacionDao = new EvaluacionDao();
        citas = new ArrayList<>();

        configurarLista();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMisCitas();
    }

    private void configurarLista() {
        adaptadorCitas = new AdaptadorCitas(citas, cita -> {
            Intent intent = new Intent(this, DetalleCitaActivity.class);
            ponerDatosCita(intent, cita);
            intent.putExtra("modo", "cancelar");
            startActivity(intent);
        });
        vista.rvMisCitas.setLayoutManager(new LinearLayoutManager(this));
        vista.rvMisCitas.setAdapter(adaptadorCitas);
    }

    private void cargarMisCitas() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String idPaciente = autenticacionDao.obtenerUsuarioActual().getUid();

        citaDao.obtenerCitasPorPaciente(idPaciente, new CitaDao.CitasListener() {
            @Override
            public void citasEncontradas(List<Cita> citasEncontradas) {
                cargarEvaluacionesYMostrarCitas(idPaciente, citasEncontradas);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MisCitasActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEvaluacionesYMostrarCitas(String idPaciente, List<Cita> citasEncontradas) {
        evaluacionDao.obtenerEvaluacionesPorPaciente(idPaciente, new EvaluacionDao.EvaluacionesListener() {
            @Override
            public void evaluacionesEncontradas(List<Evaluacion> evaluaciones) {
                Set<String> idsCitasEvaluadas = new HashSet<>();

                for (Evaluacion evaluacion : evaluaciones) {
                    if (evaluacion.getIdCita() != null && !evaluacion.getIdCita().trim().isEmpty()) {
                        idsCitasEvaluadas.add(evaluacion.getIdCita());
                    }
                }

                citas.clear();

                for (Cita cita : citasEncontradas) {
                    if (!idsCitasEvaluadas.contains(cita.getId())) {
                        citas.add(cita);
                    }
                }

                adaptadorCitas.notifyDataSetChanged();
                vista.txtSinCitas.setVisibility(citas.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MisCitasActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ponerDatosCita(Intent intent, Cita cita) {
        intent.putExtra("idCita", cita.getId());
        intent.putExtra("idConsulta", cita.getIdConsulta());
        intent.putExtra("idProfesional", cita.getIdProfesional());
        intent.putExtra("idPaciente", cita.getIdPaciente());
        intent.putExtra("fecha", cita.getFecha());
        intent.putExtra("hora", cita.getHora());
    }
}
