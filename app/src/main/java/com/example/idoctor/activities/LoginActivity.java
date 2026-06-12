package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.MainActivity;
import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityLoginBinding;
import com.example.idoctor.models.Usuario;
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

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding vista;
    private AutenticacionDao autenticacionDao;
    private GoogleSignInClient clienteGoogle;

    private final ActivityResultLauncher<Intent> lanzadorGoogle =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() == null) {
                    mostrarMensaje("No se recibieron datos de Google");
                    return;
                }

                try {
                    GoogleSignInAccount cuentaGoogle = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                            .getResult(ApiException.class);
                    iniciarSesionConCuentaGoogle(cuentaGoogle);
                } catch (ApiException e) {
                    mostrarMensaje("Error con Google. Codigo: " + e.getStatusCode());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();

        GoogleSignInOptions opcionesGoogle = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.idoctor.R.string.default_web_client_id))
                .requestEmail()
                .build();

        clienteGoogle = GoogleSignIn.getClient(this, opcionesGoogle);

        vista.btnIniciarSesion.setOnClickListener(view -> iniciarSesionConCorreo());
        vista.btnGoogle.setOnClickListener(view -> {
            lanzadorGoogle.launch(clienteGoogle.getSignInIntent());
        });
        vista.txtIrRegistro.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (autenticacionDao.obtenerUsuarioActual() != null) {
            comprobarPerfilUsuario(autenticacionDao.obtenerUsuarioActual().getUid());
        }
    }

    private void iniciarSesionConCorreo() {
        String correo = vista.edtCorreo.getText().toString().trim();
        String contrasena = vista.edtContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(contrasena)) {
            mostrarMensaje("Rellena correo y contrasena");
            return;
        }

        autenticacionDao.iniciarSesionConCorreo(correo, contrasena).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getUser() != null) {
                comprobarPerfilUsuario(task.getResult().getUser().getUid());
                return;
            }

            mostrarErrorInicioSesion(task.getException());
        });
    }

    private void iniciarSesionConCuentaGoogle(GoogleSignInAccount cuentaGoogle) {
        if (cuentaGoogle == null || cuentaGoogle.getIdToken() == null) {
            mostrarMensaje("Google no devolvio token. Revisa el cliente web de Firebase.");
            return;
        }

        AuthCredential credencial = GoogleAuthProvider.getCredential(cuentaGoogle.getIdToken(), null);

        autenticacionDao.iniciarSesionConGoogle(credencial).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getUser() != null) {
                comprobarPerfilUsuario(task.getResult().getUser().getUid());
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
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("rol", usuario.getRol());
                    startActivity(intent);
                    finish();
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
        Intent intent = new Intent(this, CompleteProfileActivity.class);
        startActivity(intent);
        finish();
    }



    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
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
}
