package com.example.vista;

import com.example.modelo.Persona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.ServicioRest;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Supplier;

/**
 * Pantalla comun para las personas del ecoparque (guias y cuidadores),
 * que comparten los mismos datos.
 */
public abstract class VistaPersonas<P extends Persona> extends VistaCrud<P> {

    private final Supplier<P> fabrica;

    protected VistaPersonas(Class<P> tipo, ServicioRest<P> servicio,
            SesionUsuario sesion, Supplier<P> fabrica) {
        super(tipo, servicio, sesion);
        this.fabrica = fabrica;
    }

    @Override
    protected String describir(P persona) {
        return persona.getNombre();
    }

    @Override
    protected void configurarColumnas(Grid<P> tabla) {
        tabla.addColumn(Persona::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(Persona::getDireccion).setHeader("Dirección");
        tabla.addColumn(Persona::getTelefono).setHeader("Teléfono");
        tabla.addColumn(persona -> Formatos.fecha(persona.getFechaIngreso())).setHeader("Fecha de ingreso");
    }

    @Override
    protected Formulario<P> crearFormulario(P existente) {
        return new FormularioPersona<>(existente, fabrica);
    }

    private static class FormularioPersona<P extends Persona> implements Formulario<P> {

        private final Supplier<P> fabrica;
        private final TextField nombre = new TextField("Nombre");
        private final TextField direccion = new TextField("Dirección");
        private final TextField telefono = new TextField("Teléfono");
        private final DatePicker fechaIngreso = new DatePicker("Fecha de ingreso");

        FormularioPersona(P existente, Supplier<P> fabrica) {
            this.fabrica = fabrica;
            if (existente != null) {
                nombre.setValue(valor(existente.getNombre()));
                direccion.setValue(valor(existente.getDireccion()));
                telefono.setValue(valor(existente.getTelefono()));
                cargarFecha(existente.getFechaIngreso());
            }
        }

        private static String valor(String texto) {
            return texto == null ? "" : texto;
        }

        private void cargarFecha(String iso) {
            if (iso == null || iso.isBlank()) {
                return;
            }
            try {
                fechaIngreso.setValue(LocalDate.parse(iso));
            } catch (DateTimeParseException e) {
                // si la fecha guardada no se puede leer, el campo queda vacio
            }
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombre, direccion, telefono, fechaIngreso};
        }

        @Override
        public String validar() {
            if (nombre.isEmpty() || fechaIngreso.isEmpty()) {
                return "Completá al menos el nombre y la fecha de ingreso.";
            }
            return null;
        }

        @Override
        public P construir() {
            P persona = fabrica.get();
            persona.setNombre(nombre.getValue().trim());
            persona.setDireccion(direccion.getValue().trim());
            persona.setTelefono(telefono.getValue().trim());
            persona.setFechaIngreso(fechaIngreso.getValue().toString());
            return persona;
        }
    }
}
