package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityCompleteProfileBinding;

public class CompleteProfileActivity extends AppCompatActivity {

    private ActivityCompleteProfileBinding vista;
    private AutenticacionDao autenticacionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();

        vista.btnCerrarSesion.setOnClickListener(view -> {
            autenticacionDao.cerrarSesion();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        vista.btnGuardarPerfil.setOnClickListener(view -> {
            // En la siguiente fase guardaremos patient/professional y users/{uid}.
            Toast.makeText(this, "Perfil pendiente para la siguiente fase", Toast.LENGTH_SHORT).show();
        });
    }
}
