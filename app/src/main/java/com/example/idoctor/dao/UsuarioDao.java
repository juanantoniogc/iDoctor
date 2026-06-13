package com.example.idoctor.dao;

import com.example.idoctor.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class UsuarioDao {

    public Task<Void> guardarUsuario(Usuario usuario) {
        return FirebaseDatabase.getInstance()
                .getReference("users")
                .child(usuario.getId())
                .setValue(usuario);
    }
}
