package com.example.vista;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Formas de mostrar datos en pantalla.
 */
public final class Formatos {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatos() {
    }

    /** Pasa una fecha ISO (aaaa-mm-dd) a dd/mm/aaaa. */
    public static String fecha(String iso) {
        if (iso == null || iso.isBlank()) {
            return "";
        }
        try {
            return LocalDate.parse(iso).format(FECHA);
        } catch (DateTimeParseException e) {
            return iso;
        }
    }

    /** Muestra una hora (hh:mm:ss) como hh:mm. */
    public static String hora(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        try {
            return LocalTime.parse(texto).format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return texto;
        }
    }

    public static String metrosCuadrados(Double valor) {
        if (valor == null) {
            return "";
        }
        return String.format("%.1f m²", valor);
    }

    /** Une los nombres de una lista de elementos separados por coma. */
    public static <T> String unir(List<T> elementos, Function<T, String> nombre) {
        if (elementos == null || elementos.isEmpty()) {
            return "";
        }
        return elementos.stream().map(nombre).collect(Collectors.joining(", "));
    }
}
