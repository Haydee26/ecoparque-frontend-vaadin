package com.example.vista;

import com.example.modelo.Zona;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import java.util.List;

/**
 * Mini mapa para elegir el lugar de una zona al cargarla o modificarla: muestra los casilleros
 * ocupados (con el nombre de cada zona) y los libres. Tocando un casillero libre se completan
 * columna y fila; el rectángulo elegido se pinta de verde si entra y de rojo si se pisa con otra
 * zona o se sale del mapa. "Buscar lugar libre" encuentra solo el primer hueco del tamaño pedido.
 */
class SelectorDeLugar extends VerticalLayout {

    private static final int LADO = 44;
    private static final String[] COLORES = {"#e9c46a", "#8ecae6", "#b5d99c", "#f4a6a6", "#c8b6e2", "#f6bd87"};

    private final List<Zona> otras;
    private final IntegerField columna;
    private final IntegerField fila;
    private final IntegerField ancho;
    private final IntegerField alto;

    private final Div cuadricula = new Div();
    private final Span mensaje = new Span();
    private boolean cambiandoCampos = false;

    /**
     * @param otras zonas que ya ocupan lugar (sin la que se está modificando)
     */
    SelectorDeLugar(List<Zona> otras, IntegerField columna, IntegerField fila, IntegerField ancho, IntegerField alto) {
        this.otras = otras;
        this.columna = columna;
        this.fila = fila;
        this.ancho = ancho;
        this.alto = alto;
        setPadding(false);
        setSpacing(false);

        cuadricula.getStyle().set("display", "grid")
                .set("grid-template-columns", "repeat(" + MapaParqueView.COLUMNAS + ", " + LADO + "px)")
                .set("grid-template-rows", "repeat(" + MapaParqueView.FILAS + ", " + LADO + "px)")
                .set("gap", "2px").set("padding", "6px").set("background", "#dfe9d5")
                .set("border-radius", "8px").set("width", "fit-content");

        Button buscar = new Button("Buscar lugar libre", evento -> buscarLugarLibre());
        Button quitar = new Button("Dejar sin ubicar", evento -> {
            cambiarCampos(null, null, null, null);
        });
        HorizontalLayout botones = new HorizontalLayout(buscar, quitar);
        botones.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        mensaje.getStyle().set("font-size", "var(--lumo-font-size-s)");
        Span ayuda = new Span("Tocá un casillero libre para poner ahí la esquina superior izquierda de la zona.");
        ayuda.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");

        add(ayuda, cuadricula, mensaje, botones);

        for (IntegerField campo : new IntegerField[]{columna, fila, ancho, alto}) {
            campo.addValueChangeListener(evento -> {
                if (!cambiandoCampos) {
                    dibujar();
                }
            });
        }
        dibujar();
    }

    // ------------------------------------------------ dibujo

    private void dibujar() {
        cuadricula.removeAll();
        Zona[][] ocupado = new Zona[MapaParqueView.FILAS + 1][MapaParqueView.COLUMNAS + 1];
        for (Zona zona : otras) {
            if (!zona.ubicada()) {
                continue;
            }
            for (int f = zona.getFila(); f < zona.getFila() + zona.getAlto(); f++) {
                for (int c = zona.getColumna(); c < zona.getColumna() + zona.getAncho(); c++) {
                    if (f >= 1 && f <= MapaParqueView.FILAS && c >= 1 && c <= MapaParqueView.COLUMNAS) {
                        ocupado[f][c] = zona;
                    }
                }
            }
        }

        boolean elegida = tieneRectangulo();
        String problema = elegida ? problema(ocupado) : null;
        String colorSeleccion = problema == null ? "#2e9e5b" : "#d64545";

        for (int f = 1; f <= MapaParqueView.FILAS; f++) {
            for (int c = 1; c <= MapaParqueView.COLUMNAS; c++) {
                final int col = c;
                final int fil = f;
                Div casillero = new Div();
                casillero.getStyle().set("box-sizing", "border-box").set("border-radius", "4px")
                        .set("font-size", "10px").set("line-height", "11px").set("overflow", "hidden")
                        .set("padding", "2px").set("color", "#33291a");
                Zona zona = ocupado[f][c];
                if (zona != null) {
                    casillero.getStyle().set("background", COLORES[Math.floorMod(indiceDe(zona), COLORES.length)]);
                    casillero.getElement().setProperty("title", zona.getNombre() + " (ocupado)");
                    if (c == zona.getColumna() && f == zona.getFila()) {
                        casillero.setText(zona.getNombre());
                        casillero.getStyle().set("font-weight", "600");
                    }
                } else {
                    casillero.getStyle().set("background", "#f7fbf2").set("border", "1px dashed #9bb48a")
                            .set("cursor", "pointer");
                    casillero.getElement().setProperty("title", "Libre: columna " + c + ", fila " + f);
                    casillero.addClickListener(evento -> elegirCasillero(col, fil));
                }
                if (elegida && dentroDelRectangulo(c, f)) {
                    casillero.getStyle().set("outline", "3px solid " + colorSeleccion).set("outline-offset", "-3px");
                    casillero.getStyle().set("box-shadow", "inset 0 0 0 100px " + colorSeleccion + "33");
                }
                cuadricula.add(casillero);
            }
        }

        if (!elegida) {
            mensaje.setText("Todavía no elegiste lugar: la zona quedará sin ubicar en el mapa.");
            mensaje.getStyle().set("color", "var(--lumo-secondary-text-color)");
        } else if (problema == null) {
            mensaje.setText("✔ Ese lugar está libre.");
            mensaje.getStyle().set("color", "#2e7d4f");
        } else {
            mensaje.setText("✖ " + problema);
            mensaje.getStyle().set("color", "#c0392b");
        }
    }

    private int indiceDe(Zona zona) {
        return zona.getId() == null ? 0 : zona.getId().intValue();
    }

    private boolean tieneRectangulo() {
        return !columna.isEmpty() && !fila.isEmpty() && !ancho.isEmpty() && !alto.isEmpty();
    }

    private boolean dentroDelRectangulo(int c, int f) {
        return c >= columna.getValue() && c < columna.getValue() + ancho.getValue()
                && f >= fila.getValue() && f < fila.getValue() + alto.getValue();
    }

    /** Devuelve qué está mal con el rectángulo elegido, o null si entra. */
    private String problema(Zona[][] ocupado) {
        int c0 = columna.getValue();
        int f0 = fila.getValue();
        int a = ancho.getValue();
        int h = alto.getValue();
        if (c0 < 1 || f0 < 1 || a < 1 || h < 1) {
            return "Columna, fila, ancho y alto tienen que ser 1 o más.";
        }
        if (c0 + a - 1 > MapaParqueView.COLUMNAS || f0 + h - 1 > MapaParqueView.FILAS) {
            return "Se sale del mapa (tiene " + MapaParqueView.COLUMNAS + " columnas y " + MapaParqueView.FILAS + " filas).";
        }
        for (int f = f0; f < f0 + h; f++) {
            for (int c = c0; c < c0 + a; c++) {
                if (ocupado[f][c] != null) {
                    return "Se superpone con " + ocupado[f][c].getNombre() + ".";
                }
            }
        }
        return null;
    }

    // ------------------------------------------------ acciones

    private void elegirCasillero(int c, int f) {
        int a = ancho.isEmpty() ? 2 : ancho.getValue();
        int h = alto.isEmpty() ? 2 : alto.getValue();
        cambiarCampos(c, f, a, h);
    }

    private void buscarLugarLibre() {
        int a = ancho.isEmpty() ? 2 : ancho.getValue();
        int h = alto.isEmpty() ? 2 : alto.getValue();
        if (a < 1 || h < 1) {
            Notification.show("Ancho y alto tienen que ser 1 o más.", 3000, Notification.Position.MIDDLE);
            return;
        }
        for (int f = 1; f + h - 1 <= MapaParqueView.FILAS; f++) {
            for (int c = 1; c + a - 1 <= MapaParqueView.COLUMNAS; c++) {
                if (entra(c, f, a, h)) {
                    cambiarCampos(c, f, a, h);
                    return;
                }
            }
        }
        Notification.show("No hay lugar libre para una zona de " + a + "x" + h
                + " casilleros. Probá con un tamaño más chico.", 4000, Notification.Position.MIDDLE);
    }

    private boolean entra(int c0, int f0, int a, int h) {
        for (Zona zona : otras) {
            if (!zona.ubicada()) {
                continue;
            }
            boolean separadasEnColumna = c0 + a <= zona.getColumna() || zona.getColumna() + zona.getAncho() <= c0;
            boolean separadasEnFila = f0 + h <= zona.getFila() || zona.getFila() + zona.getAlto() <= f0;
            if (!separadasEnColumna && !separadasEnFila) {
                return false;
            }
        }
        return true;
    }

    private void cambiarCampos(Integer c, Integer f, Integer a, Integer h) {
        cambiandoCampos = true;
        columna.setValue(c);
        fila.setValue(f);
        ancho.setValue(a);
        alto.setValue(h);
        cambiandoCampos = false;
        dibujar();
    }

    /** Dice si el rectángulo cargado entra (para frenar el guardado antes de llamar al servidor). */
    String problemaActual() {
        if (!tieneRectangulo()) {
            return null;
        }
        Zona[][] ocupado = new Zona[MapaParqueView.FILAS + 1][MapaParqueView.COLUMNAS + 1];
        for (Zona zona : otras) {
            if (!zona.ubicada()) {
                continue;
            }
            for (int f = zona.getFila(); f < zona.getFila() + zona.getAlto(); f++) {
                for (int c = zona.getColumna(); c < zona.getColumna() + zona.getAncho(); c++) {
                    if (f >= 1 && f <= MapaParqueView.FILAS && c >= 1 && c <= MapaParqueView.COLUMNAS) {
                        ocupado[f][c] = zona;
                    }
                }
            }
        }
        return problema(ocupado);
    }
}
