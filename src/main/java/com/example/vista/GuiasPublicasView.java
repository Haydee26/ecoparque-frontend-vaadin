package com.example.vista;

import com.example.modelo.GuiaPublica;
import com.example.servicio.PersonalService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Guias del parque para quien no es administrador: nombre y de que es guia
 * (itinerario y lugar o sector). No muestra datos personales.
 */
@Route(value = "equipo-guias", layout = MainLayout.class)
@PageTitle("Guías | EcoParque")
public class GuiasPublicasView extends VerticalLayout {

    public GuiasPublicasView(PersonalService personalService) {
        setPadding(true);
        add(new H2("Guías del parque"));

        Grid<GuiaPublica> tabla = new Grid<>(GuiaPublica.class, false);
        tabla.addColumn(GuiaPublica::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(guia -> texto(Formatos.unir(guia.getItinerarios(), codigo -> codigo)))
                .setHeader("Guía de (itinerarios)");
        tabla.addColumn(guia -> texto(Formatos.unir(guia.getZonas(), zona -> zona)))
                .setHeader("Lugar / sector");

        try {
            tabla.setItems(personalService.guiasPublicas());
            add(tabla);
        } catch (RuntimeException e) {
            add(new Paragraph("No se pudo cargar la lista de guías. Verificá que el backend esté funcionando."));
        }
    }

    private String texto(String valor) {
        return valor.isEmpty() ? "Sin asignar" : valor;
    }
}
