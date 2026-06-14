package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.PacienteDao;
import com.example.idoctor.databinding.ActivityMenuPacienteBinding;
import com.example.idoctor.models.Paciente;
import com.example.idoctor.models.Usuario;

public class MenuPacienteActivity extends AppCompatActivity {

    private ActivityMenuPacienteBinding vista;
    private AutenticacionDao autenticacionDao;
    private PacienteDao pacienteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityMenuPacienteBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        pacienteDao = new PacienteDao();
        comprobarRolPaciente();

        vista.btnVerProfesionales.setOnClickListener(view -> {
            startActivity(new Intent(this, ListaProfesionalesActivity.class));
        });

        vista.btnVerCitas.setOnClickListener(view -> {
            startActivity(new Intent(this, MisCitasActivity.class));
        });

        vista.btnVerEvaluaciones.setOnClickListener(view -> {
            startActivity(new Intent(this, EvaluacionesPacienteActivity.class));
        });

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
                } else {
                    mostrarDatosUsuario(usuario);
                    cargarDatosPaciente(idUsuario);
                }
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MenuPacienteActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatosUsuario(Usuario usuario) {
        String nombreCompleto = unirNombre(usuario.getNombre(), usuario.getApellidos());

        vista.txtInicialPaciente.setText(obtenerInicial(nombreCompleto));
        vista.txtNombrePaciente.setText(nombreCompleto);
        vista.txtCorreoPaciente.setText("Correo: " + obtenerTexto(usuario.getCorreo()));
        vista.txtRolPaciente.setText("Perfil de paciente");
    }

    private void cargarDatosPaciente(String idUsuario) {
        pacienteDao.obtenerPaciente(idUsuario, new PacienteDao.PacienteListener() {
            @Override
            public void pacienteEncontrado(Paciente paciente) {
                if (paciente == null) {
                    vista.txtTelefonoPaciente.setText("Telefono: --");
                    vista.txtDatoPaciente.setText("Tarjeta sanitaria: --");
                    return;
                }

                vista.txtTelefonoPaciente.setText("Telefono: " + obtenerTexto(paciente.getTelefono()));
                vista.txtDatoPaciente.setText("Tarjeta sanitaria: " + obtenerTexto(paciente.getNumeroTarjetaSanitaria()));
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MenuPacienteActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String unirNombre(String nombre, String apellidos) {
        String nombreSeguro = obtenerTexto(nombre);
        String apellidosSeguro = obtenerTexto(apellidos);

        if ("--".equals(nombreSeguro)) {
            return apellidosSeguro;
        }

        if ("--".equals(apellidosSeguro)) {
            return nombreSeguro;
        }

        return nombreSeguro + " " + apellidosSeguro;
    }

    private String obtenerInicial(String texto) {
        if (TextUtils.isEmpty(texto) || "--".equals(texto)) {
            return "P";
        }

        return texto.substring(0, 1).toUpperCase();
    }

    private String obtenerTexto(String texto) {
        return TextUtils.isEmpty(texto) ? "--" : texto;
    }
}
