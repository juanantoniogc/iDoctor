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
import com.example.idoctor.validations.Validaciones;

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
        configurarLimpiezaErrores();

        vista.btnGuardar.setOnClickListener(view -> guardarConsulta());
        vista.btnCancelar.setOnClickListener(view -> finish());
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.edtTitulo);
        Validaciones.limpiarErrorAlCambiar(vista.edtCorreo);
        Validaciones.limpiarErrorAlCambiar(vista.edtTelefono);
        Validaciones.limpiarErrorAlCambiar(vista.edtTelefonoAuxiliar);
        Validaciones.limpiarErrorAlCambiar(vista.edtUrl);
        Validaciones.limpiarErrorAlCambiar(vista.edtObservaciones);
        Validaciones.limpiarErrorAlCambiar(vista.edtCalle);
        Validaciones.limpiarErrorAlCambiar(vista.edtNumero);
        Validaciones.limpiarErrorAlCambiar(vista.edtCiudad);
        Validaciones.limpiarErrorAlCambiar(vista.edtProvincia);
        Validaciones.limpiarErrorAlCambiar(vista.edtCodigoPostal);
        Validaciones.limpiarErrorAlCambiar(vista.edtPais);
    }

    private void cargarDatosSiEdita() {
        idConsulta = getIntent().getStringExtra("idConsulta");

        vista.edtTitulo.setText(getIntent().getStringExtra("titulo"));
        vista.edtCorreo.setText(getIntent().getStringExtra("correo"));
        vista.edtTelefono.setText(getIntent().getStringExtra("telefono"));
        vista.edtTelefonoAuxiliar.setText(getIntent().getStringExtra("telefonoAuxiliar"));
        vista.edtUrl.setText(getIntent().getStringExtra("url"));
        vista.edtObservaciones.setText(getIntent().getStringExtra("observaciones"));
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
        String telefonoAuxiliar = vista.edtTelefonoAuxiliar.getText().toString().trim();
        String url = vista.edtUrl.getText().toString().trim();
        String observaciones = vista.edtObservaciones.getText().toString().trim();

        limpiarErrores();

        if (!formularioValido(titulo, correo, telefono, telefonoAuxiliar, observaciones)) {
            return;
        }

        vista.btnGuardar.setEnabled(false);
        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();
        Direccion direccion = crearDireccion();
        Consulta consulta = new Consulta(idConsulta, idProfesional, titulo, correo, telefono, url, direccion);
        consulta.setTelefonoAuxiliar(telefonoAuxiliar);
        consulta.setObservaciones(observaciones);

        consultaDao.guardarConsulta(consulta).addOnCompleteListener(tarea -> {
            if (tarea.isSuccessful()) {
                Toast.makeText(this, "Consulta guardada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                vista.btnGuardar.setEnabled(true);
                Toast.makeText(this, "No se pudo guardar la consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean formularioValido(String titulo, String correo, String telefono,
                                     String telefonoAuxiliar, String observaciones) {
        boolean valido = true;

        if (TextUtils.isEmpty(titulo)) {
            vista.edtTitulo.setError("El titulo es obligatorio");
            valido = false;
        } else if (!Validaciones.textoEntre(titulo, 3, 80)) {
            vista.edtTitulo.setError("El titulo debe tener entre 3 y 80 caracteres");
            valido = false;
        }

        if (TextUtils.isEmpty(correo)) {
            vista.edtCorreo.setError("El correo es obligatorio");
            valido = false;
        } else if (!Validaciones.emailValido(correo)) {
            vista.edtCorreo.setError("Introduce un email valido");
            valido = false;
        }

        if (TextUtils.isEmpty(telefono)) {
            vista.edtTelefono.setError("El telefono es obligatorio");
            valido = false;
        } else if (!Validaciones.telefonoValido(telefono)) {
            vista.edtTelefono.setError("El telefono no tiene un formato valido");
            valido = false;
        }

        if (!TextUtils.isEmpty(telefonoAuxiliar) && !Validaciones.telefonoValido(telefonoAuxiliar)) {
            vista.edtTelefonoAuxiliar.setError("El telefono auxiliar no tiene un formato valido");
            valido = false;
        }

        if (!Validaciones.textoOpcionalMaximo(observaciones, 500)) {
            vista.edtObservaciones.setError("Las observaciones no pueden superar 500 caracteres");
            valido = false;
        }

        if (!direccionValida()) {
            valido = false;
        }

        return valido;
    }

    private boolean direccionValida() {
        boolean valido = true;
        String calle = vista.edtCalle.getText().toString().trim();
        String numero = vista.edtNumero.getText().toString().trim();
        String ciudad = vista.edtCiudad.getText().toString().trim();
        String provincia = vista.edtProvincia.getText().toString().trim();
        String codigoPostal = vista.edtCodigoPostal.getText().toString().trim();
        String pais = vista.edtPais.getText().toString().trim();

        if (TextUtils.isEmpty(calle)) {
            vista.edtCalle.setError("La calle es obligatoria");
            valido = false;
        }

        if (TextUtils.isEmpty(numero)) {
            vista.edtNumero.setError("El numero es obligatorio");
            valido = false;
        } else if (!Validaciones.numeroDireccionValido(numero)) {
            vista.edtNumero.setError("Usa un numero tipo 12, 12B o s/n");
            valido = false;
        }

        if (TextUtils.isEmpty(ciudad)) {
            vista.edtCiudad.setError("La ciudad es obligatoria");
            valido = false;
        }

        if (TextUtils.isEmpty(provincia)) {
            vista.edtProvincia.setError("La provincia es obligatoria");
            valido = false;
        }

        if (TextUtils.isEmpty(codigoPostal)) {
            vista.edtCodigoPostal.setError("El codigo postal es obligatorio");
            valido = false;
        } else if (!Validaciones.codigoPostalEspanol(codigoPostal)) {
            vista.edtCodigoPostal.setError("El codigo postal debe tener 5 digitos");
            valido = false;
        }

        if (TextUtils.isEmpty(pais)) {
            vista.edtPais.setError("El pais es obligatorio");
            valido = false;
        }

        return valido;
    }

    private void limpiarErrores() {
        vista.edtTitulo.setError(null);
        vista.edtCorreo.setError(null);
        vista.edtTelefono.setError(null);
        vista.edtTelefonoAuxiliar.setError(null);
        vista.edtUrl.setError(null);
        vista.edtObservaciones.setError(null);
        vista.edtCalle.setError(null);
        vista.edtNumero.setError(null);
        vista.edtCiudad.setError(null);
        vista.edtProvincia.setError(null);
        vista.edtCodigoPostal.setError(null);
        vista.edtPais.setError(null);
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
