package com.example.idoctor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.idoctor.dao.AutenticacionDao;
import com.example.idoctor.dao.CitaDao;
import com.example.idoctor.dao.ConsultaDao;
import com.example.idoctor.dao.HorarioDao;
import com.example.idoctor.databinding.ActivityGenerarCitasBinding;
import com.example.idoctor.models.Cita;
import com.example.idoctor.models.Consulta;
import com.example.idoctor.models.Horario;
import com.example.idoctor.validations.Validaciones;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GenerarCitasActivity extends AppCompatActivity {

    private ActivityGenerarCitasBinding vista;
    private AutenticacionDao autenticacionDao;
    private ConsultaDao consultaDao;
    private HorarioDao horarioDao;
    private CitaDao citaDao;
    private List<Consulta> consultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vista = ActivityGenerarCitasBinding.inflate(getLayoutInflater());
        setContentView(vista.getRoot());

        autenticacionDao = new AutenticacionDao();
        consultaDao = new ConsultaDao();
        horarioDao = new HorarioDao();
        citaDao = new CitaDao();
        consultas = new ArrayList<>();

        cargarConsultas();
        configurarLimpiezaErrores();

        vista.btnGenerarCitas.setOnClickListener(view -> prepararGeneracion());
        vista.btnVolver.setOnClickListener(view -> finish());
    }

    private void configurarLimpiezaErrores() {
        Validaciones.limpiarErrorAlCambiar(vista.spConsultas);
        Validaciones.limpiarErrorAlCambiar(vista.edtDuracion);
    }

    private void cargarConsultas() {
        if (autenticacionDao.obtenerUsuarioActual() == null) {
            Toast.makeText(this, "No hay usuario iniciado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();
        consultaDao.obtenerConsultasPorProfesional(idProfesional, new ConsultaDao.ConsultasListener() {
            @Override
            public void consultasEncontradas(List<Consulta> consultasEncontradas) {
                consultas.clear();
                consultas.addAll(consultasEncontradas);
                cargarSpinnerConsultas();
            }

            @Override
            public void error(String mensajeError) {
                Toast.makeText(GenerarCitasActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSpinnerConsultas() {
        List<String> titulos = new ArrayList<>();

        for (Consulta consulta : consultas) {
            titulos.add(consulta.getTitulo());
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, titulos);
        vista.spConsultas.setAdapter(adaptador);

        if (!titulos.isEmpty()) {
            vista.spConsultas.setText(titulos.get(0), false);
        }
    }

    private void prepararGeneracion() {
        if (consultas.isEmpty()) {
            Toast.makeText(this, "Primero crea una consulta", Toast.LENGTH_SHORT).show();
            return;
        }

        String duracionTexto = vista.edtDuracion.getText().toString().trim();
        limpiarErrores();

        if (TextUtils.isEmpty(duracionTexto)) {
            vista.edtDuracion.setError("La duracion es obligatoria");
            return;
        }

        if (!duracionTexto.matches("^[0-9]+$")) {
            vista.edtDuracion.setError("La duracion debe ser un numero");
            return;
        }

        int duracionMinutos;

        try {
            duracionMinutos = Integer.parseInt(duracionTexto);
        } catch (NumberFormatException e) {
            vista.edtDuracion.setError("La duracion debe ser un numero");
            return;
        }

        if (duracionMinutos < 5 || duracionMinutos > 240) {
            vista.edtDuracion.setError("La duracion debe estar entre 5 y 240 minutos");
            return;
        }

        Consulta consulta = obtenerConsultaSeleccionada();
        if (consulta == null) {
            vista.spConsultas.setError("Selecciona una consulta");
            return;
        }

        vista.btnGenerarCitas.setEnabled(false);
        cargarHorariosParaGenerar(consulta, duracionMinutos);
    }

    private Consulta obtenerConsultaSeleccionada() {
        String tituloSeleccionado = vista.spConsultas.getText().toString().trim();

        for (Consulta consulta : consultas) {
            if (tituloSeleccionado.equals(consulta.getTitulo())) {
                return consulta;
            }
        }

        return null;
    }

    private void cargarHorariosParaGenerar(Consulta consulta, int duracionMinutos) {
        horarioDao.obtenerHorariosPorConsulta(consulta.getId(), new HorarioDao.HorariosListener() {
            @Override
            public void horariosEncontrados(List<Horario> horarios) {
                if (horarios.isEmpty()) {
                    vista.btnGenerarCitas.setEnabled(true);
                    Toast.makeText(GenerarCitasActivity.this, "La consulta no tiene horarios", Toast.LENGTH_SHORT).show();
                    return;
                }

                cargarCitasExistentes(consulta, horarios, duracionMinutos);
            }

            @Override
            public void error(String mensajeError) {
                vista.btnGenerarCitas.setEnabled(true);
                Toast.makeText(GenerarCitasActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCitasExistentes(Consulta consulta, List<Horario> horarios, int duracionMinutos) {
        citaDao.obtenerClavesCitasPorConsulta(consulta.getId(), new CitaDao.CitasExistentesListener() {
            @Override
            public void citasEncontradas(Set<String> claves) {
                generarCitas(consulta, horarios, duracionMinutos, claves);
            }

            @Override
            public void error(String mensajeError) {
                vista.btnGenerarCitas.setEnabled(true);
                Toast.makeText(GenerarCitasActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generarCitas(Consulta consulta, List<Horario> horarios, int duracionMinutos, Set<String> clavesExistentes) {
        List<Cita> citas = new ArrayList<>();
        ResumenGeneracion resumen = new ResumenGeneracion();
        int dias = vista.rbMes.isChecked() ? 30 : 7;
        Calendar calendario = Calendar.getInstance();
        String idProfesional = autenticacionDao.obtenerUsuarioActual().getUid();

        for (int i = 0; i < dias; i++) {
            for (Horario horario : horarios) {
                if (esMismoDia(calendario, horario.getDiaSemana())) {
                    resumen.horariosCoincidentes++;
                    crearCitasDelDia(consulta, horario, calendario, duracionMinutos, idProfesional, clavesExistentes, citas, resumen);
                }
            }
            calendario.add(Calendar.DAY_OF_MONTH, 1);
        }

        guardarCitas(citas, resumen);
    }

    private void crearCitasDelDia(Consulta consulta, Horario horario, Calendar calendario, int duracionMinutos,
                                  String idProfesional, Set<String> clavesExistentes, List<Cita> citas,
                                  ResumenGeneracion resumen) {
        int inicio = convertirHoraAMinutos(horario.getHoraInicio());
        int fin = convertirHoraAMinutos(horario.getHoraFin());

        if (inicio < 0 || fin < 0 || fin <= inicio) {
            resumen.horariosInvalidos++;
            return;
        }

        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fecha = formatoFecha.format(calendario.getTime());

        for (int minutos = inicio; minutos + duracionMinutos <= fin; minutos += duracionMinutos) {
            String hora = convertirMinutosAHora(minutos);
            String clave = fecha + "|" + hora;
            resumen.huecosPosibles++;

            if (!clavesExistentes.contains(clave)) {
                Cita cita = new Cita("", consulta.getId(), idProfesional, "", fecha, hora, true);
                citas.add(cita);
                clavesExistentes.add(clave);
            } else {
                resumen.huecosDuplicados++;
            }
        }
    }

    private void guardarCitas(List<Cita> citas, ResumenGeneracion resumen) {
        if (citas.isEmpty()) {
            vista.btnGenerarCitas.setEnabled(true);
            mostrarMotivoSinCitas(resumen);
            return;
        }

        final int[] guardadas = {0};

        for (Cita cita : citas) {
            citaDao.guardarCita(cita).addOnCompleteListener(tarea -> {
                if (tarea.isSuccessful()) {
                    guardadas[0]++;
                    if (guardadas[0] == citas.size()) {
                        Toast.makeText(this, "Citas generadas: " + guardadas[0], Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    vista.btnGenerarCitas.setEnabled(true);
                    Toast.makeText(this, "No se pudo guardar una cita", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void limpiarErrores() {
        vista.spConsultas.setError(null);
        vista.edtDuracion.setError(null);
    }

    private void mostrarMotivoSinCitas(ResumenGeneracion resumen) {
        if (resumen.horariosCoincidentes == 0) {
            Toast.makeText(this, "No hay horarios dentro del periodo elegido", Toast.LENGTH_LONG).show();
        } else if (resumen.horariosInvalidos > 0 && resumen.huecosPosibles == 0) {
            Toast.makeText(this, "Revisa las horas. Usa formato HH:mm y que fin sea mayor que inicio", Toast.LENGTH_LONG).show();
        } else if (resumen.huecosPosibles == 0) {
            Toast.makeText(this, "La duracion no cabe dentro del horario", Toast.LENGTH_LONG).show();
        } else if (resumen.huecosDuplicados == resumen.huecosPosibles) {
            Toast.makeText(this, "Esas citas ya estaban generadas", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No hay citas nuevas para generar", Toast.LENGTH_LONG).show();
        }
    }

    private boolean esMismoDia(Calendar calendario, String diaSemana) {
        int diaCalendar = calendario.get(Calendar.DAY_OF_WEEK);
        return obtenerDiaCalendar(diaSemana) == diaCalendar;
    }

    private int obtenerDiaCalendar(String diaSemana) {
        if (diaSemana == null) {
            return -1;
        }

        String dia = diaSemana.toLowerCase(Locale.ROOT);

        if (dia.startsWith("lunes")) return Calendar.MONDAY;
        if (dia.startsWith("martes")) return Calendar.TUESDAY;
        if (dia.startsWith("mi")) return Calendar.WEDNESDAY;
        if (dia.startsWith("jueves")) return Calendar.THURSDAY;
        if (dia.startsWith("viernes")) return Calendar.FRIDAY;
        if (dia.startsWith("s")) return Calendar.SATURDAY;
        if (dia.startsWith("domingo")) return Calendar.SUNDAY;

        return -1;
    }

    private int convertirHoraAMinutos(String hora) {
        if (hora == null || !hora.contains(":")) {
            return -1;
        }

        String[] partes = hora.split(":");
        if (partes.length != 2) {
            return -1;
        }

        try {
            int horas = Integer.parseInt(partes[0]);
            int minutos = Integer.parseInt(partes[1]);

            if (horas < 0 || horas > 23 || minutos < 0 || minutos > 59) {
                return -1;
            }

            return horas * 60 + minutos;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String convertirMinutosAHora(int minutosTotales) {
        int horas = minutosTotales / 60;
        int minutos = minutosTotales % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", horas, minutos);
    }

    private static class ResumenGeneracion {
        int horariosCoincidentes;
        int horariosInvalidos;
        int huecosPosibles;
        int huecosDuplicados;
    }
}
