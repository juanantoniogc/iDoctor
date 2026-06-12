package com.example.idoctor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.idoctor.activities.LoginActivity;
import com.example.idoctor.activities.RegisterActivity;
import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding vista;
    private AutenticacionDao autenticacionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        vista = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());
        autenticacionDao = new AutenticacionDao();

        ViewCompat.setOnApplyWindowInsetsListener(vista.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String rol = getIntent().getStringExtra("rol");
        if (rol != null) {
            Toast.makeText(this, "Rol: " + rol, Toast.LENGTH_SHORT).show();
        }

        vista.btnIniciarSesion.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        vista.btnRegistrarse.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    public void cerrarSesion() {
        autenticacionDao.cerrarSesion();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
