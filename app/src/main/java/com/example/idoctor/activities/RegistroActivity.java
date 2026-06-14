package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityRegistroBinding;
import com.example.idoctor.validations.Validaciones;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding vista;
    private AutenticacionDao autenticacionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        configurarLimpiezaErrores();

        vista.btnRegistrarse.setOnClickListener(view -> registrarUsuario());
        vista.txtIrInicioSesion.setOnClickListener(view -> finish());
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.edtCorreo);
        Validaciones.limpiarErrorAlCambiar(vista.edtContrasena);
        Validaciones.limpiarErrorAlCambiar(vista.edtRepetirContrasena);
    }

    private void registrarUsuario() {
        String correo = vista.edtCorreo.getText().toString().trim();
        String contrasena = vista.edtContrasena.getText().toString();
        String repetirContrasena = vista.edtRepetirContrasena.getText().toString();

        limpiarErrores();

        if (!formularioValido(correo, contrasena, repetirContrasena)) {
            return;
        }

        vista.btnRegistrarse.setEnabled(false);
        autenticacionDao.registrarConCorreo(correo, contrasena).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful() && tarea.getResult().getUser() != null) {
                mostrarMensaje("Usuario creado correctamente");
                startActivity(new Intent(this, CompletarPerfilActivity.class));
                finish();
                return;
            }

            vista.btnRegistrarse.setEnabled(true);
            mostrarErrorRegistro(tarea.getException());
        });
    }

    private boolean formularioValido(String correo, String contrasena, String repetirContrasena) {
        boolean valido = true;

        if (TextUtils.isEmpty(correo)) {
            vista.edtCorreo.setError("El email es obligatorio");
            valido = false;
        } else if (correo.contains(" ")) {
            vista.edtCorreo.setError("El email no puede contener espacios");
            valido = false;
        } else if (correo.length() > 100) {
            vista.edtCorreo.setError("El email no puede superar 100 caracteres");
            valido = false;
        } else if (!Validaciones.emailValido(correo)) {
            vista.edtCorreo.setError("Introduce un email valido");
            valido = false;
        }

        if (TextUtils.isEmpty(contrasena)) {
            vista.edtContrasena.setError("La contrasena es obligatoria");
            valido = false;
        } else if (!Validaciones.contrasenaFuerte(contrasena)) {
            vista.edtContrasena.setError("Minimo 8 caracteres, una mayuscula, una minuscula y un numero");
            valido = false;
        }

        if (TextUtils.isEmpty(repetirContrasena)) {
            vista.edtRepetirContrasena.setError("Repite la contrasena");
            valido = false;
        } else if (!contrasena.equals(repetirContrasena)) {
            vista.edtRepetirContrasena.setError("Las contrasenas no coinciden");
            valido = false;
        }

        return valido;
    }

    private void limpiarErrores() {
        vista.edtCorreo.setError(null);
        vista.edtContrasena.setError(null);
        vista.edtRepetirContrasena.setError(null);
    }

    private void mostrarErrorRegistro(Exception excepcion) {
        if (excepcion instanceof FirebaseAuthUserCollisionException) {
            mostrarMensaje("Ese correo ya esta registrado");
        } else if (excepcion instanceof FirebaseAuthInvalidCredentialsException) {
            mostrarMensaje("El formato del correo no es valido");
        } else if (excepcion instanceof FirebaseNetworkException) {
            mostrarMensaje("Error de red. Revisa tu conexion");
        } else {
            mostrarMensaje("No se pudo registrar la cuenta");
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
