package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.databinding.ActivityDetalleProfesionalBinding;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetalleProfesionalActivity extends AppCompatActivity {

    private ActivityDetalleProfesionalBinding vista;
    private String idProfesional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        mostrarDatosProfesional();
        vista.btnVerConsultas.setOnClickListener(view -> abrirConsultas());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarDatosProfesional() {
        idProfesional = getIntent().getStringExtra("idProfesional");
        String nombre = getIntent().getStringExtra("nombre");
        String apellidos = getIntent().getStringExtra("apellidos");
        String especialidad = getIntent().getStringExtra("especialidad");
        String descripcion = getIntent().getStringExtra("descripcion");
        String numeroColegiado = getIntent().getStringExtra("numeroColegiado");
        String foto = getIntent().getStringExtra("foto");
        double mediaEstrellas = getIntent().getDoubleExtra("mediaEstrellas", 0);
        int numeroValoraciones = getIntent().getIntExtra("numeroValoraciones", 0);

        vista.txtNombreProfesional.setText(valorTexto(nombre));
        vista.txtApellidos.setText(valorTexto(apellidos));
        vista.txtEspecialidad.setText(valorTexto(especialidad));
        vista.txtDescripcion.setText(valorTexto(descripcion));
        vista.txtNumeroColegiado.setText("Numero de colegiado: " + valorTexto(numeroColegiado));
        vista.ratingValoracionesProfesional.setRating((float) mediaEstrellas);
        vista.txtValoracionesProfesional.setText(String.format(Locale.getDefault(),
                "%.1f de 5 estrellas (%d valoraciones)",
                mediaEstrellas,
                numeroValoraciones));

        if (TextUtils.isEmpty(foto)) {
            vista.imgFotoProfesional.setVisibility(View.GONE);
        } else {
            vista.imgFotoProfesional.setVisibility(View.VISIBLE);
            Picasso.get().load(foto).into(vista.imgFotoProfesional);
        }
    }

    private void abrirConsultas() {
        Intent intent = new Intent(this, ListaConsultasActivity.class);
        intent.putExtra("idProfesional", idProfesional);
        startActivity(intent);
    }

    private String valorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Sin datos";
        }

        return texto;
    }
}
