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
import com.example.idoctor.validations.Validaciones;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

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
        configurarLimpiezaErrores();
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

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.edtNombre);
        Validaciones.limpiarErrorAlCambiar(vista.edtApellidos);
        Validaciones.limpiarErrorAlCambiar(vista.edtTelefono);
        Validaciones.limpiarErrorAlCambiar(vista.edtDni);
        Validaciones.limpiarErrorAlCambiar(vista.edtNumeroTarjetaSanitaria);
        Validaciones.limpiarErrorAlCambiar(vista.edtNumeroColegiado);
        Validaciones.limpiarErrorAlCambiar(vista.edtEspecialidad);
        Validaciones.limpiarErrorAlCambiar(vista.edtDescripcion);
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

        limpiarErrores();

        if (!datosComunesValidos(rol, nombre, apellidos, telefono)) {
            return;
        }

        if ("patient".equals(rol) && !datosPacienteValidos()) {
            return;
        }

        if ("professional".equals(rol) && !datosProfesionalValidos()) {
            return;
        }

        String foto = generarImagenPerfil();
        Usuario usuario = new Usuario(id, correo, nombre, apellidos, foto, rol);
        vista.btnGuardarPerfil.setEnabled(false);
        usuarioDao.guardarUsuario(usuario).addOnCompleteListener(tarea -> {
            if (!tarea.isSuccessful()) {
                vista.btnGuardarPerfil.setEnabled(true);
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

    private boolean datosComunesValidos(String rol, String nombre, String apellidos, String telefono) {
        boolean valido = true;

        if (TextUtils.isEmpty(rol)) {
            mostrarMensaje("Elige paciente o profesional");
            valido = false;
        }

        if (TextUtils.isEmpty(nombre)) {
            vista.edtNombre.setError("El nombre es obligatorio");
            valido = false;
        } else if (!Validaciones.textoEntre(nombre, 2, 50)) {
            vista.edtNombre.setError("El nombre debe tener entre 2 y 50 caracteres");
            valido = false;
        } else if (!Validaciones.soloLetrasEspaciosGuiones(nombre)) {
            vista.edtNombre.setError("El nombre solo puede contener letras, espacios y guiones");
            valido = false;
        }

        if (TextUtils.isEmpty(apellidos)) {
            vista.edtApellidos.setError("Los apellidos son obligatorios");
            valido = false;
        } else if (!Validaciones.textoEntre(apellidos, 2, 50)) {
            vista.edtApellidos.setError("Los apellidos deben tener entre 2 y 50 caracteres");
            valido = false;
        } else if (!Validaciones.soloLetrasEspaciosGuiones(apellidos)) {
            vista.edtApellidos.setError("Los apellidos solo pueden contener letras, espacios y guiones");
            valido = false;
        }

        if (TextUtils.isEmpty(telefono)) {
            vista.edtTelefono.setError("El telefono es obligatorio");
            valido = false;
        } else if (!Validaciones.telefonoValido(telefono)) {
            vista.edtTelefono.setError("El telefono no tiene un formato valido");
            valido = false;
        }

        return valido;
    }

    private boolean datosPacienteValidos() {
        String dni = vista.edtDni.getText().toString().trim();
        String numeroTarjetaSanitaria = vista.edtNumeroTarjetaSanitaria.getText().toString().trim();
        boolean valido = true;

        if (TextUtils.isEmpty(dni)) {
            vista.edtDni.setError("El DNI es obligatorio");
            valido = false;
        } else if (!Validaciones.dniValido(dni)) {
            vista.edtDni.setError("El DNI no es valido");
            valido = false;
        }

        if (TextUtils.isEmpty(numeroTarjetaSanitaria)) {
            vista.edtNumeroTarjetaSanitaria.setError("La tarjeta sanitaria es obligatoria");
            valido = false;
        } else if (!Validaciones.textoEntre(numeroTarjetaSanitaria, 6, 30)) {
            vista.edtNumeroTarjetaSanitaria.setError("La tarjeta sanitaria debe tener entre 6 y 30 caracteres");
            valido = false;
        }

        return valido;
    }

    private boolean datosProfesionalValidos() {
        String numeroColegiado = vista.edtNumeroColegiado.getText().toString().trim();
        String especialidad = vista.edtEspecialidad.getText().toString().trim();
        String descripcion = vista.edtDescripcion.getText().toString().trim();
        boolean valido = true;

        if (TextUtils.isEmpty(numeroColegiado)) {
            vista.edtNumeroColegiado.setError("El numero de colegiado es obligatorio");
            valido = false;
        } else if (!Validaciones.numeroColegiadoValido(numeroColegiado)) {
            vista.edtNumeroColegiado.setError("Usa entre 4 y 30 caracteres: letras, numeros y guiones");
            valido = false;
        }

        if (TextUtils.isEmpty(especialidad)) {
            vista.edtEspecialidad.setError("La especialidad es obligatoria");
            valido = false;
        } else if (!especialidadValida(especialidad)) {
            vista.edtEspecialidad.setError("Elige una especialidad valida");
            valido = false;
        }

        if (TextUtils.isEmpty(descripcion)) {
            vista.edtDescripcion.setError("La descripcion es obligatoria");
            valido = false;
        } else if (!Validaciones.textoEntre(descripcion, 20, 500)) {
            vista.edtDescripcion.setError("La descripcion debe tener entre 20 y 500 caracteres");
            valido = false;
        }

        return valido;
    }

    private boolean especialidadValida(String especialidad) {
        return ESPECIALIDAD_GENERAL.equals(especialidad)
                || ESPECIALIDAD_FISIOTERAPIA.equals(especialidad)
                || ESPECIALIDAD_ODONTOLOGIA.equals(especialidad);
    }

    private String generarImagenPerfil() {
        return "https://randomuser.me/api/portraits/men/" + new Random().nextInt(100) + ".jpg";
    }

    private void limpiarErrores() {
        vista.edtNombre.setError(null);
        vista.edtApellidos.setError(null);
        vista.edtTelefono.setError(null);
        vista.edtDni.setError(null);
        vista.edtNumeroTarjetaSanitaria.setError(null);
        vista.edtNumeroColegiado.setError(null);
        vista.edtEspecialidad.setError(null);
        vista.edtDescripcion.setError(null);
    }

    private void guardarPaciente(String id, String correo, String nombre, String apellidos,
                                 String telefono, String foto) {
        String dni = vista.edtDni.getText().toString().trim();
        String numeroTarjetaSanitaria = vista.edtNumeroTarjetaSanitaria.getText().toString().trim();

        Paciente paciente = new Paciente(id, nombre, apellidos, correo, telefono, foto, dni, numeroTarjetaSanitaria);

        pacienteDao.guardarPaciente(paciente).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                startActivity(new Intent(this, MenuPacienteActivity.class));
                finish();
            } else {
                vista.btnGuardarPerfil.setEnabled(true);
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
                vista.btnGuardarPerfil.setEnabled(true);
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
