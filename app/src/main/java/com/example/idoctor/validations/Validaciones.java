package com.example.idoctor.validations;

import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class Validaciones {

    private static final Pattern LETRAS_ESPACIOS_GUIONES = Pattern.compile("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s-]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?[0-9\\s-]{9,15}$");
    private static final Pattern DNI = Pattern.compile("^[0-9]{8}[A-Za-z]$");
    private static final Pattern HORA = Pattern.compile("^([01][0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern CODIGO_POSTAL = Pattern.compile("^[0-9]{5}$");
    private static final Pattern COLEGIADO = Pattern.compile("^[A-Za-z0-9-]{4,30}$");
    private static final Pattern NUMERO_DIRECCION = Pattern.compile("^([0-9]+[A-Za-z]?|s/n|S/N)$");
    private static final Pattern ID_SIMPLE = Pattern.compile("^[A-Za-z0-9_-]+$");

    private Validaciones() {
    }

    public static boolean emailValido(String email) {
        return !TextUtils.isEmpty(email)
                && email.length() <= 100
                && !email.contains(" ")
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean contrasenaFuerte(String contrasena) {
        if (TextUtils.isEmpty(contrasena) || contrasena.length() < 8) {
            return false;
        }

        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneNumero = false;

        for (char caracter : contrasena.toCharArray()) {
            if (Character.isUpperCase(caracter)) {
                tieneMayuscula = true;
            } else if (Character.isLowerCase(caracter)) {
                tieneMinuscula = true;
            } else if (Character.isDigit(caracter)) {
                tieneNumero = true;
            }
        }

        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

    public static boolean telefonoValido(String telefono) {
        return !TextUtils.isEmpty(telefono) && TELEFONO.matcher(telefono).matches();
    }

    public static boolean dniValido(String dni) {
        if (TextUtils.isEmpty(dni) || !DNI.matcher(dni).matches()) {
            return false;
        }

        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(dni.substring(0, 8));
        char letraCalculada = letras.charAt(numero % 23);
        char letraUsuario = Character.toUpperCase(dni.charAt(8));
        return letraUsuario == letraCalculada;
    }

    public static boolean fechaValida(String fecha) {
        return parsearFecha(fecha) != null;
    }

    public static boolean fechaNoPasada(String fecha) {
        Calendar fechaCita = parsearFecha(fecha);
        if (fechaCita == null) {
            return false;
        }

        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MILLISECOND, 0);

        return !fechaCita.before(hoy);
    }

    private static Calendar parsearFecha(String fecha) {
        if (TextUtils.isEmpty(fecha)) {
            return null;
        }

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formato.setLenient(false);

        try {
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(formato.parse(fecha));
            calendario.set(Calendar.HOUR_OF_DAY, 0);
            calendario.set(Calendar.MINUTE, 0);
            calendario.set(Calendar.SECOND, 0);
            calendario.set(Calendar.MILLISECOND, 0);
            return calendario;
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean horaValida(String hora) {
        return !TextUtils.isEmpty(hora) && HORA.matcher(hora).matches();
    }

    public static int horaAMinutos(String hora) {
        if (!horaValida(hora)) {
            return -1;
        }

        String[] partes = hora.split(":");
        return Integer.parseInt(partes[0]) * 60 + Integer.parseInt(partes[1]);
    }

    public static boolean textoEntre(String texto, int minimo, int maximo) {
        if (texto == null) {
            return false;
        }

        int longitud = texto.trim().length();
        return longitud >= minimo && longitud <= maximo;
    }

    public static boolean textoOpcionalMaximo(String texto, int maximo) {
        return texto == null || texto.trim().length() <= maximo;
    }

    public static boolean soloLetrasEspaciosGuiones(String texto) {
        return !TextUtils.isEmpty(texto) && LETRAS_ESPACIOS_GUIONES.matcher(texto).matches();
    }

    public static boolean codigoPostalEspanol(String codigoPostal) {
        return !TextUtils.isEmpty(codigoPostal) && CODIGO_POSTAL.matcher(codigoPostal).matches();
    }

    public static boolean numeroColegiadoValido(String numeroColegiado) {
        return !TextUtils.isEmpty(numeroColegiado) && COLEGIADO.matcher(numeroColegiado).matches();
    }

    public static boolean numeroDireccionValido(String numero) {
        return !TextUtils.isEmpty(numero) && NUMERO_DIRECCION.matcher(numero).matches();
    }

    public static boolean idSimpleValido(String id) {
        return !TextUtils.isEmpty(id) && id.trim().equals(id) && ID_SIMPLE.matcher(id).matches();
    }

    public static void limpiarErrorAlCambiar(TextView campo) {
        campo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                campo.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
