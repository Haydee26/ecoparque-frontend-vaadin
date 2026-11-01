package com.example.vista;

import com.example.modelo.Identificable;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.ServicioRest;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

/**
 * Patron Template Method: esta clase arma la pantalla completa (titulo, boton de alta,
 * tabla, dialogo de alta/modificacion y confirmacion de borrado). Cada pantalla concreta
 * solo define lo que cambia: textos, columnas y formulario.
 *
 * Solo el administrador ve los botones de registrar, modificar y eliminar.
 */
public abstract class VistaCrud<T extends Identificable> extends VerticalLayout {

    private static final String COLOR_REGISTRAR = "#7a8c5d";
    private static final String COLOR_MODIFICAR = "#2563eb";
    private static final String COLOR_ELIMINAR = "#c0392b";

    private final ServicioRest<T> servicio;
    private final boolean esAdministrador;
    private final Grid<T> tabla;
    private BuscadorDeTabla<T> buscador;
    private boolean construida = false;

    protected VistaCrud(Class<T> tipo, ServicioRest<T> servicio, SesionUsuario sesion) {
        this.servicio = servicio;
        this.esAdministrador = sesion.esAdministrador();
        this.tabla = new Grid<>(tipo, false);
        setPadding(true);
    }

    // ------------------------------------------------ lo que define cada pantalla

    protected abstract String titulo();

    protected abstract String textoBotonNuevo();

    protected abstract String textoModificar();

    /** Texto corto que identifica al elemento (se usa al confirmar el borrado). */
    protected abstract String describir(T elemento);

    protected abstract void configurarColumnas(Grid<T> tabla);

    /** Las pantallas que no permiten modificar (por ejemplo, usuarios) lo desactivan. */
    protected boolean permiteModificar() {
        return true;
    }

    /** Elementos que muestra la tabla. Las pantallas con busqueda o filtros lo cambian. */
    protected List<T> obtenerElementos() {
        return servicio.listar();
    }

    /** Las pantallas con un buscador propio (como Empleados) lo desactivan. */
    protected boolean usaBuscadorIntegrado() {
        return true;
    }

    /** Componentes extra (por ejemplo un buscador) que se muestran arriba de la tabla. */
    protected Component[] componentesSobreLaTabla() {
        return new Component[0];
    }

    /** existente es null cuando se esta registrando uno nuevo. */
    protected abstract Formulario<T> crearFormulario(T existente);

    // ------------------------------------------------ armado de la pantalla

    /** Se arma al mostrarse, cuando el objeto ya esta completamente construido. */
    @Override
    protected void onAttach(AttachEvent evento) {
        super.onAttach(evento);
        if (!construida) {
            construirPantalla();
            construida = true;
        }
    }

    private void construirPantalla() {
        add(new H2(titulo()));

        if (esAdministrador) {
            Button nuevo = new Button(textoBotonNuevo(), evento -> abrirFormulario(null));
            colorear(nuevo, COLOR_REGISTRAR);
            add(nuevo);
        }

        if (usaBuscadorIntegrado()) {
            buscador = new BuscadorDeTabla<>(tabla);
            add(buscador.campo());
        }
        add(componentesSobreLaTabla());

        configurarColumnas(tabla);
        if (esAdministrador) {
            // Fija a la derecha: con muchas columnas, los botones siempre quedan a la vista.
            tabla.addComponentColumn(this::crearAcciones).setHeader("Acciones")
                    .setAutoWidth(true).setFlexGrow(0).setFrozenToEnd(true);
        }
        // Si un texto es largo, baja a otra línea en vez de quedar cortado.
        tabla.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        tabla.setAllRowsVisible(true);
        add(tabla);

        actualizarTabla();
    }

    private HorizontalLayout crearAcciones(T elemento) {
        HorizontalLayout acciones = new HorizontalLayout();

        if (permiteModificar()) {
            Button modificar = new Button("Modificar", evento -> abrirFormulario(elemento));
            colorear(modificar, COLOR_MODIFICAR);
            acciones.add(modificar);
        }

        Button eliminar = new Button("Eliminar", evento -> confirmarEliminacion(elemento));
        colorear(eliminar, COLOR_ELIMINAR);
        acciones.add(eliminar);

        return acciones;
    }

    private void colorear(Button boton, String color) {
        boton.getStyle().set("background-color", color).set("color", "#ffffff");
    }

    // ------------------------------------------------ alta y modificacion

    private void abrirFormulario(T existente) {
        boolean esNuevo = existente == null;
        Formulario<T> formulario;
        try {
            formulario = crearFormulario(existente);
        } catch (RestClientException e) {
            Notification.show("No se pudieron cargar las opciones del formulario.",
                    4000, Notification.Position.MIDDLE);
            return;
        }

        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle(esNuevo ? textoBotonNuevo() : textoModificar());
        dialogo.add(new FormLayout(formulario.campos()));

        Button cancelar = new Button("Cancelar", evento -> dialogo.close());
        Button guardar = new Button("Guardar", evento -> {
            String problema = formulario.validar();
            if (problema != null) {
                Notification.show(problema, 3000, Notification.Position.MIDDLE);
                return;
            }
            T elemento = formulario.construir();
            try {
                if (esNuevo) {
                    servicio.crear(elemento);
                } else {
                    servicio.actualizar(existente.getId(), elemento);
                }
                dialogo.close();
                actualizarTabla();
                Notification.show(esNuevo ? "¡Registro guardado correctamente!" : "¡Registro actualizado correctamente!",
                        3000, Notification.Position.TOP_CENTER);
            } catch (RestClientException e) {
                Notification.show(mensajeDe(e, "No se pudo guardar. Revisá los datos e intentá de nuevo."),
                        5000, Notification.Position.MIDDLE);
            }
        });
        colorear(guardar, COLOR_REGISTRAR);

        dialogo.getFooter().add(cancelar, guardar);
        dialogo.open();
    }

    // ------------------------------------------------ eliminacion

    private void confirmarEliminacion(T elemento) {
        ConfirmDialog confirmar = new ConfirmDialog();
        confirmar.setHeader("Confirmar eliminación");
        confirmar.setText("¿Confirmás que querés eliminar «" + describir(elemento)
                + "»? Esta acción no se puede deshacer.");
        confirmar.setCancelable(true);
        confirmar.setCancelText("Cancelar");
        confirmar.setConfirmText("Eliminar");
        confirmar.setConfirmButtonTheme("error primary");
        confirmar.addConfirmListener(evento -> eliminar(elemento));
        confirmar.open();
    }

    private void eliminar(T elemento) {
        try {
            servicio.eliminar(elemento.getId());
            actualizarTabla();
            Notification.show("Registro eliminado.", 3000, Notification.Position.TOP_CENTER);
        } catch (RestClientException e) {
            Notification.show(mensajeDe(e,
                    "No se pudo eliminar. Puede estar en uso por otro registro (por ejemplo, una asignación)."),
                    6000, Notification.Position.MIDDLE);
        }
    }

    /** Si el backend explico el problema (campo "mensaje"), se muestra; si no, el texto por defecto. */
    private String mensajeDe(RestClientException e, String porDefecto) {
        if (e instanceof HttpStatusCodeException respuesta) {
            Matcher m = Pattern.compile("\"mensaje\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                    .matcher(respuesta.getResponseBodyAsString());
            if (m.find()) {
                return m.group(1).replace("\\\"", "\"");
            }
        }
        if (e instanceof HttpStatusCodeException respuesta) {
            return porDefecto + " (error " + respuesta.getStatusCode().value() + ")";
        }
        return porDefecto;
    }

    protected void actualizarTabla() {
        try {
            List<T> elementos = obtenerElementos();
            if (buscador != null) {
                buscador.cargar(elementos);
            } else {
                tabla.setItems(elementos);
            }
        } catch (RestClientException e) {
            Notification.show("No se pudieron obtener los datos del backend.",
                    4000, Notification.Position.MIDDLE);
        }
    }
}
