package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorEvaluaciones;
import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.EvaluacionDao;
import com.example.idoctor.databinding.ActivityEvaluacionesPacienteBinding;
import com.example.idoctor.models.Evaluacion;

import java.util.ArrayList;
import java.util.List;

public class EvaluacionesPacienteActivity extends AppCompatActivity {

    private ActivityEvaluacionesPacienteBinding vista;
    private AutenticacionDao autenticacionDao;
    private EvaluacionDao evaluacionDao;
    private AdaptadorEvaluaciones adaptadorEvaluaciones;
    private List<Evaluacion> evaluaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityEvaluacionesPacienteBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        evaluacionDao = new EvaluacionDao();
        evaluaciones = new ArrayList<>();

        configurarLista();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEvaluaciones();
    }

    private void configurarLista() {
        adaptadorEvaluaciones = new AdaptadorEvaluaciones(evaluaciones, evaluacion -> {
            Intent intent = new Intent(this, DetalleEvaluacionActivity.class);
            ponerDatosEvaluacion(intent, evaluacion);
            startActivity(intent);
        });
        vista.rvEvaluaciones.setLayoutManager(new LinearLayoutManager(this));
        vista.rvEvaluaciones.setAdapter(adaptadorEvaluaciones);
    }

    private void cargarEvaluaciones() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String idPaciente = autenticacionDao.obtenerUsuarioActual().getUid();

        evaluacionDao.obtenerEvaluacionesPorPaciente(idPaciente, new EvaluacionDao.EvaluacionesListener() {
            @Override
            public void evaluacionesEncontradas(List<Evaluacion> evaluacionesEncontradas) {
                evaluaciones.clear();
                evaluaciones.addAll(evaluacionesEncontradas);
                adaptadorEvaluaciones.notifyDataSetChanged();

                vista.txtSinEvaluaciones.setVisibility(evaluaciones.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(EvaluacionesPacienteActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ponerDatosEvaluacion(Intent intent, Evaluacion evaluacion) {
        intent.putExtra("idEvaluacion", evaluacion.getId());
        intent.putExtra("idCita", evaluacion.getIdCita());
        intent.putExtra("idPaciente", evaluacion.getIdPaciente());
        intent.putExtra("descripcion", evaluacion.getDescripcion());
        intent.putExtra("exploracion", evaluacion.getExploracion());
        intent.putExtra("tratamiento", evaluacion.getTratamiento());
        intent.putExtra("momento", evaluacion.getMomento());
    }
}
