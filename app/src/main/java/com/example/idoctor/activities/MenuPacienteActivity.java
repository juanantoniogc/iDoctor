package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityMenuPacienteBinding;
import com.example.idoctor.models.Usuario;

public class MenuPacienteActivity extends AppCompatActivity {

    private ActivityMenuPacienteBinding vista;
    private AutenticacionDao autenticacionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityMenuPacienteBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        comprobarRolPaciente();

        vista.btnCerrarSesion.setOnClickListener(view -> {
            autenticacionDao.cerrarSesion();
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        });
    }

    private void comprobarRolPaciente() {
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
                    startActivity(new Intent(MenuPacienteActivity.this, CompletarPerfilActivity.class));
                    finish();
                } else if ("professional".equals(usuario.getRol())) {
                    startActivity(new Intent(MenuPacienteActivity.this, MenuProfesionalActivity.class));
                    finish();
                }
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MenuPacienteActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
