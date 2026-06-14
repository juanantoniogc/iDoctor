package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.idoctor.adapters.AdaptadorValoraciones;
import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.ValoracionDao;
import com.example.idoctor.databinding.ActivityListaValoracionesProfesionalBinding;
import com.example.idoctor.models.Valoracion;

import java.util.ArrayList;
import java.util.List;

public class ListaValoracionesProfesionalActivity extends AppCompatActivity {

    private ActivityListaValoracionesProfesionalBinding vista;
    private AutenticacionDao autenticacionDao;
    private ValoracionDao valoracionDao;
    private AdaptadorValoraciones adaptadorValoraciones;
    private List<Valoracion> valoraciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityListaValoracionesProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        valoracionDao = new ValoracionDao();
        valoraciones = new ArrayList<>();

        configurarLista();
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarValoraciones();
    }

    private void configurarLista() {
        adaptadorValoraciones = new AdaptadorValoraciones(valoraciones, valoracion -> {
            Intent intent = new Intent(this, DetalleValoracionActivity.class);
            intent.putExtra("idValoracion", valoracion.getId());
            intent.putExtra("idCita", valoracion.getIdCita());
            intent.putExtra("idPaciente", valoracion.getIdPaciente());
            intent.putExtra("estrellas", valoracion.getEstrellas());
            intent.putExtra("comentario", valoracion.getComentario());
            intent.putExtra("momento", valoracion.getMomento());
            startActivity(intent);
        });

        vista.rvValoraciones.setLayoutManager(new LinearLayoutManager(this));
        vista.rvValoraciones.setAdapter(adaptadorValoraciones);
    }

    private void cargarValoraciones() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();
        valoracionDao.obtenerValoracionesPorProfesional(idProfesional, new ValoracionDao.ValoracionesListener() {
            @Override
            public void valoracionesEncontradas(List<Valoracion> valoracionesEncontradas) {
                valoraciones.clear();
                valoraciones.addAll(valoracionesEncontradas);
                adaptadorValoraciones.notifyDataSetChanged();
                vista.txtSinValoraciones.setVisibility(valoraciones.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(ListaValoracionesProfesionalActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
