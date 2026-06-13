package com.example.idoctor.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.databinding.ActivityDetalleProfesionalBinding;

public class DetalleProfesionalActivity extends AppCompatActivity {

    private ActivityDetalleProfesionalBinding vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        mostrarDatosProfesional();
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarDatosProfesional() {
        String nombre = getIntent().getStringExtra("nombre");
        String apellidos = getIntent().getStringExtra("apellidos");
        String especialidad = getIntent().getStringExtra("especialidad");
        String descripcion = getIntent().getStringExtra("descripcion");
        String telefono = getIntent().getStringExtra("telefono");
        String correo = getIntent().getStringExtra("correo");

        vista.txtNombreProfesional.setText((nombre + " " + apellidos).trim());
        vista.txtEspecialidad.setText(valorTexto(especialidad));
        vista.txtDescripcion.setText(valorTexto(descripcion));
        vista.txtTelefono.setText("Telefono: " + valorTexto(telefono));
        vista.txtCorreo.setText("Correo: " + valorTexto(correo));
    }

    private String valorTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Sin datos";
        }

        return texto;
    }
}
