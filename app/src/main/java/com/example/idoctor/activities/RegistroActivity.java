package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityRegistroBinding;
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

        vista.btnRegistrarse.setOnClickListener(view -> registrarUsuario());
        vista.txtIrInicioSesion.setOnClickListener(view -> finish());
    }

    private void registrarUsuario() {
        String correo = vista.edtCorreo.getText().toString().trim();
        String contrasena = vista.edtContrasena.getText().toString().trim();
        String repetirContrasena = vista.edtRepetirContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(repetirContrasena)) {
            mostrarMensaje("Rellena todos los campos");
            return;
        }

        if (!contrasena.equals(repetirContrasena)) {
            mostrarMensaje("Las contrasenas no coinciden");
            return;
        }

        if (contrasena.length() < 6) {
            mostrarMensaje("La contrasena debe tener al menos 6 caracteres");
            return;
        }

        autenticacionDao.registrarConCorreo(correo, contrasena).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful() && tarea.getResult().getUser() != null) {
                mostrarMensaje("Usuario creado correctamente");
                startActivity(new Intent(this, CompletarPerfilActivity.class));
                finish();
                return;
            }

            mostrarErrorRegistro(tarea.getException());
        });
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
