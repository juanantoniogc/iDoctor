package com.example.idoctor.dao;

import androidx.annotation.NonNull;

import com.example.idoctor.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AutenticacionDao {

    private final FirebaseAuth autenticacion;
    private final DatabaseReference referenciaUsuarios;

    public AutenticacionDao() {
        autenticacion = FirebaseAuth.getInstance();
        referenciaUsuarios = FirebaseDatabase.getInstance().getReference("users");
    }

    public Task<AuthResult> iniciarSesionConCorreo(String correo, String contrasena) {
        return autenticacion.signInWithEmailAndPassword(correo, contrasena);
    }

    public Task<AuthResult> registrarConCorreo(String correo, String contrasena) {
        return autenticacion.createUserWithEmailAndPassword(correo, contrasena);
    }

    public Task<AuthResult> iniciarSesionConGoogle(AuthCredential credencial) {
        return autenticacion.signInWithCredential(credencial);
    }

    public FirebaseUser obtenerUsuarioActual() {
        return autenticacion.getCurrentUser();
    }

    public void cerrarSesion() {
        autenticacion.signOut();
    }

    public void obtenerDatosUsuario(String idUsuario, DatosUsuarioListener listener) {
        referenciaUsuarios.child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    listener.usuarioEncontrado(usuario);
                } else {
                    listener.usuarioEncontrado(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.error(error.getMessage());
            }
        });
    }

    public interface DatosUsuarioListener {
        void usuarioEncontrado(Usuario usuario);

        void error(String mensajeError);
    }
}
