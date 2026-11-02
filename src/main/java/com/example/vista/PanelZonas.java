package com.example.vista;

import com.example.modelo.Especie;
import com.example.modelo.Habitat;
import com.example.modelo.Itinerario;
import com.example.modelo.Zona;
import com.example.servicio.EspecieService;
import com.example.servicio.ItinerarioService;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel de la pantalla Zonas: un bloque por zona, con tamano proporcional a su extension.
 * Cada bloque muestra las especies que viven ahi, sus habitats y los itinerarios que la recorren.
 * Se puede elegir un itinerario para resaltar su recorrido, numerando las zonas en orden.
 */
public class PanelZonas extends VerticalLayout {

    private static final String[] COLORES = {
        "#c8e6c9", "#bbdefb", "#ffe0b2", "#d1c4e9", "#f8bbd0", "#b2dfdb", "#fff9c4", "#d7ccc8"
    };

    private final ZonaService zonaService;
    private final EspecieService especieService;
    private final ItinerarioService itinerarioService;

    private final Div mapa = new Div();
    private final ComboBox<Itinerario> selector = new ComboBox<>("Resaltar un itinerario");

    private List<Zona> zonas = List.of();
    private List<Especie> especies = List.of();
    private List<Itinerario> itinerarios = List.of();

    public PanelZonas(ZonaService zonaService, EspecieService especieService,
            ItinerarioService itinerarioService) {
        this.zonaService = zonaService;
        this.especieService = especieService;
        this.itinerarioService = itinerarioService;

        add(new Paragraph("Cada bloque es una zona; cuanto más grande el bloque, más extensión tiene."));

        selector.setItemLabelGenerator(it -> "Itinerario " + it.getCodigo());
        selector.setClearButtonVisible(true);
        selector.setPlaceholder("Todos");
        selector.addValueChangeListener(evento -> dibujar());
        add(selector);

        mapa.getStyle().set("display", "flex").set("flex-wrap", "wrap").set("gap", "12px").set("width", "100%");
        add(mapa);
        setPadding(false);
    }

    /** Vuelve a pedir los datos al backend y redibuja los bloques. */
    public void recargar() {
        try {
            zonas = zonaService.listar();
            especies = especieService.listar();
            itinerarios = itinerarioService.listar();
        } catch (RuntimeException e) {
            Notification.show("No se pudo cargar el detalle de las zonas. Verificá que el backend esté funcionando.");
            return;
        }
        selector.setItems(itinerarios);
        dibujar();
    }

    private void dibujar() {
        mapa.removeAll();
        if (zonas.isEmpty()) {
            mapa.add(new Paragraph("Todavía no hay zonas cargadas."));
            return;
        }
        Itinerario elegido = selector.getValue();
        List<Long> recorrido = new ArrayList<>();
        if (elegido != null && elegido.getZonas() != null) {
            for (Zona z : elegido.getZonas()) {
                recorrido.add(z.getId());
            }
        }

        int indice = 0;
        for (Zona zona : zonas) {
            mapa.add(bloque(zona, COLORES[indice % COLORES.length], elegido != null, recorrido));
            indice++;
        }
    }

    private Div bloque(Zona zona, String color, boolean hayResaltado, List<Long> recorrido) {
        double extension = zona.getExtension() == null || zona.getExtension() <= 0 ? 1 : zona.getExtension();
        int posicion = recorrido.indexOf(zona.getId());
        boolean enRecorrido = posicion >= 0;

        Div caja = new Div();
        caja.getStyle().set("flex", extension + " " + extension + " 240px")
                .set("min-width", "240px")
                .set("box-sizing", "border-box")
                .set("padding", "14px")
                .set("border-radius", "12px")
                .set("background", color)
                .set("color", "#1b2b1b")
                .set("border", enRecorrido ? "4px solid #1b5e20" : "2px solid rgba(0,0,0,0.15)")
                .set("opacity", hayResaltado && !enRecorrido ? "0.35" : "1");

        Span titulo = new Span((enRecorrido ? (posicion + 1) + ". " : "") + zona.getNombre());
        titulo.getStyle().set("font-weight", "700").set("font-size", "1.15em").set("display", "block");
        Span medida = new Span(Formatos.metrosCuadrados(zona.getExtension()));
        medida.getStyle().set("font-size", "0.85em").set("opacity", "0.8").set("display", "block");
        caja.add(titulo, medida);

        // Especies de la zona y sus habitats.
        Set<String> nombresHabitats = new LinkedHashSet<>();
        Div listaEspecies = new Div();
        listaEspecies.getStyle().set("margin-top", "8px");
        int cantidad = 0;
        for (Especie especie : especies) {
            if (especie.getZona() == null || !zona.getId().equals(especie.getZona().getId())) {
                continue;
            }
            cantidad++;
            int ejemplares = especie.getCantidadEjemplares() == null ? 0 : especie.getCantidadEjemplares();
            Div linea = new Div(new Span("• " + especie.getNombreEspanol() + " (" + ejemplares + ")"));
            listaEspecies.add(linea);
            if (especie.getHabitats() != null) {
                for (Habitat h : especie.getHabitats()) {
                    nombresHabitats.add(h.getNombre());
                }
            }
        }
        if (cantidad == 0) {
            Span vacio = new Span("Sin especies cargadas");
            vacio.getStyle().set("font-style", "italic").set("opacity", "0.7");
            listaEspecies.add(vacio);
        }
        caja.add(listaEspecies);

        if (!nombresHabitats.isEmpty()) {
            Div habitats = new Div(new Span("Hábitats: " + String.join(", ", nombresHabitats)));
            habitats.getStyle().set("margin-top", "8px").set("font-size", "0.85em");
            caja.add(habitats);
        }

        // Itinerarios que pasan por la zona.
        List<String> codigos = new ArrayList<>();
        for (Itinerario it : itinerarios) {
            if (it.getZonas() == null) {
                continue;
            }
            for (Zona z : it.getZonas()) {
                if (zona.getId().equals(z.getId())) {
                    codigos.add(it.getCodigo());
                    break;
                }
            }
        }
        if (!codigos.isEmpty()) {
            Div recorridos = new Div(new Span("Itinerarios: " + String.join(", ", codigos)));
            recorridos.getStyle().set("margin-top", "4px").set("font-size", "0.85em");
            caja.add(recorridos);
        }
        return caja;
    }
}
