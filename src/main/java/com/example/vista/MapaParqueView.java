package com.example.vista;

import com.example.modelo.Especie;
import com.example.modelo.Habitat;
import com.example.modelo.Itinerario;
import com.example.modelo.Zona;
import com.example.servicio.EspecieService;
import com.example.servicio.ItinerarioService;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Mapa dibujado del parque. El terreno es una cuadricula de COLUMNAS x FILAS casilleros;
 * cada zona ocupa un rectangulo de casilleros. Los casilleros libres se ven punteados:
 * ahi se pueden ubicar zonas nuevas (columna, fila, ancho y alto en la pantalla Zonas).
 */
@Route(value = "mapa", layout = MainLayout.class)
@PageTitle("Mapa del parque | EcoParque")
public class MapaParqueView extends VerticalLayout {

    /** Tamano de la cuadricula; tiene que coincidir con el del backend (ZonaService). */
    public static final int COLUMNAS = 10;
    public static final int FILAS = 6;

    private final ZonaService zonaService;
    private final EspecieService especieService;
    private final ItinerarioService itinerarioService;

    private final Div contenedor = new Div();
    private final Div sinUbicar = new Div();

    private List<Zona> zonas = List.of();
    private List<Especie> especies = List.of();
    private List<Itinerario> itinerarios = List.of();

    public MapaParqueView(ZonaService zonaService, EspecieService especieService,
            ItinerarioService itinerarioService) {
        this.zonaService = zonaService;
        this.especieService = especieService;
        this.itinerarioService = itinerarioService;

        add(new H2("Mapa del parque"),
                new Paragraph("Tocá una zona para ver sus especies, hábitats e itinerarios. "
                        + "Los casilleros punteados son terreno libre para zonas nuevas."));
        contenedor.getStyle().set("width", "100%").set("max-width", "1000px");
        add(contenedor, sinUbicar);
    }

    @Override
    protected void onAttach(AttachEvent evento) {
        super.onAttach(evento);
        try {
            zonas = zonaService.listar();
            especies = especieService.listar();
            itinerarios = itinerarioService.listar();
        } catch (RuntimeException e) {
            Notification.show("No se pudo cargar el mapa. Verificá que el backend esté funcionando.");
            return;
        }
        dibujar();
    }

    private void dibujar() {
        contenedor.removeAll();
        sinUbicar.removeAll();

        // Terreno: cuadricula con aspecto de parque (cesped y cerco).
        Div terreno = new Div();
        terreno.getStyle().set("display", "grid")
                .set("grid-template-columns", "repeat(" + COLUMNAS + ", 1fr)")
                .set("grid-auto-rows", "92px")
                .set("gap", "6px")
                .set("padding", "14px")
                .set("border-radius", "18px")
                .set("border", "6px solid #8d6e63")
                .set("background", "linear-gradient(135deg, #dcedc8, #c5e1a5)");

        boolean[][] ocupado = new boolean[FILAS + 1][COLUMNAS + 1];
        List<Zona> pendientes = new ArrayList<>();
        for (Zona zona : zonas) {
            if (!zona.ubicada() || !entraEnElMapa(zona)) {
                pendientes.add(zona);
                continue;
            }
            for (int f = zona.getFila(); f < zona.getFila() + zona.getAlto(); f++) {
                for (int c = zona.getColumna(); c < zona.getColumna() + zona.getAncho(); c++) {
                    ocupado[f][c] = true;
                }
            }
            terreno.add(bloque(zona));
        }

        for (int f = 1; f <= FILAS; f++) {
            for (int c = 1; c <= COLUMNAS; c++) {
                if (!ocupado[f][c]) {
                    terreno.add(casilleroLibre(c, f));
                }
            }
        }
        contenedor.add(terreno);

        Div entrada = new Div(new Span("🚪 Entrada del parque"));
        entrada.getStyle().set("text-align", "center").set("margin-top", "8px").set("font-weight", "600")
                .set("padding", "6px").set("border-radius", "10px").set("background", "#efebe9");
        contenedor.add(entrada);

        if (!pendientes.isEmpty()) {
            sinUbicar.add(new H3("Zonas sin ubicar en el mapa"));
            for (Zona zona : pendientes) {
                sinUbicar.add(new Div(new Span("• " + zona.getNombre()
                        + " (asignale columna, fila, ancho y alto en la pantalla Zonas)")));
            }
        }
    }

    private boolean entraEnElMapa(Zona z) {
        return z.getColumna() >= 1 && z.getFila() >= 1 && z.getAncho() >= 1 && z.getAlto() >= 1
                && z.getColumna() + z.getAncho() - 1 <= COLUMNAS
                && z.getFila() + z.getAlto() - 1 <= FILAS;
    }

    private Div casilleroLibre(int columna, int fila) {
        Div celda = new Div(new Span("C" + columna + " · F" + fila));
        celda.getStyle().set("grid-column", String.valueOf(columna))
                .set("grid-row", String.valueOf(fila))
                .set("border", "2px dashed rgba(56, 100, 40, 0.45)")
                .set("border-radius", "10px")
                .set("font-size", "0.7em")
                .set("color", "rgba(40, 80, 30, 0.7)")
                .set("display", "flex").set("align-items", "flex-end").set("justify-content", "flex-end")
                .set("padding", "4px").set("box-sizing", "border-box");
        return celda;
    }

    private Div bloque(Zona zona) {
        Tema tema = Tema.de(zona.getNombre());
        int cantidadEspecies = 0;
        int ejemplares = 0;
        for (Especie especie : especies) {
            if (especie.getZona() != null && zona.getId().equals(especie.getZona().getId())) {
                cantidadEspecies++;
                ejemplares += especie.getCantidadEjemplares() == null ? 0 : especie.getCantidadEjemplares();
            }
        }

        Span icono = new Span(tema.icono);
        icono.getStyle().set("font-size", "2em").set("line-height", "1");
        Span nombre = new Span(zona.getNombre());
        nombre.getStyle().set("font-weight", "700").set("font-size", "1.05em").set("display", "block");
        Span datos = new Span(cantidadEspecies + (cantidadEspecies == 1 ? " especie" : " especies")
                + " · " + ejemplares + " ejemplares");
        datos.getStyle().set("font-size", "0.8em").set("display", "block");
        Span medida = new Span(Formatos.metrosCuadrados(zona.getExtension()));
        medida.getStyle().set("font-size", "0.75em").set("opacity", "0.85").set("display", "block");

        Div caja = new Div(icono, nombre, datos, medida);
        caja.getStyle().set("grid-column", zona.getColumna() + " / span " + zona.getAncho())
                .set("grid-row", zona.getFila() + " / span " + zona.getAlto())
                .set("background", tema.fondo)
                .set("color", tema.texto)
                .set("border-radius", "14px")
                .set("padding", "10px")
                .set("box-sizing", "border-box")
                .set("cursor", "pointer")
                .set("box-shadow", "0 3px 8px rgba(0,0,0,0.25)")
                .set("overflow", "hidden");
        caja.addClickListener(evento -> mostrarDetalle(zona));
        return caja;
    }

    private void mostrarDetalle(Zona zona) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle(zona.getNombre());

        VerticalLayout cuerpo = new VerticalLayout();
        cuerpo.setPadding(false);
        cuerpo.add(new Span("Extensión: " + Formatos.metrosCuadrados(zona.getExtension())));

        cuerpo.add(new H3("Especies"));
        Set<String> habitats = new LinkedHashSet<>();
        boolean hay = false;
        for (Especie especie : especies) {
            if (especie.getZona() == null || !zona.getId().equals(especie.getZona().getId())) {
                continue;
            }
            hay = true;
            int cantidad = especie.getCantidadEjemplares() == null ? 0 : especie.getCantidadEjemplares();
            cuerpo.add(new Span("• " + especie.getNombreEspanol() + " (" + cantidad + " ejemplares)"));
            if (especie.getHabitats() != null) {
                for (Habitat h : especie.getHabitats()) {
                    habitats.add(h.getNombre());
                }
            }
        }
        if (!hay) {
            cuerpo.add(new Span("Todavía no hay especies en esta zona."));
        }

        cuerpo.add(new H3("Hábitats"));
        cuerpo.add(new Span(habitats.isEmpty() ? "Sin hábitats asociados." : String.join(", ", habitats)));

        cuerpo.add(new H3("Itinerarios que la recorren"));
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
        cuerpo.add(new Span(codigos.isEmpty() ? "Ningún itinerario pasa por esta zona." : String.join(", ", codigos)));

        dialogo.add(cuerpo);
        dialogo.getFooter().add(new Button("Cerrar", evento -> dialogo.close()));
        dialogo.open();
    }

    /** Aspecto de cada zona segun su nombre: icono, degradado de fondo y color de texto. */
    private static class Tema {

        final String icono;
        final String fondo;
        final String texto;

        Tema(String icono, String fondo, String texto) {
            this.icono = icono;
            this.fondo = fondo;
            this.texto = texto;
        }

        static Tema de(String nombre) {
            String n = nombre == null ? "" : nombre.toLowerCase();
            if (n.contains("sabana")) {
                return new Tema("🦁", "linear-gradient(135deg, #f9a825, #c77800)", "#2b1a00");
            }
            if (n.contains("selva")) {
                return new Tema("🌴", "linear-gradient(135deg, #2e7d32, #1b4d20)", "#ffffff");
            }
            if (n.contains("bosque")) {
                return new Tema("🌲", "linear-gradient(135deg, #558b2f, #33691e)", "#ffffff");
            }
            if (n.contains("desierto")) {
                return new Tema("🐪", "linear-gradient(135deg, #ffe082, #e0b24a)", "#3b2a00");
            }
            if (n.contains("humedal") || n.contains("laguna")) {
                return new Tema("🦩", "linear-gradient(135deg, #4dd0e1, #00838f)", "#00262b");
            }
            if (n.contains("monta")) {
                return new Tema("⛰️", "linear-gradient(135deg, #90a4ae, #546e7a)", "#ffffff");
            }
            return new Tema("🌿", "linear-gradient(135deg, #a5d6a7, #66bb6a)", "#10300f");
        }
    }
}
