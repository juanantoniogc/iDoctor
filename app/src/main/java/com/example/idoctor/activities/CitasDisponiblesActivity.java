package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorCitas;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.databinding.ActivityCitasDisponiblesBinding;
import com.example.idoctor.models.Cita;

import java.util.ArrayList;
import java.util.List;

public class CitasDisponiblesActivity extends AppCompatActivity {

    private ActivityCitasDisponiblesBinding vista;
    private CitaDao citaDao;
    private AdaptadorCitas adaptadorCitas;
    private List<Cita> citas;
    private String idConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityCitasDisponiblesBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        citaDao = new CitaDao();
        citas = new ArrayList<>();
        idConsulta = getIntent().getStringExtra("idConsulta");

        if (idConsulta == null || idConsulta.trim().isEmpty()) {
            Toast.makeText(this, "No se encontro la consulta", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        configurarLista();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCitasDisponibles();
    }

    private void configurarLista() {
        adaptadorCitas = new AdaptadorCitas(citas, cita -> {
            Intent intent = new Intent(this, DetalleCitaActivity.class);
            ponerDatosCita(intent, cita);
            intent.putExtra("modo", "reservar");
            startActivity(intent);
        });
        vista.rvCitasDisponibles.setLayoutManager(new LinearLayoutManager(this));
        vista.rvCitasDisponibles.setAdapter(adaptadorCitas);
    }

    private void cargarCitasDisponibles() {
        citaDao.obtenerCitasDisponiblesPorConsulta(idConsulta, new CitaDao.CitasListener() {
            @Override
            public void citasEncontradas(List<Cita> citasEncontradas) {
                citas.clear();
                citas.addAll(citasEncontradas);
                adaptadorCitas.notifyDataSetChanged();

                vista.txtSinCitas.setVisibility(citas.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(CitasDisponiblesActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
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
