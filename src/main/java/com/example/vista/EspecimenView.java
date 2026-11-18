package com.example.vista;

import com.example.modelo.Especie;
import com.example.modelo.Especimen;
import com.example.modelo.EstadoSalud;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.EspecieService;
import com.example.servicio.EspecimenService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.util.List;

@Route(value = "especimenes", layout = MainLayout.class)
@PageTitle("Especímenes | EcoParque")
public class EspecimenView extends VistaCrud<Especimen> {

    private final EspecieService especieService;
    private final EspecimenService especimenService;
    private final SesionUsuario sesion;

    public EspecimenView(EspecimenService servicio, EspecieService especieService, SesionUsuario sesion) {
        super(Especimen.class, servicio, sesion);
        this.especieService = especieService;
        this.especimenService = servicio;
        this.sesion = sesion;
    }

    @Override
    protected String titulo() {
        return "Gestión de Especímenes";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nuevo especimen";
    }

    @Override
    protected String textoModificar() {
        return "Modificar especimen";
    }

    @Override
    protected String describir(Especimen especimen) {
        return especimen.getNombre();
    }

    @Override
    protected void configurarColumnas(Grid<Especimen> tabla) {
        tabla.addColumn(Especimen::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(e -> e.getEspecie() == null ? "" : e.getEspecie().getNombreCientifico())
                .setHeader("Especie").setSortable(true);
        tabla.addColumn(e -> e.getFechaIngreso() == null ? "" : Formatos.fecha(e.getFechaIngreso().toString()))
                .setHeader("Fecha de ingreso");
        tabla.addColumn(e -> e.getEstadoSalud() == null ? "" : e.getEstadoSalud().getTexto())
                .setHeader("Estado de salud").setSortable(true);
        tabla.addColumn(e -> e.getObservacionSalud() == null ? "" : e.getObservacionSalud())
                .setHeader("Observación");
                // Quien puede editar la salud (cuidador, veterinario, administrador) ve este boton,
        // aunque no pueda registrar ni modificar especimenes.
        if (sesion.tiene("SALUD_EDITAR")) {
            tabla.addComponentColumn(e -> new Button("Cambiar salud",
                    evento -> new DialogoSalud(e, especimenService, this::actualizarTabla).open()))
                    .setHeader("Salud").setAutoWidth(true);
        }
    }
   
    /**
     * La lista de especies se pide al backend cada vez que se abre el
     * formulario.
     */
    @Override
    protected Formulario<Especimen> crearFormulario(Especimen existente) {
        return new FormularioEspecimen(existente, especieService.listar());
    }

    private static class FormularioEspecimen implements Formulario<Especimen> {

        private final TextField nombre = new TextField("Nombre");
        private final ComboBox<Especie> especie = new ComboBox<>("Especie");
        private final DatePicker fechaIngreso = new DatePicker("Fecha de ingreso");
        private final ComboBox<EstadoSalud> estadoSalud = new ComboBox<>("Estado de salud");
        private final TextArea observacion = new TextArea("Observación de salud");

        FormularioEspecimen(Especimen existente, List<Especie> especies) {
            especie.setItems(especies);
            especie.setItemLabelGenerator(Especie::getNombreCientifico);
            fechaIngreso.setMax(LocalDate.now());
            estadoSalud.setItems(EstadoSalud.values());
            estadoSalud.setItemLabelGenerator(EstadoSalud::getTexto);
            estadoSalud.setValue(EstadoSalud.SANO);

            if (existente != null) {
                nombre.setValue(existente.getNombre() == null ? "" : existente.getNombre());
                especie.setValue(existente.getEspecie());
                fechaIngreso.setValue(existente.getFechaIngreso());
                estadoSalud.setValue(existente.getEstadoSalud());
                observacion.setValue(existente.getObservacionSalud() == null ? "" : existente.getObservacionSalud());
            }
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombre, especie, fechaIngreso, estadoSalud, observacion};
        }

        @Override
        public String validar() {
            if (nombre.getValue().isBlank() || especie.isEmpty() || fechaIngreso.isEmpty() || estadoSalud.isEmpty()) {
                return "Completá el nombre, la especie, la fecha de ingreso y el estado de salud.";
            }
            if (fechaIngreso.getValue().isAfter(LocalDate.now())) {
                return "La fecha de ingreso no puede ser futura.";
            }
            if (estadoSalud.getValue() != EstadoSalud.SANO && observacion.getValue().isBlank()) {
                return "Si no está sano, escribí qué le pasa en la observación.";
            }
            return null;
        }

        @Override
        public Especimen construir() {
            Especimen especimen = new Especimen();
            especimen.setNombre(nombre.getValue().trim());
            especimen.setEspecie(especie.getValue());
            especimen.setFechaIngreso(fechaIngreso.getValue());
            especimen.setEstadoSalud(estadoSalud.getValue());
            especimen.setObservacionSalud(observacion.getValue().isBlank() ? null : observacion.getValue().trim());
            return especimen;
        }
    }
}
