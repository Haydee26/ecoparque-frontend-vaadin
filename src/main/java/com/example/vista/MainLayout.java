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
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Barra superior horizontal: Menú | Parque | Actividad | Personal |
     * Administración (solo con lo que el rol permite).
     */
    private MenuBar crearMenuSuperior(SesionUsuario sesion) {
        MenuBar barra = new MenuBar();
        barra.addItem(etiqueta("Menú"), evento -> UI.getCurrent().navigate(InicioView.class));

        List<Opcion> parque = new ArrayList<>();
        parque.add(new Opcion("Zonas", ZonaView.class, VaadinIcon.MAP_MARKER));
        parque.add(new Opcion("Mapa del parque", MapaParqueView.class, VaadinIcon.LOCATION_ARROW));
        parque.add(new Opcion("Hábitats", HabitatView.class, VaadinIcon.GLOBE));
        parque.add(new Opcion("Especies", EspecieView.class, VaadinIcon.LIST));
        parque.add(new Opcion("Itinerarios", ItinerarioView.class, VaadinIcon.FLAG));
        parque.add(new Opcion("Especies enfermas", EspeciesEnfermasView.class, VaadinIcon.WARNING));
        agregarGrupo(barra, "Parque ▾", parque, sesion);

        List<Opcion> actividad = new ArrayList<>();
        actividad.add(new Opcion("Asignaciones de guías", AsignacionGuiaView.class, VaadinIcon.CALENDAR));
        actividad.add(new Opcion("Responsabilidades", ResponsabilidadCuidadorView.class, VaadinIcon.CLIPBOARD));
        agregarGrupo(barra, "Actividad ▾", actividad, sesion);

        agregarGrupo(barra, "Personal ▾", opcionesDePersonal(sesion), sesion);
        agregarGrupo(barra, "Administración ▾", opcionesDeAdministracion(), sesion);
        return barra;
    }

    /**
     * Una opción del menú: texto, pantalla de destino e ícono.
     */
    private record Opcion(String texto, Class<? extends Component> destino, VaadinIcon icono) {

    }

    /**
     * Guías y cuidadores: con permiso se ven los datos completos; si no, solo
     * la lista pública de guías.
     */
    private List<Opcion> opcionesDePersonal(SesionUsuario sesion) {
        List<Opcion> personal = new ArrayList<>();
        personal.add(new Opcion("Empleados", EmpleadoView.class, VaadinIcon.ARCHIVE));
        if (sesion.puedeVer(GuiaView.class)) {
            personal.add(new Opcion("Guías", GuiaView.class, VaadinIcon.USER));
        } else if (!"VETERINARIO".equals(sesion.rol())) {
            // Sin permiso para los datos personales solo ve quienes son las guías y de qué son guías.
            // El veterinario no la ve: trabaja con los cuidadores, que son quienes atienden a las especies.
            personal.add(new Opcion("Guías", GuiasPublicasView.class, VaadinIcon.USER));
        }
        personal.add(new Opcion("Cuidadores", CuidadorView.class, VaadinIcon.USERS));
        return personal;
    }

    private List<Opcion> opcionesDeAdministracion() {
        List<Opcion> administracion = new ArrayList<>();
        administracion.add(new Opcion("Registro del parque", RegistroParqueView.class, VaadinIcon.CHART));
        administracion.add(new Opcion("Usuarios", UsuarioView.class, VaadinIcon.KEY));
        administracion.add(new Opcion("Roles", RolView.class, VaadinIcon.LOCK));
        return administracion;
    }

    /**
     * Agrega el grupo solo si el usuario puede ver al menos una de sus
     * opciones.
     */
    private void agregarGrupo(MenuBar barra, String titulo, List<Opcion> opciones, SesionUsuario sesion) {
        List<Opcion> visibles = opciones.stream().filter(o -> sesion.puedeVer(o.destino())).toList();
        if (visibles.isEmpty()) {
            return;
        }
        SubMenu submenu = barra.addItem(etiqueta(titulo)).getSubMenu();
        for (Opcion opcion : visibles) {
            submenu.addItem(opcion.texto(), evento -> UI.getCurrent().navigate(opcion.destino()));
        }
    }

    /**
     * Texto blanco para que se lea sobre la barra marron.
     */
    private Span etiqueta(String texto) {
        Span etiqueta = new Span(texto);
        etiqueta.getStyle().set("color", "#ffffff").set("font-weight", "600");
        return etiqueta;
    }

    /**
     * Menu agrupado: Parque, Actividad, Personal y Administracion, solo con lo
     * que el rol permite.
     */
    private Component crearMenu(SesionUsuario sesion) {
        SideNav inicio = new SideNav();
        inicio.addItem(new SideNavItem("Menú", InicioView.class, VaadinIcon.HOME.create()));
        VerticalLayout menu = new VerticalLayout(inicio);
        List<Opcion> parque = new ArrayList<>();
        parque.add(new Opcion("Zonas", ZonaView.class, VaadinIcon.MAP_MARKER));
        parque.add(new Opcion("Mapa del parque", MapaParqueView.class, VaadinIcon.LOCATION_ARROW));
        parque.add(new Opcion("Hábitats", HabitatView.class, VaadinIcon.GLOBE));
        parque.add(new Opcion("Especies", EspecieView.class, VaadinIcon.LIST));
        parque.add(new Opcion("Itinerarios", ItinerarioView.class, VaadinIcon.FLAG));
        parque.add(new Opcion("Especies enfermas", EspeciesEnfermasView.class, VaadinIcon.WARNING));
        agregarSeccion(menu, "Parque", parque, sesion);
        List<Opcion> actividad = new ArrayList<>();
        actividad.add(new Opcion("Asignaciones de guías", AsignacionGuiaView.class, VaadinIcon.CALENDAR));
        actividad.add(new Opcion("Responsabilidades", ResponsabilidadCuidadorView.class, VaadinIcon.CLIPBOARD));
        agregarSeccion(menu, "Actividad", actividad, sesion);

        agregarSeccion(menu, "Personal", opcionesDePersonal(sesion), sesion);
        agregarSeccion(menu, "Administración", opcionesDeAdministracion(), sesion);

        menu.setPadding(false);
        menu.setSpacing(false);
        return menu;
    }

    private void agregarSeccion(VerticalLayout menu, String titulo, List<Opcion> opciones, SesionUsuario sesion) {
        SideNav seccion = new SideNav(titulo);
        boolean hayOpciones = false;
        for (Opcion opcion : opciones) {
            if (sesion.puedeVer(opcion.destino())) {
                seccion.addItem(new SideNavItem(opcion.texto(), opcion.destino(), opcion.icono().create()));
                hayOpciones = true;
            }
        }
        if (hayOpciones) {
            menu.add(seccion);
        }
    }
}
