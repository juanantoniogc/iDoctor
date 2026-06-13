package com.example.idoctor.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.databinding.ActivityFormularioConsultaBinding;
import com.example.idoctor.models.Consulta;
import com.example.idoctor.models.Direccion;

public class FormularioConsultaActivity extends AppCompatActivity {

    private ActivityFormularioConsultaBinding vista;
    private AutenticacionDao autenticacionDao;
    private ConsultaDao consultaDao;
    private String idConsulta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityFormularioConsultaBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        consultaDao = new ConsultaDao();

        cargarDatosSiEdita();

        vista.btnGuardar.setOnClickListener(view -> guardarConsulta());
        vista.btnCancelar.setOnClickListener(view -> finish());
    }

    private void cargarDatosSiEdita() {
        idConsulta = getIntent().getStringExtra("idConsulta");

        vista.edtTitulo.setText(getIntent().getStringExtra("titulo"));
        vista.edtCorreo.setText(getIntent().getStringExtra("correo"));
        vista.edtTelefono.setText(getIntent().getStringExtra("telefono"));
        vista.edtUrl.setText(getIntent().getStringExtra("url"));
        vista.edtCalle.setText(getIntent().getStringExtra("street"));
        vista.edtNumero.setText(getIntent().getStringExtra("number"));
        vista.edtPiso.setText(getIntent().getStringExtra("floor"));
        vista.edtPortal.setText(getIntent().getStringExtra("portal"));
        vista.edtCiudad.setText(getIntent().getStringExtra("city"));
        vista.edtProvincia.setText(getIntent().getStringExtra("province"));
        vista.edtCodigoPostal.setText(getIntent().getStringExtra("postalCode"));
        vista.edtPais.setText(getIntent().getStringExtra("country"));
    }

    private void guardarConsulta() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            startActivity(new Intent(this, InicioSesionActivity.class));
            finish();
            return;
        }

        String titulo = vista.edtTitulo.getText().toString().trim();
        String correo = vista.edtCorreo.getText().toString().trim();
        String telefono = vista.edtTelefono.getText().toString().trim();
        String url = vista.edtUrl.getText().toString().trim();

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(telefono)) {
            Toast.makeText(this, "Rellena titulo, correo y telefono", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(idConsulta)) {
            idConsulta = consultaDao.crearIdConsulta();
        }

        if (TextUtils.isEmpty(idConsulta)) {
            Toast.makeText(this, "No se pudo crear la consulta", Toast.LENGTH_SHORT).show();
            return;
        }

        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();
        Direccion direccion = crearDireccion();
        Consulta consulta = new Consulta(idConsulta, idProfesional, titulo, correo, telefono, url, direccion);

        consultaDao.guardarConsulta(consulta).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Consulta guardada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "No se pudo guardar la consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Direccion crearDireccion() {
        return new Direccion(
                vista.edtCalle.getText().toString().trim(),
                vista.edtNumero.getText().toString().trim(),
                vista.edtPiso.getText().toString().trim(),
                vista.edtPortal.getText().toString().trim(),
                vista.edtCiudad.getText().toString().trim(),
                vista.edtProvincia.getText().toString().trim(),
                vista.edtCodigoPostal.getText().toString().trim(),
                vista.edtPais.getText().toString().trim()
        );
    }
}
