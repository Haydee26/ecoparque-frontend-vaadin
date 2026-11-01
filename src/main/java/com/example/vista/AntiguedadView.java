package com.example.vista;

import com.example.modelo.EmpleadoAntiguedad;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.PersonalService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Personal del parque (guias y cuidadores) ordenado por antiguedad:
 * primero quien hace mas tiempo que ingreso. Solo para el administrador.
 */
@Route(value = "antiguedad", layout = MainLayout.class)
@PageTitle("Antigüedad | EcoParque")
@SoloAdministrador
public class AntiguedadView extends VerticalLayout {

    public AntiguedadView(PersonalService personalService) {
        setPadding(true);
        add(new H2("Personal por antigüedad"));

        Grid<EmpleadoAntiguedad> tabla = new Grid<>(EmpleadoAntiguedad.class, false);
        tabla.addColumn(empleado -> empleado.getNombre()).setHeader("Nombre");
        tabla.addColumn(empleado -> empleado.getTipo()).setHeader("Puesto");
        tabla.addColumn(empleado -> Formatos.fecha(empleado.getFechaIngreso())).setHeader("Fecha de ingreso");
        tabla.addColumn(empleado -> anios(empleado.getAnios())).setHeader("Antigüedad");

        try {
            tabla.setItems(personalService.porAntiguedad());
            add(tabla);
        } catch (RuntimeException e) {
            add(new Paragraph("No se pudo cargar la lista. Verificá que el backend esté funcionando."));
        }
    }

    private String anios(Long anios) {
        if (anios == null) {
            return "-";
        }
        return anios == 0 ? "Menos de 1 año" : anios + (anios == 1 ? " año" : " años");
    }
}
