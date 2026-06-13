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
import com.example.idoctor.databinding.ActivityCitasProfesionalBinding;
import com.example.idoctor.models.Cita;

import java.util.ArrayList;
import java.util.List;

public class CitasProfesionalActivity extends AppCompatActivity {

    private ActivityCitasProfesionalBinding vista;
    private AutenticacionDao autenticacionDao;
    private CitaDao citaDao;
    private AdaptadorCitas adaptadorCitas;
    private List<Cita> citas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityCitasProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        citaDao = new CitaDao();
        citas = new ArrayList<>();

        configurarLista();

        vista.btnGenerarCitas.setOnClickListener(view -> {
            startActivity(new Intent(this, GenerarCitasActivity.class));
        });

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCitas();
    }

    private void configurarLista() {
        adaptadorCitas = new AdaptadorCitas(citas, cita -> {
            Intent intent = new Intent(this, DetalleCitaProfesionalActivity.class);
            ponerDatosCita(intent, cita);
            startActivity(intent);
        });
        vista.rvCitasProfesional.setLayoutManager(new LinearLayoutManager(this));
        vista.rvCitasProfesional.setAdapter(adaptadorCitas);
    }

    private void cargarCitas() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();

        citaDao.obtenerCitasPorProfesional(idProfesional, new CitaDao.CitasListener() {
            @Override
            public void citasEncontradas(List<Cita> citasEncontradas) {
                citas.clear();
                citas.addAll(citasEncontradas);
                adaptadorCitas.notifyDataSetChanged();

                vista.txtSinCitas.setVisibility(citas.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(CitasProfesionalActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
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
