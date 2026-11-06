package com.example.vista;

import com.example.modelo.Especie;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.EspecieService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestClientException;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;

/**
 * Lista las especies que un cuidador marcó como enfermas. Solo entra quien
 * tiene el permiso SALUD_VER (administrador y veterinario).
 */
@Route(value = "especies-enfermas", layout = MainLayout.class)
@PageTitle("Especies enfermas | EcoParque")
public class EspeciesEnfermasView extends VerticalLayout {

    private final EspecieService especieService;
    private final Grid<Especie> tabla = new Grid<>(Especie.class, false);
    private final Paragraph sinEnfermas = new Paragraph("No hay especies enfermas.");

    public EspeciesEnfermasView(EspecieService especieService, SesionUsuario sesion) {
        this.especieService = especieService;
        setPadding(true);

        tabla.addColumn(Especie::getNombreEspanol).setHeader("Especie");
        tabla.addColumn(especie -> especie.getZona() == null ? "" : especie.getZona().getNombre())
                .setHeader("Zona");
        // Más ancho que las otras: acá va el texto largo del cuidador.
        tabla.addColumn(Especie::getObservacionSalud).setHeader("Qué le pasa")
                .setFlexGrow(3).setWidth("300px");
        tabla.addColumn(Especie::getAvisadoPor).setHeader("Avisó");
        tabla.addColumn(Especie::getFechaAvisoSalud).setHeader("Fecha del aviso");
       

        // Quien puede editar salud (veterinario y administrador) puede marcarla como sana.
        if (sesion.tiene("SALUD_EDITAR")) {
            tabla.addComponentColumn(especie -> new HorizontalLayout(
                    new Button("Modificar", evento -> modificarAviso(especie)),
                    new Button("Marcar sana", evento -> marcarSana(especie))))
                    .setHeader("Acción").setAutoWidth(true);
        }

        // Si el texto es largo, baja a otro renglón en vez de quedar cortado.
        tabla.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        tabla.setAllRowsVisible(true);

        add(new H2("Especies enfermas"), sinEnfermas, tabla);
        actualizar();
    }

    private void marcarSana(Especie especie) {
        try {
            especieService.marcarSana(especie.getId());
            actualizar();
            Notification.show("Marcada como sana.", 3000, Notification.Position.TOP_CENTER);
        } catch (RestClientException e) {
            Notification.show("No se pudo actualizar.", 4000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Abre un cuadro con el texto actual para corregirlo.
     */
    private void modificarAviso(Especie especie) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle("Modificar aviso: " + especie.getNombreEspanol());

        TextArea observacion = new TextArea("¿Qué le pasa?");
        observacion.setWidth("400px");
        observacion.setValue(especie.getObservacionSalud() == null ? "" : especie.getObservacionSalud());
        dialogo.add(observacion);

        Button cancelar = new Button("Cancelar", evento -> dialogo.close());
        Button guardar = new Button("Guardar", evento -> {
            if (observacion.getValue().isBlank()) {
                Notification.show("Escribí qué le pasa a la especie.", 3000, Notification.Position.MIDDLE);
                return;
            }
            try {
                // Es el mismo endpoint de avisar: vuelve a guardar la observación.
                especieService.avisarEnferma(especie.getId(), observacion.getValue().trim());
                dialogo.close();
                actualizar();
                Notification.show("Aviso modificado.", 3000, Notification.Position.TOP_CENTER);
            } catch (RestClientException e) {
                Notification.show("No se pudo modificar el aviso.", 4000, Notification.Position.MIDDLE);
            }
        });
        dialogo.getFooter().add(cancelar, guardar);
        dialogo.open();
    }

    /**
     * Pide la lista al backend y muestra el cartel "No hay..." si viene vacía.
     */
    private void actualizar() {
        try {
            var enfermas = especieService.listarEnfermas();
            tabla.setItems(enfermas);
            sinEnfermas.setVisible(enfermas.isEmpty());
            tabla.setVisible(!enfermas.isEmpty());
        } catch (RestClientException e) {
            Notification.show("No se pudieron obtener los datos del backend.",
                    4000, Notification.Position.MIDDLE);
        }
    }
}
