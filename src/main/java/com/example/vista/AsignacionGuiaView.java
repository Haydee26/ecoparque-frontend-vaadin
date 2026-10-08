package com.example.vista;

import com.example.modelo.AsignacionGuia;
import com.example.modelo.Guia;
import com.example.modelo.Itinerario;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.AsignacionGuiaService;
import com.example.servicio.GuiaService;
import com.example.servicio.ItinerarioService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Route(value = "asignaciones-guia", layout = MainLayout.class)
@PageTitle("Asignaciones de guías | EcoParque")
public class AsignacionGuiaView extends VistaCrud<AsignacionGuia> {

    private final GuiaService guiaService;
    private final ItinerarioService itinerarioService;

    public AsignacionGuiaView(AsignacionGuiaService servicio, GuiaService guiaService,
            ItinerarioService itinerarioService, SesionUsuario sesion) {
        super(AsignacionGuia.class, servicio, sesion);
        this.guiaService = guiaService;
        this.itinerarioService = itinerarioService;
    }

    @Override
    protected String titulo() {
        return "Asignaciones de guías a itinerarios";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Asignar guía a itinerario";
    }

    @Override
    protected String textoModificar() {
        return "Modificar asignación";
    }

    @Override
    protected String describir(AsignacionGuia asignacion) {
        String guia = asignacion.getGuia() == null ? "" : asignacion.getGuia().getNombre();
        String itinerario = asignacion.getItinerario() == null ? "" : asignacion.getItinerario().getCodigo();
        return guia + " en el itinerario " + itinerario;
    }

    @Override
    protected void configurarColumnas(Grid<AsignacionGuia> tabla) {
        tabla.addColumn(asignacion -> asignacion.getGuia() == null ? "" : asignacion.getGuia().getNombre())
                .setHeader("Guía").setSortable(true);
        tabla.addColumn(asignacion -> asignacion.getItinerario() == null ? "" : asignacion.getItinerario().getCodigo())
                .setHeader("Itinerario");
        tabla.addColumn(asignacion -> Formatos.hora(asignacion.getHora())).setHeader("Hora");
    }

    @Override
    protected Formulario<AsignacionGuia> crearFormulario(AsignacionGuia existente) {
        return new FormularioAsignacion(existente, guiaService.listar(), itinerarioService.listar());
    }

    private static class FormularioAsignacion implements Formulario<AsignacionGuia> {

        private final ComboBox<Guia> guia = new ComboBox<>("Guía");
        private final ComboBox<Itinerario> itinerario = new ComboBox<>("Itinerario");
        private final TimePicker hora = new TimePicker("Hora");

        FormularioAsignacion(AsignacionGuia existente, List<Guia> guias, List<Itinerario> itinerarios) {
            guia.setItems(guias);
            guia.setItemLabelGenerator(Guia::getNombre);
            itinerario.setItems(itinerarios);
            itinerario.setItemLabelGenerator(Itinerario::getCodigo);

            if (existente != null) {
                guia.setValue(existente.getGuia());
                itinerario.setValue(existente.getItinerario());
                cargarHora(existente.getHora());
            }
        }

        private void cargarHora(String texto) {
            if (texto == null || texto.isBlank()) {
                return;
            }
            try {
                hora.setValue(LocalTime.parse(texto));
            } catch (DateTimeParseException e) {
                // si la hora guardada no se puede leer, el campo queda vacio
            }
        }

        @Override
        public Component[] campos() {
            return new Component[]{guia, itinerario, hora};
        }

        @Override
        public String validar() {
            if (guia.isEmpty() || itinerario.isEmpty() || hora.isEmpty()) {
                return "Elegí el guía, el itinerario y la hora.";
            }
            return null;
        }

        @Override
        public AsignacionGuia construir() {
            AsignacionGuia asignacion = new AsignacionGuia();
            asignacion.setGuia(guia.getValue());
            asignacion.setItinerario(itinerario.getValue());
            asignacion.setHora(hora.getValue().toString());
            return asignacion;
        }
    }
}
