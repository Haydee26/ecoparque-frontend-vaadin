package com.example.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public static String metrosCuadrados(Double valor) {
        if (valor == null) {
            return "";
        }
        return String.format("%.1f m²", valor);
    }
}
