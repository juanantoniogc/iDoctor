package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.ProfesionalDao;
import com.example.idoctor.databinding.ActivityMenuProfesionalBinding;
import com.example.idoctor.models.Profesional;
import com.example.idoctor.models.Usuario;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class MenuProfesionalActivity extends AppCompatActivity {

    private ActivityMenuProfesionalBinding vista;
    private AutenticacionDao autenticacionDao;
    private ProfesionalDao profesionalDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityMenuProfesionalBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        profesionalDao = new ProfesionalDao();
        comprobarRolProfesional();

        vista.btnMisConsultas.setOnClickListener(view -> {
            startActivity(new Intent(this, ListaConsultasProfesionalActivity.class));
        });

        vista.btnMisHorarios.setOnClickListener(view -> {
            startActivity(new Intent(this, ListaConsultasProfesionalActivity.class));
        });

        vista.btnMisCitas.setOnClickListener(view -> {
            startActivity(new Intent(this, CitasProfesionalActivity.class));
        });

        vista.btnMisValoraciones.setOnClickListener(view -> {
            startActivity(new Intent(this, ListaValoracionesProfesionalActivity.class));
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
                } else {
                    mostrarDatosUsuario(usuario);
                    cargarDatosProfesional(idUsuario);
                }
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MenuProfesionalActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatosUsuario(Usuario usuario) {
        String nombreCompleto = unirNombre(usuario.getNombre(), usuario.getApellidos());

        cargarFoto(usuario.getFoto());
        vista.txtNombreProfesional.setText(nombreCompleto);
        vista.txtCorreoProfesional.setText("Correo: " + obtenerTexto(usuario.getCorreo()));
        vista.txtRolProfesional.setText("Perfil profesional");
    }

    private void cargarDatosProfesional(String idUsuario) {
        profesionalDao.obtenerProfesional(idUsuario, new ProfesionalDao.ProfesionalListener() {
            @Override
            public void profesionalEncontrado(Profesional profesional) {
                if (profesional == null) {
                    vista.txtTelefonoProfesional.setText("Telefono: --");
                    vista.txtDatoProfesional.setText("Colegiado: --");
                    vista.txtEspecialidadProfesional.setText("Especialidad: --");
                    vista.ratingValoracionesProfesional.setRating(0);
                    vista.txtValoracionesProfesional.setText("--");
                    vista.txtDescripcionProfesional.setText("Descripcion: --");
                    return;
                }

                cargarFoto(profesional.getFoto());
                vista.txtTelefonoProfesional.setText("Telefono: " + obtenerTexto(profesional.getTelefono()));
                vista.txtDatoProfesional.setText("Colegiado: " + obtenerTexto(profesional.getNumeroColegiado()));
                vista.txtEspecialidadProfesional.setText("Especialidad: " + obtenerTexto(profesional.getEspecialidad()));
                vista.ratingValoracionesProfesional.setRating((float) profesional.getMediaEstrellas());
                vista.txtValoracionesProfesional.setText(String.format(Locale.getDefault(),
                        "%.1f de 5 estrellas (%d valoraciones)",
                        profesional.getMediaEstrellas(),
                        profesional.getNumeroValoraciones()));
                vista.txtDescripcionProfesional.setText("Descripcion: " + obtenerTexto(profesional.getDescripcion()));
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(MenuProfesionalActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarFoto(String foto) {
        if (TextUtils.isEmpty(foto)) {
            vista.imgFotoProfesional.setImageResource(com.example.idoctor.R.drawable.bg_profile_initial);
            return;
        }

        Picasso.get()
                .load(foto)
                .placeholder(com.example.idoctor.R.drawable.bg_profile_initial)
                .error(com.example.idoctor.R.drawable.bg_profile_initial)
                .into(vista.imgFotoProfesional);
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

    private String obtenerTexto(String texto) {
        return TextUtils.isEmpty(texto) ? "--" : texto;
    }
}
