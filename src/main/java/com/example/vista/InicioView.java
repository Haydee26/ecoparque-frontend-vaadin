package com.example.vista;

import com.example.seguridad.SesionUsuario;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Inicio | EcoParque")
public class InicioView extends VerticalLayout {

    public InicioView(SesionUsuario sesion) {
        add(new H2("Sistema de gestión del Ecoparque"),
                new Paragraph("Bienvenido/a, " + sesion.nombrePersona() + ". Elegí una sección del menú."));
    }
}
