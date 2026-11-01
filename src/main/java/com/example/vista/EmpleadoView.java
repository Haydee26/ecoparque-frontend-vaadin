package com.example.vista;

import com.example.modelo.Empleado;
import com.example.seguridad.SesionUsuario;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.EmpleadoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

/**
 * Base de datos de empleados: todas las personas del parque (incluido el administrador),
 * con buscador por nombre, apellido o DNI. Se ordenan por antiguedad, del mas viejo al mas nuevo.
 * Solo para el administrador.
 */
@Route(value = "empleados", layout = MainLayout.class)
@PageTitle("Empleados | EcoParque")
@SoloAdministrador
public class EmpleadoView extends VistaCrud<Empleado> {

    private final EmpleadoService empleadoService;
    private final TextField buscador = new TextField();

    public EmpleadoView(EmpleadoService servicio, SesionUsuario sesion) {
        super(Empleado.class, servicio, sesion);
        this.empleadoService = servicio;
        buscador.setPlaceholder("Buscar por nombre, apellido o DNI");
        buscador.setPrefixComponent(VaadinIcon.SEARCH.create());
        buscador.setClearButtonVisible(true);
        buscador.setWidth("24rem");
        // Busca solo, un instante despues de que se deja de escribir.
        buscador.setValueChangeMode(ValueChangeMode.LAZY);
        buscador.addValueChangeListener(evento -> actualizarTabla());
    }

    @Override
    protected String titulo() {
        return "Empleados";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar empleado";
    }

    @Override
    protected String textoModificar() {
        return "Modificar empleado";
    }

    @Override
    protected String describir(Empleado empleado) {
        return empleado.nombreCompleto();
    }

    /** Usa su propio buscador, que consulta al backend por nombre, apellido o DNI. */
    @Override
    protected boolean usaBuscadorIntegrado() {
        return false;
    }

    @Override
    protected List<Empleado> obtenerElementos() {
        return empleadoService.buscar(buscador.getValue());
    }

    @Override
    protected Component[] componentesSobreLaTabla() {
        return new Component[]{buscador};
    }

    @Override
    protected void configurarColumnas(Grid<Empleado> tabla) {
        tabla.addColumn(Empleado::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(Empleado::getApellido).setHeader("Apellido").setSortable(true);
        tabla.addColumn(Empleado::getDni).setHeader("DNI");
        tabla.addColumn(empleado -> Formatos.fecha(empleado.getFechaNacimiento())).setHeader("Fecha de nacimiento");
        tabla.addColumn(Empleado::getTelefono).setHeader("Teléfono");
        tabla.addColumn(empleado -> texto(empleado.getCorreo())).setHeader("Correo electrónico");
        tabla.addColumn(Empleado::getDireccion).setHeader("Dirección");
        tabla.addColumn(empleado -> empleado.getPuestos() == null || empleado.getPuestos().isEmpty()
                ? "Sin puesto" : String.join(", ", empleado.getPuestos())).setHeader("Puesto");
        tabla.addColumn(empleado -> Formatos.fecha(empleado.getFechaIngreso())).setHeader("Fecha de ingreso");
        tabla.addColumn(empleado -> anios(empleado.getAnios())).setHeader("Antigüedad");
    }

    private static String texto(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private static String anios(Long anios) {
        if (anios == null) {
            return "-";
        }
        return anios == 0 ? "Menos de 1 año" : anios + (anios == 1 ? " año" : " años");
    }

    @Override
    protected Formulario<Empleado> crearFormulario(Empleado existente) {
        return new FormularioEmpleado(existente);
    }

    private static class FormularioEmpleado implements Formulario<Empleado> {

        private final TextField nombre = new TextField("Nombre");
        private final TextField apellido = new TextField("Apellido");
        private final TextField dni = new TextField("DNI");
        private final DatePicker fechaNacimiento = new DatePicker("Fecha de nacimiento");
        private final TextField direccion = new TextField("Dirección");
        private final TextField telefono = new TextField("Teléfono");
        private final TextField correo = new TextField("Correo electrónico (opcional)");
        private final DatePicker fechaIngreso = new DatePicker("Fecha de ingreso");
        private final CheckboxGroup<String> puestos = new CheckboxGroup<>("Puesto");

        FormularioEmpleado(Empleado existente) {
            dni.setHelperText("7 u 8 números, sin puntos");
            puestos.setItems("Guía", "Cuidador");
            puestos.setHelperText("Puede ser guía, cuidador, los dos o ninguno.");
            if (existente != null) {
                nombre.setValue(valor(existente.getNombre()));
                apellido.setValue(valor(existente.getApellido()));
                dni.setValue(valor(existente.getDni()));
                direccion.setValue(valor(existente.getDireccion()));
                telefono.setValue(valor(existente.getTelefono()));
                correo.setValue(valor(existente.getCorreo()));
                cargarFecha(fechaNacimiento, existente.getFechaNacimiento());
                cargarFecha(fechaIngreso, existente.getFechaIngreso());
                if (existente.getPuestos() != null) {
                    puestos.setValue(Set.copyOf(existente.getPuestos().stream()
                            .filter(p -> p.equals("Guía") || p.equals("Cuidador")).toList()));
                }
            }
        }

        private static String valor(String texto) {
            return texto == null ? "" : texto;
        }

        private static void cargarFecha(DatePicker campo, String iso) {
            if (iso == null || iso.isBlank()) {
                return;
            }
            try {
                campo.setValue(LocalDate.parse(iso));
            } catch (DateTimeParseException e) {
                // si la fecha guardada no se puede leer, el campo queda vacio
            }
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombre, apellido, dni, fechaNacimiento, direccion, telefono, correo, fechaIngreso, puestos};
        }

        @Override
        public String validar() {
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaNacimiento.isEmpty()
                    || direccion.isEmpty() || telefono.isEmpty() || fechaIngreso.isEmpty()) {
                return "Completá todos los datos. Solo el correo es opcional.";
            }
            return null;
        }

        @Override
        public Empleado construir() {
            Empleado empleado = new Empleado();
            empleado.setNombre(nombre.getValue().trim());
            empleado.setApellido(apellido.getValue().trim());
            empleado.setDni(dni.getValue().trim());
            empleado.setFechaNacimiento(fechaNacimiento.getValue().toString());
            empleado.setDireccion(direccion.getValue().trim());
            empleado.setTelefono(telefono.getValue().trim());
            empleado.setCorreo(correo.getValue().trim());
            empleado.setFechaIngreso(fechaIngreso.getValue().toString());
            List<String> roles = new ArrayList<>();
            if (puestos.getValue().contains("Guía")) {
                roles.add("GUIA");
            }
            if (puestos.getValue().contains("Cuidador")) {
                roles.add("CUIDADOR");
            }
            empleado.setRoles(roles);
            return empleado;
        }
    }
}
