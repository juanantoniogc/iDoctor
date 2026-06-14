package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityInicioSesionBinding;
import com.example.idoctor.models.Usuario;
import com.example.idoctor.validations.Validaciones;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

public class InicioSesionActivity extends AppCompatActivity {

    private ActivityInicioSesionBinding vista;
    private AutenticacionDao autenticacionDao;
    private GoogleSignInClient clienteGoogle;

    private final ActivityResultLauncher<Intent> lanzadorGoogle =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), resultado -> {
                if (resultado.getData() == null) {
                    mostrarMensaje("No se recibieron datos de Google");
                    return;
                }

                try {
                    GoogleSignInAccount cuentaGoogle = GoogleSignIn.getSignedInAccountFromIntent(resultado.getData())
                            .getResult(ApiException.class);
                    iniciarSesionConCuentaGoogle(cuentaGoogle);
                } catch (ApiException e) {
                    if (e.getStatusCode() == 12501) {
                        return;
                    }
                    mostrarMensaje("Error con Google. Codigo: " + e.getStatusCode());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityInicioSesionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        configurarGoogle();
        configurarLimpiezaErrores();

        vista.btnIniciarSesion.setOnClickListener(view -> iniciarSesionConCorreo());
        vista.btnGoogle.setOnClickListener(view -> lanzadorGoogle.launch(clienteGoogle.getSignInIntent()));
        vista.txtIrRegistro.setOnClickListener(view -> startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.edtCorreo);
        Validaciones.limpiarErrorAlCambiar(vista.edtContrasena);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (autenticacionDao.obtenerUsuarioActual() != null) {
            comprobarPerfilUsuario(autenticacionDao.obtenerUsuarioActual().getUid());
        }
    }

    private void configurarGoogle() {
        GoogleSignInOptions opcionesGoogle = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.idoctor.R.string.default_web_client_id))
                .requestEmail()
                .build();

        clienteGoogle = GoogleSignIn.getClient(this, opcionesGoogle);
    }

    private void iniciarSesionConCorreo() {
        String correo = vista.edtCorreo.getText().toString().trim();
        String contrasena = vista.edtContrasena.getText().toString();

        limpiarErrores();

        if (!formularioValido(correo, contrasena)) {
            return;
        }

        vista.btnIniciarSesion.setEnabled(false);
        autenticacionDao.iniciarSesionConCorreo(correo, contrasena).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful() && tarea.getResult().getUser() != null) {
                comprobarPerfilUsuario(tarea.getResult().getUser().getUid());
                return;
            }

            vista.btnIniciarSesion.setEnabled(true);
            mostrarErrorInicioSesion(tarea.getException());
        });
    }

    private boolean formularioValido(String correo, String contrasena) {
        boolean valido = true;

        if (TextUtils.isEmpty(correo)) {
            vista.edtCorreo.setError("El email es obligatorio");
            valido = false;
        } else if (correo.contains(" ")) {
            vista.edtCorreo.setError("El email no puede contener espacios");
            valido = false;
        } else if (!Validaciones.emailValido(correo)) {
            vista.edtCorreo.setError("Introduce un email valido");
            valido = false;
        }

        if (TextUtils.isEmpty(contrasena)) {
            vista.edtContrasena.setError("La contrasena es obligatoria");
            valido = false;
        }

        return valido;
    }

    private void limpiarErrores() {
        vista.edtCorreo.setError(null);
        vista.edtContrasena.setError(null);
    }

    private void iniciarSesionConCuentaGoogle(GoogleSignInAccount cuentaGoogle) {
        if (cuentaGoogle == null || cuentaGoogle.getIdToken() == null) {
            mostrarMensaje("Google no devolvio token. Revisa el cliente web de Firebase.");
            return;
        }

        AuthCredential credencial = GoogleAuthProvider.getCredential(cuentaGoogle.getIdToken(), null);

        autenticacionDao.iniciarSesionConGoogle(credencial).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful() && tarea.getResult().getUser() != null) {
                comprobarPerfilUsuario(tarea.getResult().getUser().getUid());
                return;
            }

            mostrarMensaje("Fallo en autenticacion con Google");
        });
    }

    private void comprobarPerfilUsuario(String idUsuario) {
        autenticacionDao.obtenerDatosUsuario(idUsuario, new AutenticacionDao.DatosUsuarioListener() {
            @Override
            public void usuarioEncontrado(Usuario usuario) {
                if (usuario == null || TextUtils.isEmpty(usuario.getRol())) {
                    irACompletarPerfil();
                } else if ("patient".equals(usuario.getRol())) {
                    startActivity(new Intent(InicioSesionActivity.this, MenuPacienteActivity.class));
                    finish();
                } else if ("professional".equals(usuario.getRol())) {
                    startActivity(new Intent(InicioSesionActivity.this, MenuProfesionalActivity.class));
                    finish();
                } else {
                    mostrarMensaje("Rol no valido");
                }
            }

            @Override
            public void error(String mensajeError) {
                mostrarMensaje("Sesion iniciada, pero no se pudo leer el perfil");
                irACompletarPerfil();
            }
        });
    }

    private void irACompletarPerfil() {
        startActivity(new Intent(this, CompletarPerfilActivity.class));
        finish();
    }

    private void mostrarErrorInicioSesion(Exception excepcion) {
        if (excepcion instanceof FirebaseAuthInvalidUserException) {
            mostrarMensaje("No existe una cuenta con ese correo");
        } else if (excepcion instanceof FirebaseAuthInvalidCredentialsException) {
            mostrarMensaje("Correo o contrasena incorrectos");
        } else if (excepcion instanceof FirebaseNetworkException) {
            mostrarMensaje("Error de red. Revisa tu conexion");
        } else {
            mostrarMensaje("No se pudo iniciar sesion");
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
