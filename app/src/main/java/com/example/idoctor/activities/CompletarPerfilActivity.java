package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.PacienteDao;
import com.example.idoctor.dao.ProfesionalDao;
import com.example.idoctor.dao.UsuarioDao;
import com.example.idoctor.databinding.ActivityCompletarPerfilBinding;
import com.example.idoctor.models.Paciente;
import com.example.idoctor.models.Profesional;
import com.example.idoctor.models.Usuario;
import com.google.firebase.auth.FirebaseUser;

public class CompletarPerfilActivity extends AppCompatActivity {

    private static final String ESPECIALIDAD_GENERAL = "General";
    private static final String ESPECIALIDAD_FISIOTERAPIA = "Fisioterapia";
    private static final String ESPECIALIDAD_ODONTOLOGIA = "Odontologia";

    private ActivityCompletarPerfilBinding vista;
    private AutenticacionDao autenticacionDao;
    private UsuarioDao usuarioDao;
    private PacienteDao pacienteDao;
    private ProfesionalDao profesionalDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityCompletarPerfilBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        usuarioDao = new UsuarioDao();
        pacienteDao = new PacienteDao();
        profesionalDao = new ProfesionalDao();

        configurarEspecialidades();
        mostrarCamposRol("");

        vista.rgRol.setOnCheckedChangeListener((group, idSeleccionado) -> {
            if (idSeleccionado == vista.rbPaciente.getId()) {
                mostrarCamposRol("patient");
            } else if (idSeleccionado == vista.rbProfesional.getId()) {
                mostrarCamposRol("professional");
            }
        });

        vista.btnCerrarSesion.setOnClickListener(view -> {
            autenticacionDao.cerrarSesion();
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
        });

        vista.btnGuardarPerfil.setOnClickListener(view -> guardarPerfil());
    }

    private void mostrarCamposRol(String rol) {
        vista.layoutPaciente.setVisibility("patient".equals(rol) ? View.VISIBLE : View.GONE);
        vista.layoutProfesional.setVisibility("professional".equals(rol) ? View.VISIBLE : View.GONE);
    }

    private void configurarEspecialidades() {
        String[] especialidades = {
                ESPECIALIDAD_GENERAL,
                ESPECIALIDAD_FISIOTERAPIA,
                ESPECIALIDAD_ODONTOLOGIA
        };

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                especialidades
        );

        vista.edtEspecialidad.setAdapter(adaptador);
    }

    private void guardarPerfil() {
        FirebaseUser usuarioFirebase = autenticacionDao.obtenerUsuarioActual();
        if (usuarioFirebase == null) {
            mostrarMensaje("No hay usuario iniciado");
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String rol = obtenerRolSeleccionado();
        String id = usuarioFirebase.getUid();
        String correo = usuarioFirebase.getEmail() == null ? "" : usuarioFirebase.getEmail();
        String nombre = vista.edtNombre.getText().toString().trim();
        String apellidos = vista.edtApellidos.getText().toString().trim();
        String telefono = vista.edtTelefono.getText().toString().trim();
        String foto = vista.edtFoto.getText().toString().trim();

        if (TextUtils.isEmpty(rol)) {
            mostrarMensaje("Elige paciente o profesional");
            return;
        }

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellidos) || TextUtils.isEmpty(telefono)) {
            mostrarMensaje("Rellena nombre, apellidos y telefono");
            return;
        }

        if ("patient".equals(rol) && !datosPacienteValidos()) {
            return;
        }

        if ("professional".equals(rol) && !datosProfesionalValidos()) {
            return;
        }

        Usuario usuario = new Usuario(id, correo, nombre, apellidos, foto, rol);
        usuarioDao.guardarUsuario(usuario).addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                mostrarMensaje(obtenerMensajeError("No se pudieron guardar los datos basicos", tarea.getException()));
                return;
            }

            if ("patient".equals(rol)) {
                guardarPaciente(id, correo, nombre, apellidos, telefono, foto);
            } else {
                guardarProfesional(id, correo, nombre, apellidos, telefono, foto);
            }
        });
    }

    private String obtenerRolSeleccionado() {
        int idSeleccionado = vista.rgRol.getCheckedRadioButtonId();

        if (idSeleccionado == vista.rbPaciente.getId()) {
            return "patient";
        } else if (idSeleccionado == vista.rbProfesional.getId()) {
            return "professional";
        }

        return "";
    }

    private boolean datosPacienteValidos() {
        String dni = vista.edtDni.getText().toString().trim();
        String numeroTarjetaSanitaria = vista.edtNumeroTarjetaSanitaria.getText().toString().trim();

        if (TextUtils.isEmpty(dni) || TextUtils.isEmpty(numeroTarjetaSanitaria)) {
            mostrarMensaje("Rellena DNI y numero de tarjeta sanitaria");
            return false;
        }

        return true;
    }

    private boolean datosProfesionalValidos() {
        String numeroColegiado = vista.edtNumeroColegiado.getText().toString().trim();
        String especialidad = vista.edtEspecialidad.getText().toString().trim();
        String descripcion = vista.edtDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(numeroColegiado) || TextUtils.isEmpty(especialidad) || TextUtils.isEmpty(descripcion)) {
            mostrarMensaje("Rellena numero de colegiado, especialidad y descripcion");
            return false;
        }

        if (!especialidadValida(especialidad)) {
            mostrarMensaje("Elige una especialidad valida");
            return false;
        }

        return true;
    }

    private boolean especialidadValida(String especialidad) {
        return ESPECIALIDAD_GENERAL.equals(especialidad)
                || ESPECIALIDAD_FISIOTERAPIA.equals(especialidad)
                || ESPECIALIDAD_ODONTOLOGIA.equals(especialidad);
    }

    private void guardarPaciente(String id, String correo, String nombre, String apellidos,
                                 String telefono, String foto) {
        String dni = vista.edtDni.getText().toString().trim();
        String numeroTarjetaSanitaria = vista.edtNumeroTarjetaSanitaria.getText().toString().trim();

        Paciente paciente = new Paciente(id, nombre, apellidos, correo, telefono, foto, dni, numeroTarjetaSanitaria);
        paciente.setTieneSeguroMedico(vista.cbTieneSeguroMedico.isChecked());

        pacienteDao.guardarPaciente(paciente).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                startActivity(new Intent(this, MenuPacienteActivity.class));
                finish();
            } else {
                mostrarMensaje(obtenerMensajeError("No se pudo guardar el paciente", tarea.getException()));
            }
        });
    }

    private void guardarProfesional(String id, String correo, String nombre, String apellidos,
                                    String telefono, String foto) {
        String numeroColegiado = vista.edtNumeroColegiado.getText().toString().trim();
        String especialidad = vista.edtEspecialidad.getText().toString().trim();
        String descripcion = vista.edtDescripcion.getText().toString().trim();

        Profesional profesional = new Profesional(id, nombre, apellidos, correo, telefono, foto,
                numeroColegiado, especialidad, descripcion);

        profesionalDao.guardarProfesional(profesional).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                startActivity(new Intent(this, MenuProfesionalActivity.class));
                finish();
            } else {
                mostrarMensaje(obtenerMensajeError("No se pudo guardar el profesional", tarea.getException()));
            }
        });
    }

    private String obtenerMensajeError(String mensaje, Exception excepcion) {
        if (excepcion == null || excepcion.getMessage() == null) {
            return mensaje;
        }

        return mensaje + ": " + excepcion.getMessage();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
