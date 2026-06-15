package com.example.idoctor.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.databinding.ActivityDetalleValoracionBinding;
import com.example.idoctor.models.Paciente;
import com.example.idoctor.models.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetalleValoracionActivity extends AppCompatActivity {

    private ActivityDetalleValoracionBinding vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityDetalleValoracionBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        mostrarDatos();
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void mostrarDatos() {
        int estrellas = getIntent().getIntExtra("estrellas", 0);

        vista.txtEstrellas.setText(estrellas + " de 5 estrellas");
        vista.ratingEstrellas.setRating(estrellas);
        vista.txtComentario.setText("Comentario: " + texto(getIntent().getStringExtra("comentario")));
        vista.txtMomento.setText(formatearMomento(getIntent().getStringExtra("momento")));
        cargarNombrePaciente(getIntent().getStringExtra("idPaciente"));
    }

    private void cargarNombrePaciente(String idPaciente) {
        if (idPaciente == null || idPaciente.trim().isEmpty()) {
            vista.txtPaciente.setText("Paciente: Sin datos");
            return;
        }

        FirebaseDatabase.getInstance().getReference("patients").child(idPaciente)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Paciente paciente = snapshot.getValue(Paciente.class);
                            vista.txtPaciente.setText("Paciente: " + nombreCompleto(paciente));
                        } else {
                            cargarNombreUsuario(idPaciente);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cargarNombreUsuario(idPaciente);
                    }
                });
    }

    private void cargarNombreUsuario(String idPaciente) {
        FirebaseDatabase.getInstance().getReference("users").child(idPaciente)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        vista.txtPaciente.setText("Paciente: " + nombreCompleto(usuario));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        vista.txtPaciente.setText("Paciente: Sin datos");
                    }
                });
    }

    private String nombreCompleto(Paciente paciente) {
        if (paciente == null) {
            return "Sin datos";
        }

        String nombre = valorSeguro(paciente.getNombre());
        String apellidos = valorSeguro(paciente.getApellidos());
        String nombreCompleto = (nombre + " " + apellidos).trim();
        return nombreCompleto.isEmpty() ? "Sin datos" : nombreCompleto;
    }

    private String nombreCompleto(Usuario usuario) {
        if (usuario == null) {
            return "Sin datos";
        }

        String nombre = valorSeguro(usuario.getNombre());
        String apellidos = valorSeguro(usuario.getApellidos());
        String nombreCompleto = (nombre + " " + apellidos).trim();
        return nombreCompleto.isEmpty() ? "Sin datos" : nombreCompleto;
    }

    private String formatearMomento(String momento) {
        if (momento == null || momento.trim().isEmpty()) {
            return "Valorado en fecha no disponible";
        }

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat formatoSalida = new SimpleDateFormat("'Valorado el' d 'de' MMMM 'de' yyyy 'a las' HH:mm", new Locale("es", "ES"));

        try {
            return formatoSalida.format(formatoEntrada.parse(momento));
        } catch (ParseException e) {
            return "Valorado el " + momento;
        }
    }

    private String texto(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "Sin datos";
        }

        return valor;
    }

    private String valorSeguro(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
