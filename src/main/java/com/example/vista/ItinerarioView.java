package com.example.vista;

import com.example.modelo.Itinerario;
import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.ItinerarioService;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Route(value = "itinerarios", layout = MainLayout.class)
@PageTitle("Itinerarios | EcoParque")
public class ItinerarioView extends VistaCrud<Itinerario> {

    private final ZonaService zonaService;

    public ItinerarioView(ItinerarioService servicio, ZonaService zonaService, SesionUsuario sesion) {
        super(Itinerario.class, servicio, sesion);
        this.zonaService = zonaService;
    }

    @Override
    protected String titulo() {
        return "Gestión de Itinerarios";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nuevo itinerario";
    }

    @Override
    protected String textoModificar() {
        return "Modificar itinerario";
    }

    @Override
    protected String describir(Itinerario itinerario) {
        return itinerario.getCodigo();
    }

    @Override
    protected void configurarColumnas(Grid<Itinerario> tabla) {
        tabla.addColumn(Itinerario::getCodigo).setHeader("Código").setSortable(true);
        tabla.addColumn(Itinerario::getDuracionMinutos).setHeader("Duración (min)");
        tabla.addColumn(Itinerario::getLongitudMetros).setHeader("Longitud (m)");
        tabla.addColumn(Itinerario::getMaximoVisitantes).setHeader("Máx. visitantes");
        tabla.addColumn(Itinerario::getCantidadEspecies).setHeader("Cant. especies");
        tabla.addColumn(itinerario -> Formatos.unir(itinerario.getZonas(), Zona::getNombre))
                .setHeader("Zonas");
    }

    @Override
    protected Formulario<Itinerario> crearFormulario(Itinerario existente) {
        return new FormularioItinerario(existente, zonaService.listar());
    }

    private static class FormularioItinerario implements Formulario<Itinerario> {

        private final TextField codigo = new TextField("Código");
        private final IntegerField duracion = new IntegerField("Duración (minutos)");
        private final NumberField longitud = new NumberField("Longitud (metros)");
        private final IntegerField maximoVisitantes = new IntegerField("Máximo de visitantes");
        private final IntegerField cantidadEspecies = new IntegerField("Cantidad de especies");
        private final CheckboxGroup<Zona> zonas = new CheckboxGroup<>("Zonas que recorre");

        FormularioItinerario(Itinerario existente, List<Zona> todasLasZonas) {
            zonas.setItems(todasLasZonas);
            zonas.setItemLabelGenerator(Zona::getNombre);

            if (existente != null) {
                codigo.setValue(existente.getCodigo() == null ? "" : existente.getCodigo());
                if (existente.getDuracionMinutos() != null) {
                    duracion.setValue(existente.getDuracionMinutos());
                }
                if (existente.getLongitudMetros() != null) {
                    longitud.setValue(existente.getLongitudMetros());
                }
                if (existente.getMaximoVisitantes() != null) {
                    maximoVisitantes.setValue(existente.getMaximoVisitantes());
                }
                if (existente.getCantidadEspecies() != null) {
                    cantidadEspecies.setValue(existente.getCantidadEspecies());
                }
                if (existente.getZonas() != null) {
                    zonas.setValue(new HashSet<>(existente.getZonas()));
                }
            }
        }

        @Override
        public Component[] campos() {
            return new Component[]{codigo, duracion, longitud, maximoVisitantes, cantidadEspecies, zonas};
        }

        @Override
        public String validar() {
            if (codigo.isEmpty() || duracion.isEmpty() || longitud.isEmpty() || maximoVisitantes.isEmpty()) {
                return "Completá el código, la duración, la longitud y el máximo de visitantes.";
            }
            if (duracion.getValue() <= 0 || longitud.getValue() <= 0 || maximoVisitantes.getValue() <= 0) {
                return "La duración, la longitud y el máximo de visitantes tienen que ser mayores a cero.";
            }
            return null;
        }

        @Override
        public Itinerario construir() {
            Itinerario itinerario = new Itinerario();
            itinerario.setCodigo(codigo.getValue().trim());
            itinerario.setDuracionMinutos(duracion.getValue());
            itinerario.setLongitudMetros(longitud.getValue());
            itinerario.setMaximoVisitantes(maximoVisitantes.getValue());
            itinerario.setCantidadEspecies(cantidadEspecies.isEmpty() ? 0 : cantidadEspecies.getValue());
            itinerario.setZonas(new ArrayList<>(zonas.getValue()));
            return itinerario;
        }
    }
}
