package com.example.vista;

import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestClientException;

@Route(value = "zonas", layout = MainLayout.class)
@PageTitle("Zonas | EcoParque")
public class ZonaView extends VerticalLayout {

    private final ZonaService zonaService;
    private final boolean esAdministrador;
    private final Grid<Zona> tabla = new Grid<>(Zona.class, false);

    public ZonaView(ZonaService zonaService, SesionUsuario sesion) {
        this.zonaService = zonaService;
        this.esAdministrador = sesion.esAdministrador();

        setPadding(true);
        add(new H2("Gestión de Zonas"));

        if (esAdministrador) {
            Button nueva = new Button("Registrar nueva zona", evento -> abrirFormulario(null));
            nueva.getStyle().set("background-color", "#7a8c5d").set("color", "#ffffff");
            add(nueva);
        }

        configurarTabla();
        add(tabla);
        actualizarTabla();
    }

    private void configurarTabla() {
        tabla.addColumn(Zona::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(zona -> formatearExtension(zona)).setHeader("Extensión");
        if (esAdministrador) {
            tabla.addComponentColumn(this::crearAcciones).setHeader("Acciones");
        }
        tabla.setAllRowsVisible(true);
    }

    private String formatearExtension(Zona zona) {
        if (zona.getExtension() == null) {
            return "";
        }
        return String.format("%.1f m²", zona.getExtension());
    }

    private HorizontalLayout crearAcciones(Zona zona) {
        Button modificar = new Button("Modificar", evento -> abrirFormulario(zona));
        modificar.getStyle().set("background-color", "#2563eb").set("color", "#ffffff");

        Button eliminar = new Button("Eliminar", evento -> confirmarEliminacion(zona));
        eliminar.getStyle().set("background-color", "#c0392b").set("color", "#ffffff");

        return new HorizontalLayout(modificar, eliminar);
    }

    /** Abre el formulario para registrar (existente == null) o modificar una zona. */
    private void abrirFormulario(Zona existente) {
        boolean esNueva = existente == null;

        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle(esNueva ? "Registrar nueva zona" : "Modificar zona");

        TextField nombre = new TextField("Nombre");
        NumberField extension = new NumberField("Extensión (m²)");
        if (!esNueva) {
            nombre.setValue(existente.getNombre() == null ? "" : existente.getNombre());
            if (existente.getExtension() != null) {
                extension.setValue(existente.getExtension());
            }
        }
        dialogo.add(new FormLayout(nombre, extension));

        Button cancelar = new Button("Cancelar", evento -> dialogo.close());
        Button guardar = new Button("Guardar", evento -> {
            if (nombre.isEmpty() || extension.isEmpty() || extension.getValue() <= 0) {
                Notification.show("Completá el nombre y una extensión mayor a cero.",
                        3000, Notification.Position.MIDDLE);
                return;
            }
            Zona zona = new Zona();
            zona.setNombre(nombre.getValue().trim());
            zona.setExtension(extension.getValue());

            String mensaje = esNueva ? "¡Zona registrada correctamente!" : "¡Zona actualizada correctamente!";
            try {
                if (esNueva) {
                    zonaService.crear(zona);
                } else {
                    zonaService.actualizar(existente.getId(), zona);
                }
                dialogo.close();
                actualizarTabla();
                Notification.show(mensaje, 3000, Notification.Position.TOP_CENTER);
            } catch (RestClientException e) {
                Notification.show("No se pudo guardar la zona.", 4000, Notification.Position.MIDDLE);
            }
        });
        guardar.getStyle().set("background-color", "#7a8c5d").set("color", "#ffffff");

        dialogo.getFooter().add(cancelar, guardar);
        dialogo.open();
    }

    private void confirmarEliminacion(Zona zona) {
        ConfirmDialog confirmar = new ConfirmDialog();
        confirmar.setHeader("Eliminar zona");
        confirmar.setText("¿Confirmás que querés eliminar la zona «" + zona.getNombre()
                + "»? Esta acción no se puede deshacer.");
        confirmar.setCancelable(true);
        confirmar.setCancelText("Cancelar");
        confirmar.setConfirmText("Eliminar");
        confirmar.setConfirmButtonTheme("error primary");
        confirmar.addConfirmListener(evento -> eliminar(zona));
        confirmar.open();
    }

    private void eliminar(Zona zona) {
        try {
            zonaService.eliminar(zona.getId());
            actualizarTabla();
            Notification.show("Zona eliminada.", 3000, Notification.Position.TOP_CENTER);
        } catch (RestClientException e) {
            Notification.show("No se pudo eliminar la zona. Puede estar en uso por una especie o un itinerario.",
                    5000, Notification.Position.MIDDLE);
        }
    }

    private void actualizarTabla() {
        try {
            tabla.setItems(zonaService.listar());
        } catch (RestClientException e) {
            Notification.show("No se pudo obtener las zonas del backend.", 4000, Notification.Position.MIDDLE);
        }
    }
}
