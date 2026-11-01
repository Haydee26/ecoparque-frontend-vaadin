package com.example.vista;

import com.example.modelo.Persona;
import com.example.servicio.ServicioRest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Lista de los empleados que tienen un rol del parque (guia o cuidador). Es solo de consulta:
 * el rol se da o se quita al registrar o modificar al empleado en Empleados.
 */
public abstract class VistaRoles<P extends Persona> extends VerticalLayout {

    protected VistaRoles(Class<P> tipo, ServicioRest<P> servicio, String titulo) {
        setPadding(true);
        add(new H2(titulo),
                new Paragraph("Para dar o quitar este puesto, modificá al empleado en la sección Empleados."));

        Grid<P> tabla = new Grid<>(tipo, false);
        tabla.addColumn(Persona::getNombre).setHeader("Nombre y apellido").setSortable(true);
        tabla.addColumn(Persona::getDireccion).setHeader("Dirección");
        tabla.addColumn(Persona::getTelefono).setHeader("Teléfono");
        tabla.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        tabla.setAllRowsVisible(true);

        BuscadorDeTabla<P> buscador = new BuscadorDeTabla<>(tabla);
        add(buscador.campo());
        try {
            buscador.cargar(servicio.listar());
            add(tabla);
        } catch (RuntimeException e) {
            add(new Paragraph("No se pudo cargar la lista. Verificá que el backend esté funcionando."));
        }
    }
}
