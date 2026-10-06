package com.example.vista;

import com.example.modelo.Guia;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.GuiaService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "guias", layout = MainLayout.class)
@PageTitle("Guías | EcoParque")
public class GuiaView extends VistaPersonas<Guia> {

    public GuiaView(GuiaService servicio, SesionUsuario sesion) {
        super(Guia.class, servicio, sesion, Guia::new);
    }

    @Override
    protected String titulo() {
        return "Gestión de Guías";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar guía";
    }

    @Override
    protected String textoModificar() {
        return "Modificar guía";
    }
}
