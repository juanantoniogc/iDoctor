package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityMenuProfesionalBinding;
import com.example.idoctor.models.Usuario;

public class MenuProfesionalActivity extends AppCompatActivity {

    private ActivityMenuProfesionalBinding vista;
    private AutenticacionDao autenticacionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityMenuProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        comprobarRolProfesional();

        vista.btnMisConsultas.setOnClickListener(view -> {
            startActivity(new Intent(this, ListaConsultasProfesionalActivity.class));
        });

        vista.btnMisHorarios.setOnClickListener(view -> {
            startActivity(new Intent(this, ListaConsultasProfesionalActivity.class));
        });

        vista.btnMisCitas.setOnClickListener(view -> {
            Toast.makeText(this, "Mis citas se hara en una fase posterior", Toast.LENGTH_SHORT).show();
        });

        vista.btnCerrarSesion.setOnClickListener(view -> {
            autenticacionDao.cerrarSesion();
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        });
    }

    private void comprobarRolProfesional() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String idUsuario = autenticacionDao.obtenerUsuarioActual().getUid();
        autenticacionDao.obtenerDatosUsuario(idUsuario, new AutenticacionDao.DatosUsuarioListener() {
            @Override
            public void usuarioEncontrado(Usuario usuario) {
                if (usuario == null || TextUtils.isEmpty(usuario.getRol())) {
                    startActivity(new Intent(MenuProfesionalActivity.this, CompletarPerfilActivity.class));
                    finish();
                } else if ("patient".equals(usuario.getRol())) {
                    startActivity(new Intent(MenuProfesionalActivity.this, MenuPacienteActivity.class));
                    finish();
                }
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MenuProfesionalActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
