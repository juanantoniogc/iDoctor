package com.example.idoctor.dao;

import com.example.idoctor.models.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UsuarioDao {

    public Task<Void> guardarUsuario(Usuario usuario) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("id", usuario.getId());
        datosUsuario.put("email", usuario.getCorreo());
        datosUsuario.put("name", usuario.getNombre());
        datosUsuario.put("surname", usuario.getApellidos());
        datosUsuario.put("photo", usuario.getFoto());
        datosUsuario.put("role", usuario.getRol());

        return FirebaseDatabase.getInstance()
                .getReference("users")
                .child(usuario.getId())
                .setValue(datosUsuario);
    }
}
