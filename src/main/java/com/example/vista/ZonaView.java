package com.example.vista;

import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.EspecieService;
import com.example.servicio.ItinerarioService;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route(value = "zonas", layout = MainLayout.class)
@PageTitle("Zonas | EcoParque")
public class ZonaView extends VistaCrud<Zona> {

    private final PanelZonas panel;
    private final ZonaService zonaServicio;

    public ZonaView(ZonaService servicio, EspecieService especieService,
            ItinerarioService itinerarioService, SesionUsuario sesion) {
        super(Zona.class, servicio, sesion);
        this.zonaServicio = servicio;
        this.panel = new PanelZonas(servicio, especieService, itinerarioService);
    }

    @Override
    protected Component[] componentesSobreLaTabla() {
        return new Component[]{panel};
    }

    @Override
    protected void actualizarTabla() {
        super.actualizarTabla();
        panel.recargar();
    }

    @Override
    protected String titulo() {
        return "Gestión de Zonas";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nueva zona";
    }

    @Override
    protected String textoModificar() {
        return "Modificar zona";
    }

    @Override
    protected String describir(Zona zona) {
        return zona.getNombre();
    }

    @Override
    protected void configurarColumnas(Grid<Zona> tabla) {
        tabla.addColumn(Zona::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(zona -> Formatos.metrosCuadrados(zona.getExtension())).setHeader("Extensión");
        tabla.addColumn(zona -> zona.ubicada()
                ? "Col. " + zona.getColumna() + ", fila " + zona.getFila()
                + " (" + zona.getAncho() + "x" + zona.getAlto() + ")"
                : "Sin ubicar").setHeader("Lugar en el mapa");
    }

    @Override
    protected Formulario<Zona> crearFormulario(Zona existente) {
        // Las demás zonas (sin la que se modifica): ocupan lugar en el mapa del formulario.
        List<Zona> otras = zonaServicio.listar().stream()
                .filter(zona -> existente == null || !java.util.Objects.equals(zona.getId(), existente.getId()))
                .toList();
        return new FormularioZona(existente, otras);
    }

    private static class FormularioZona implements Formulario<Zona> {

        private final TextField nombre = new TextField("Nombre");
        private final NumberField extension = new NumberField("Extensión (m²)");
        private final IntegerField columna = new IntegerField("Columna (1 a " + MapaParqueView.COLUMNAS + ")");
        private final IntegerField fila = new IntegerField("Fila (1 a " + MapaParqueView.FILAS + ")");
        private final IntegerField ancho = new IntegerField("Ancho (casilleros)");
        private final IntegerField alto = new IntegerField("Alto (casilleros)");

        private final SelectorDeLugar selector;

        FormularioZona(Zona existente, List<Zona> otras) {
            if (existente != null) {
                nombre.setValue(existente.getNombre() == null ? "" : existente.getNombre());
                if (existente.getExtension() != null) {
                    extension.setValue(existente.getExtension());
                }
                columna.setValue(existente.getColumna());
                fila.setValue(existente.getFila());
                ancho.setValue(existente.getAncho());
                alto.setValue(existente.getAlto());
            }
            for (IntegerField campo : new IntegerField[]{columna, fila, ancho, alto}) {
                campo.setMin(1);
                campo.setStepButtonsVisible(true);
                campo.setWidth("9em");
            }
            selector = new SelectorDeLugar(otras, columna, fila, ancho, alto);
        }

        @Override
        public Component[] campos() {
            HorizontalLayout datos = new HorizontalLayout(nombre, extension);
            HorizontalLayout medidas = new HorizontalLayout(columna, fila, ancho, alto);
            VerticalLayout contenido = new VerticalLayout(datos, selector, medidas);
            contenido.setPadding(false);
            return new Component[]{contenido};
        }

        @Override
        public String validar() {
            if (nombre.isEmpty() || extension.isEmpty() || extension.getValue() <= 0) {
                return "Completá el nombre y una extensión mayor a cero.";
            }
            int cargados = (columna.isEmpty() ? 0 : 1) + (fila.isEmpty() ? 0 : 1)
                    + (ancho.isEmpty() ? 0 : 1) + (alto.isEmpty() ? 0 : 1);
            if (cargados > 0 && cargados < 4) {
                return "Para ubicar la zona en el mapa completá columna, fila, ancho y alto (o dejá los cuatro vacíos).";
            }
            return selector.problemaActual();
        }

        @Override
        public Zona construir() {
            Zona zona = new Zona();
            zona.setNombre(nombre.getValue().trim());
            zona.setExtension(extension.getValue());
            zona.setColumna(columna.getValue());
            zona.setFila(fila.getValue());
            zona.setAncho(ancho.getValue());
            zona.setAlto(alto.getValue());
            return zona;
        }
    }
}
