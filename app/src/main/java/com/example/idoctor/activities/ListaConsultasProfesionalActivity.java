package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorConsultasProfesional;
import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.databinding.ActivityListaConsultasProfesionalBinding;
import com.example.idoctor.models.Consulta;

import java.util.ArrayList;
import java.util.List;

public class ListaConsultasProfesionalActivity extends AppCompatActivity {

    private ActivityListaConsultasProfesionalBinding vista;
    private AutenticacionDao autenticacionDao;
    private ConsultaDao consultaDao;
    private AdaptadorConsultasProfesional adaptadorConsultas;
    private List<Consulta> consultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityListaConsultasProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        consultaDao = new ConsultaDao();
        consultas = new ArrayList<>();

        configurarLista();
        configurarBotones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarConsultas();
    }

    private void configurarLista() {
        adaptadorConsultas = new AdaptadorConsultasProfesional(consultas, new AdaptadorConsultasProfesional.OnConsultaClickListener() {
            @Override
            public void editarConsulta(Consulta consulta) {
                abrirFormulario(consulta);
            }

            @Override
            public void verHorarios(Consulta consulta) {
                abrirHorarios(consulta);
            }

            @Override
            public void eliminarConsulta(Consulta consulta) {
                confirmarEliminarConsulta(consulta);
            }
        });

        vista.rvConsultas.setLayoutManager(new LinearLayoutManager(this));
        vista.rvConsultas.setAdapter(adaptadorConsultas);
    }

    private void configurarBotones() {
        vista.btnNuevaConsulta.setOnClickListener(view -> abrirFormulario(null));
        vista.btnVolver.setOnClickListener(view -> finish());
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
                adaptadorConsultas.notifyDataSetChanged();
                vista.txtSinConsultas.setVisibility(consultas.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(ListaConsultasProfesionalActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirFormulario(Consulta consulta) {
        Intent intent = new Intent(this, FormularioConsultaActivity.class);

        if (consulta != null) {
            intent.putExtra("idConsulta", consulta.getId());
            intent.putExtra("titulo", consulta.getTitulo());
            intent.putExtra("correo", consulta.getCorreo());
            intent.putExtra("telefono", consulta.getTelefono());
            intent.putExtra("telefonoAuxiliar", consulta.getTelefonoAuxiliar());
            intent.putExtra("url", consulta.getUrl());
            intent.putExtra("observaciones", consulta.getObservaciones());

            if (consulta.getDireccion() != null) {
                intent.putExtra("street", consulta.getDireccion().getStreet());
                intent.putExtra("number", consulta.getDireccion().getNumber());
                intent.putExtra("floor", consulta.getDireccion().getFloor());
                intent.putExtra("portal", consulta.getDireccion().getPortal());
                intent.putExtra("city", consulta.getDireccion().getCity());
                intent.putExtra("province", consulta.getDireccion().getProvince());
                intent.putExtra("postalCode", consulta.getDireccion().getPostalCode());
                intent.putExtra("country", consulta.getDireccion().getCountry());
            }
        }

        startActivity(intent);
    }

    private void abrirHorarios(Consulta consulta) {
        Intent intent = new Intent(this, ListaHorariosActivity.class);
        intent.putExtra("idConsulta", consulta.getId());
        startActivity(intent);
    }

    private void confirmarEliminarConsulta(Consulta consulta) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar consulta")
                .setMessage("Quieres eliminar esta consulta?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarConsulta(consulta))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarConsulta(Consulta consulta) {
        consultaDao.eliminarConsulta(consulta.getId()).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Consulta eliminada", Toast.LENGTH_SHORT).show();
                cargarConsultas();
            } else {
                Toast.makeText(this, "No se pudo eliminar la consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
