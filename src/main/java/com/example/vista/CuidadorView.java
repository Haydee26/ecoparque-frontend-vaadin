package com.example.vista;

import com.example.modelo.Cuidador;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.CuidadorService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cuidadores", layout = MainLayout.class)
@PageTitle("Cuidadores | EcoParque")
@SoloAdministrador
public class CuidadorView extends VistaRoles<Cuidador> {

    public CuidadorView(CuidadorService servicio) {
        super(Cuidador.class, servicio, "Cuidadores");
    }
}
