package com.example.vista;

import com.example.modelo.Especimen;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.EspecimenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;
import org.springframework.web.client.RestClientException;

/**
 * Lista los especimenes que no estan sanos (enfermos o en tratamiento).
 * Entra quien tiene el permiso SALUD_VER (administrador y veterinario).
 */
@Route(value = "salud-especimenes", layout = MainLayout.class)
@PageTitle("Salud de los especímenes | EcoParque")
public class SaludEspecimenesView extends VerticalLayout {

    private final EspecimenService especimenService;
    private final Grid<Especimen> tabla = new Grid<>(Especimen.class, false);
    private final Paragraph sinNovedades = new Paragraph("Todos los especímenes están sanos.");

    public SaludEspecimenesView(EspecimenService especimenService, SesionUsuario sesion) {
        this.especimenService = especimenService;
        setPadding(true);

        tabla.addColumn(Especimen::getNombre).setHeader("Especimen").setSortable(true);
        tabla.addColumn(e -> e.getEspecie() == null ? "" : e.getEspecie().getNombreCientifico())
                .setHeader("Especie");
        tabla.addColumn(e -> e.getEspecie() == null || e.getEspecie().getZona() == null
                ? "" : e.getEspecie().getZona().getNombre())
                .setHeader("Zona");
        tabla.addColumn(e -> e.getEstadoSalud() == null ? "" : e.getEstadoSalud().getTexto())
                .setHeader("Estado").setSortable(true);
        tabla.addColumn(e -> e.getObservacionSalud() == null ? "" : e.getObservacionSalud())
                .setHeader("Qué le pasa").setFlexGrow(3).setWidth("300px");

        // Solo quien tiene SALUD_EDITAR ve el boton (veterinario y administrador).
        if (sesion.tiene("SALUD_EDITAR")) {
            tabla.addComponentColumn(e -> new Button("Cambiar salud",
                    evento -> new DialogoSalud(e, especimenService, this::actualizar).open()))
                    .setHeader("Acción").setAutoWidth(true);
        }

        tabla.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        tabla.setAllRowsVisible(true);

        add(new H2("Salud de los especímenes"), sinNovedades, tabla);
        actualizar();
    }

    /** Pide al backend los que no estan sanos; si no hay ninguno, muestra el cartel. */
    private void actualizar() {
        try {
            List<Especimen> noSanos = especimenService.listarNoSanos();
            tabla.setItems(noSanos);
            sinNovedades.setVisible(noSanos.isEmpty());
            tabla.setVisible(!noSanos.isEmpty());
        } catch (RestClientException e) {
            Notification.show("No se pudieron obtener los datos del backend.",
                    4000, Notification.Position.MIDDLE);
        }
    }
}