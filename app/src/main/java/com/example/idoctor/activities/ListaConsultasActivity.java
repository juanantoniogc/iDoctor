package com.example.idoctor.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorConsultas;
import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.databinding.ActivityListaConsultasBinding;
import com.example.idoctor.models.Consulta;

import java.util.ArrayList;
import java.util.List;

public class ListaConsultasActivity extends AppCompatActivity {

    private ActivityListaConsultasBinding vista;
    private ConsultaDao consultaDao;
    private AdaptadorConsultas adaptadorConsultas;
    private List<Consulta> consultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityListaConsultasBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        consultaDao = new ConsultaDao();
        consultas = new ArrayList<>();

        configurarLista();
        cargarConsultas();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void configurarLista() {
        adaptadorConsultas = new AdaptadorConsultas(consultas);
        vista.rvConsultas.setLayoutManager(new LinearLayoutManager(this));
        vista.rvConsultas.setAdapter(adaptadorConsultas);
    }

    private void cargarConsultas() {
        String idProfesional = getIntent().getStringExtra("idProfesional");

        if (idProfesional == null || idProfesional.trim().isEmpty()) {
            Toast.makeText(this, "No se encontro el profesional", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        consultaDao.obtenerConsultasPorProfesional(idProfesional, new ConsultaDao.ConsultasListener() {
            @Override
            public void consultasEncontradas(List<Consulta> consultasEncontradas) {
                consultas.clear();
                consultas.addAll(consultasEncontradas);
                adaptadorConsultas.notifyDataSetChanged();

                vista.txtSinConsultas.setVisibility(consultas.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(ListaConsultasActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
