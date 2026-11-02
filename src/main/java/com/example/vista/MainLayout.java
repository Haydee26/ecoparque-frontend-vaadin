package com.example.vista;

import com.example.seguridad.SesionUsuario;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        HorizontalLayout encabezado = new HorizontalLayout(alternarMenu, titulo, crearMenuSuperior(sesion), usuario, salir);
        encabezado.setWidthFull();
        encabezado.setAlignItems(FlexComponent.Alignment.CENTER);
        encabezado.expand(titulo);
        encabezado.getStyle().set("background-color", "#6b4f3b")
                .set("color", "#ffffff")
                .set("padding", "0.5rem 1rem");
        return encabezado;
    }

    /** Barra superior horizontal: Menú | Parque | Actividad | Personal | Administración (se despliegan hacia abajo). */
    private MenuBar crearMenuSuperior(SesionUsuario sesion) {
        MenuBar barra = new MenuBar();
        barra.addItem(etiqueta("Menú"), evento -> UI.getCurrent().navigate(InicioView.class));

        SubMenu parque = barra.addItem(etiqueta("Parque ▾")).getSubMenu();
        parque.addItem("Zonas", evento -> UI.getCurrent().navigate(ZonaView.class));
        parque.addItem("Mapa del parque", evento -> UI.getCurrent().navigate(MapaParqueView.class));
        parque.addItem("Hábitats", evento -> UI.getCurrent().navigate(HabitatView.class));
        parque.addItem("Especies", evento -> UI.getCurrent().navigate(EspecieView.class));
        parque.addItem("Itinerarios", evento -> UI.getCurrent().navigate(ItinerarioView.class));

        SubMenu actividad = barra.addItem(etiqueta("Actividad ▾")).getSubMenu();
        actividad.addItem("Asignaciones de guías", evento -> UI.getCurrent().navigate(AsignacionGuiaView.class));
        actividad.addItem("Responsabilidades",
                evento -> UI.getCurrent().navigate(ResponsabilidadCuidadorView.class));

        SubMenu personal = barra.addItem(etiqueta("Personal ▾")).getSubMenu();
        if (sesion.esAdministrador()) {
            personal.addItem("Empleados", evento -> UI.getCurrent().navigate(EmpleadoView.class));
            personal.addItem("Guías", evento -> UI.getCurrent().navigate(GuiaView.class));
            personal.addItem("Cuidadores", evento -> UI.getCurrent().navigate(CuidadorView.class));
        } else {
            personal.addItem("Guías", evento -> UI.getCurrent().navigate(GuiasPublicasView.class));
        }

        if (sesion.esAdministrador()) {
            SubMenu administracion = barra.addItem(etiqueta("Administración ▾")).getSubMenu();
            administracion.addItem("Registro del parque", evento -> UI.getCurrent().navigate(RegistroParqueView.class));
            administracion.addItem("Usuarios", evento -> UI.getCurrent().navigate(UsuarioView.class));
        }
        return barra;
    }

    /** Texto blanco para que se lea sobre la barra marron. */
    private Span etiqueta(String texto) {
        Span etiqueta = new Span(texto);
        etiqueta.getStyle().set("color", "#ffffff").set("font-weight", "600");
        return etiqueta;
    }

    /** Menu agrupado: Parque, Actividad, Personal y (solo administrador) Administracion. */
    private Component crearMenu(SesionUsuario sesion) {
        SideNav inicio = new SideNav();
        inicio.addItem(new SideNavItem("Menú", InicioView.class, VaadinIcon.HOME.create()));

        SideNav parque = new SideNav("Parque");
        parque.addItem(new SideNavItem("Zonas", ZonaView.class, VaadinIcon.MAP_MARKER.create()));
        parque.addItem(new SideNavItem("Mapa del parque", MapaParqueView.class, VaadinIcon.LOCATION_ARROW.create()));
        parque.addItem(new SideNavItem("Hábitats", HabitatView.class, VaadinIcon.GLOBE.create()));
        parque.addItem(new SideNavItem("Especies", EspecieView.class, VaadinIcon.LIST.create()));
        parque.addItem(new SideNavItem("Itinerarios", ItinerarioView.class, VaadinIcon.FLAG.create()));

        SideNav actividad = new SideNav("Actividad");
        actividad.addItem(new SideNavItem("Asignaciones de guías", AsignacionGuiaView.class,
                VaadinIcon.CALENDAR.create()));
        actividad.addItem(new SideNavItem("Responsabilidades", ResponsabilidadCuidadorView.class,
                VaadinIcon.CLIPBOARD.create()));

        SideNav personal = new SideNav("Personal");
        if (sesion.esAdministrador()) {
            // El administrador ve los datos completos de los empleados.
            personal.addItem(new SideNavItem("Empleados", EmpleadoView.class, VaadinIcon.ARCHIVE.create()));
            personal.addItem(new SideNavItem("Guías", GuiaView.class, VaadinIcon.USER.create()));
            personal.addItem(new SideNavItem("Cuidadores", CuidadorView.class, VaadinIcon.USERS.create()));
        } else {
            // El resto solo ve quienes son las guias y de que son guias, sin datos personales.
            personal.addItem(new SideNavItem("Guías", GuiasPublicasView.class, VaadinIcon.USER.create()));
        }

        VerticalLayout menu = new VerticalLayout(inicio, parque, actividad, personal);
        if (sesion.esAdministrador()) {
            SideNav administracion = new SideNav("Administración");
            administracion.addItem(new SideNavItem("Registro del parque", RegistroParqueView.class,
                    VaadinIcon.CHART.create()));
            administracion.addItem(new SideNavItem("Usuarios", UsuarioView.class, VaadinIcon.KEY.create()));
            menu.add(administracion);
        }
        menu.setPadding(false);
        menu.setSpacing(false);
        return menu;
    }
}
