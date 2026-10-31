package com.example.vista;

import com.example.modelo.Cuidador;
import com.example.modelo.Especie;
import com.example.modelo.ResponsabilidadCuidador;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.CuidadorService;
import com.example.servicio.EspecieService;
import com.example.servicio.ResponsabilidadCuidadorService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Route(value = "responsabilidades-cuidador", layout = MainLayout.class)
@PageTitle("Responsabilidades | EcoParque")
public class ResponsabilidadCuidadorView extends VistaCrud<ResponsabilidadCuidador> {

    private final CuidadorService cuidadorService;
    private final EspecieService especieService;

    public ResponsabilidadCuidadorView(ResponsabilidadCuidadorService servicio, CuidadorService cuidadorService,
            EspecieService especieService, SesionUsuario sesion) {
        super(ResponsabilidadCuidador.class, servicio, sesion);
        this.cuidadorService = cuidadorService;
        this.especieService = especieService;
    }

    @Override
    protected String titulo() {
        return "Cuidadores a cargo de especies";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Asignar cuidador a especie";
    }

    @Override
    protected String textoModificar() {
        return "Modificar responsabilidad";
    }

    @Override
    protected String describir(ResponsabilidadCuidador responsabilidad) {
        String cuidador = responsabilidad.getCuidador() == null ? "" : responsabilidad.getCuidador().getNombre();
        String especie = responsabilidad.getEspecie() == null ? "" : responsabilidad.getEspecie().getNombreEspanol();
        return cuidador + " a cargo de " + especie;
    }

    @Override
    protected void configurarColumnas(Grid<ResponsabilidadCuidador> tabla) {
        tabla.addColumn(r -> r.getCuidador() == null ? "" : r.getCuidador().getNombre())
                .setHeader("Cuidador").setSortable(true);
        tabla.addColumn(r -> r.getEspecie() == null ? "" : r.getEspecie().getNombreEspanol())
                .setHeader("Especie");
        tabla.addColumn(r -> Formatos.fecha(r.getFechaDesde())).setHeader("A cargo desde");
        tabla.addColumn(r -> r.getFechaHasta() == null || r.getFechaHasta().isBlank()
                ? "Vigente" : Formatos.fecha(r.getFechaHasta()))
                .setHeader("A cargo hasta");
    }

    @Override
    protected Formulario<ResponsabilidadCuidador> crearFormulario(ResponsabilidadCuidador existente) {
        return new FormularioResponsabilidad(existente, cuidadorService.listar(), especieService.listar());
    }

    private static class FormularioResponsabilidad implements Formulario<ResponsabilidadCuidador> {

        private final ComboBox<Cuidador> cuidador = new ComboBox<>("Cuidador");
        private final ComboBox<Especie> especie = new ComboBox<>("Especie");
        private final DatePicker fechaDesde = new DatePicker("A cargo desde");
        private final DatePicker fechaHasta = new DatePicker("A cargo hasta (opcional)");

        FormularioResponsabilidad(ResponsabilidadCuidador existente, List<Cuidador> cuidadores, List<Especie> especies) {
            cuidador.setItems(cuidadores);
            cuidador.setItemLabelGenerator(Cuidador::getNombre);
            especie.setItems(especies);
            especie.setItemLabelGenerator(Especie::getNombreEspanol);

            if (existente != null) {
                cuidador.setValue(existente.getCuidador());
                especie.setValue(existente.getEspecie());
                cargarFecha(fechaDesde, existente.getFechaDesde());
                cargarFecha(fechaHasta, existente.getFechaHasta());
            }
        }

        private void cargarFecha(DatePicker campo, String iso) {
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
            return new Component[]{cuidador, especie, fechaDesde, fechaHasta};
        }

        @Override
        public String validar() {
            if (cuidador.isEmpty() || especie.isEmpty() || fechaDesde.isEmpty()) {
                return "Elegí el cuidador, la especie y la fecha desde.";
            }
            if (!fechaHasta.isEmpty() && fechaHasta.getValue().isBefore(fechaDesde.getValue())) {
                return "La fecha hasta no puede ser anterior a la fecha desde.";
            }
            return null;
        }

        @Override
        public ResponsabilidadCuidador construir() {
            ResponsabilidadCuidador responsabilidad = new ResponsabilidadCuidador();
            responsabilidad.setCuidador(cuidador.getValue());
            responsabilidad.setEspecie(especie.getValue());
            responsabilidad.setFechaDesde(fechaDesde.getValue().toString());
            responsabilidad.setFechaHasta(fechaHasta.isEmpty() ? null : fechaHasta.getValue().toString());
            return responsabilidad;
        }
    }
}
