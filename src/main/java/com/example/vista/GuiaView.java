package com.example.vista;

import com.example.modelo.Guia;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.GuiaService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "guias", layout = MainLayout.class)
@PageTitle("Guías | EcoParque")
@SoloAdministrador
public class GuiaView extends VistaRoles<Guia> {

    public GuiaView(GuiaService servicio) {
        super(Guia.class, servicio, "Guías");
    }
}
