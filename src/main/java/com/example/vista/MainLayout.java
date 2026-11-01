package com.example.vista;

import com.example.seguridad.SesionUsuario;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

/**
 * Estructura comun a todas las pantallas: barra superior y menu lateral.
 */
public class MainLayout extends AppLayout {

    public MainLayout(SesionUsuario sesion) {
        addToNavbar(crearEncabezado(sesion));
        addToDrawer(crearMenu(sesion));
    }

    private HorizontalLayout crearEncabezado(SesionUsuario sesion) {
        DrawerToggle alternarMenu = new DrawerToggle();
        alternarMenu.getStyle().set("color", "#ffffff");

        H1 titulo = new H1("EcoParque");
        titulo.getStyle().set("font-size", "1.25rem").set("margin", "0").set("color", "#ffffff");

        Span usuario = new Span("Usuario: " + sesion.nombrePersona() + " (" + sesion.rol() + ")");

        Button salir = new Button("Cerrar sesión", evento -> sesion.cerrar());
        salir.getStyle().set("background-color", "transparent")
                .set("color", "#ffffff")
                .set("border", "1px solid #ffffff");

        HorizontalLayout encabezado = new HorizontalLayout(alternarMenu, titulo, usuario, salir);
        encabezado.setWidthFull();
        encabezado.setAlignItems(FlexComponent.Alignment.CENTER);
        encabezado.expand(titulo);
        encabezado.getStyle().set("background-color", "#6b4f3b")
                .set("color", "#ffffff")
                .set("padding", "0.5rem 1rem");
        return encabezado;
    }

    private SideNav crearMenu(SesionUsuario sesion) {
        SideNav menu = new SideNav();
        menu.addItem(new SideNavItem("Dashboard", InicioView.class, VaadinIcon.HOME.create()));
        menu.addItem(new SideNavItem("Zonas", ZonaView.class, VaadinIcon.MAP_MARKER.create()));
        menu.addItem(new SideNavItem("Hábitats", HabitatView.class, VaadinIcon.GLOBE.create()));
        menu.addItem(new SideNavItem("Especies", EspecieView.class, VaadinIcon.LIST.create()));
        menu.addItem(new SideNavItem("Itinerarios", ItinerarioView.class, VaadinIcon.FLAG.create()));
        if (sesion.esAdministrador()) {
            // El administrador ve los datos completos del personal.
            menu.addItem(new SideNavItem("Guías", GuiaView.class, VaadinIcon.USER.create()));
            menu.addItem(new SideNavItem("Cuidadores", CuidadorView.class, VaadinIcon.USERS.create()));
        } else {
            // El resto solo ve quienes son las guias y de que son guias, sin datos personales.
            menu.addItem(new SideNavItem("Guías", GuiasPublicasView.class, VaadinIcon.USER.create()));
        }
        menu.addItem(new SideNavItem("Asignaciones", AsignacionGuiaView.class, VaadinIcon.CALENDAR.create()));
        menu.addItem(new SideNavItem("Responsabilidades", ResponsabilidadCuidadorView.class,
                VaadinIcon.CLIPBOARD.create()));
        if (sesion.esAdministrador()) {
            menu.addItem(new SideNavItem("Antigüedad", AntiguedadView.class, VaadinIcon.TIMER.create()));
            menu.addItem(new SideNavItem("Registro del parque", RegistroParqueView.class,
                    VaadinIcon.CHART.create()));
            menu.addItem(new SideNavItem("Usuarios", UsuarioView.class, VaadinIcon.KEY.create()));
        }
        return menu;
    }
}
