package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorProfesionales;
import com.example.idoctor.dao.ProfesionalDao;
import com.example.idoctor.databinding.ActivityListaProfesionalesBinding;
import com.example.idoctor.models.Profesional;

import java.util.ArrayList;
import java.util.List;

public class ListaProfesionalesActivity extends AppCompatActivity {

    private ActivityListaProfesionalesBinding vista;
    private ProfesionalDao profesionalDao;
    private AdaptadorProfesionales adaptadorProfesionales;
    private List<Profesional> profesionales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityListaProfesionalesBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        profesionalDao = new ProfesionalDao();
        profesionales = new ArrayList<>();

        configurarLista();
        cargarProfesionales();

        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void configurarLista() {
        adaptadorProfesionales = new AdaptadorProfesionales(profesionales, profesional -> abrirDetalleProfesional(profesional));
        vista.rvProfesionales.setLayoutManager(new LinearLayoutManager(this));
        vista.rvProfesionales.setAdapter(adaptadorProfesionales);
    }

    private void cargarProfesionales() {
        profesionalDao.obtenerProfesionales(new ProfesionalDao.ProfesionalesListener() {
            @Override
            public void profesionalesEncontrados(List<Profesional> profesionalesEncontrados) {
                profesionales.clear();
                profesionales.addAll(profesionalesEncontrados);
                adaptadorProfesionales.notifyDataSetChanged();

                vista.txtSinProfesionales.setVisibility(profesionales.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(ListaProfesionalesActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDetalleProfesional(Profesional profesional) {
        Intent intent = new Intent(this, DetalleProfesionalActivity.class);
        intent.putExtra("idProfesional", profesional.getId());
        intent.putExtra("nombre", profesional.getNombre());
        intent.putExtra("apellidos", profesional.getApellidos());
        intent.putExtra("especialidad", profesional.getEspecialidad());
        intent.putExtra("descripcion", profesional.getDescripcion());
        intent.putExtra("telefono", profesional.getTelefono());
        intent.putExtra("correo", profesional.getCorreo());
        intent.putExtra("numeroColegiado", profesional.getNumeroColegiado());
        intent.putExtra("foto", profesional.getFoto());
        startActivity(intent);
    }
}
