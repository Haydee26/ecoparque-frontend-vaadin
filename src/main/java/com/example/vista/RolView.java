package com.example.vista;

import com.example.modelo.PermisoInfo;
import com.example.modelo.Rol;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.RolService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestión de roles: el administrador crea un rol nuevo y tilda qué puede ver y qué puede editar
 * en cada sección. Los roles base (administrador, guía, cuidador e invitado) no se pueden borrar
 * ni renombrar, y el administrador siempre tiene todos los permisos.
 */
@Route(value = "roles", layout = MainLayout.class)
@PageTitle("Roles | EcoParque")
public class RolView extends VistaCrud<Rol> {

    private final RolService rolService;

    public RolView(RolService servicio, SesionUsuario sesion) {
        super(Rol.class, servicio, sesion);
        this.rolService = servicio;
    }

    @Override
    protected String titulo() {
        return "Gestión de Roles";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Crear nuevo rol";
    }

    @Override
    protected String textoModificar() {
        return "Modificar rol";
    }

    @Override
    protected String describir(Rol rol) {
        return rol.getNombre();
    }

    @Override
    protected void configurarColumnas(Grid<Rol> tabla) {
        Map<String, PermisoInfo> catalogo = catalogoPorCodigo();
        tabla.addColumn(Rol::getNombre).setHeader("Rol").setSortable(true).setAutoWidth(true);
        tabla.addColumn(Rol::getDescripcion).setHeader("Descripción");
        tabla.addColumn(rol -> rol.isBase() ? "Base" : "Creado por el administrador").setHeader("Tipo");
        tabla.addColumn(Rol::getUsuarios).setHeader("Usuarios");
        tabla.addColumn(rol -> resumir(rol, catalogo)).setHeader("Permisos").setFlexGrow(3);
    }

    private Map<String, PermisoInfo> catalogoPorCodigo() {
        Map<String, PermisoInfo> mapa = new HashMap<>();
        try {
            for (PermisoInfo permiso : rolService.catalogo()) {
                mapa.put(permiso.getCodigo(), permiso);
            }
        } catch (RuntimeException e) {
            // sin catálogo se muestran los códigos tal cual
        }
        return mapa;
    }

    /** "Zonas: editar · Especies: ver · …" (si puede editar, también puede ver). */
    private String resumir(Rol rol, Map<String, PermisoInfo> catalogo) {
        Map<String, String> porSeccion = new LinkedHashMap<>();
        for (String codigo : rol.getPermisos()) {
            PermisoInfo info = catalogo.get(codigo);
            if (info == null) {
                porSeccion.put(codigo, "");
                continue;
            }
            String actual = porSeccion.get(info.getSeccion());
            if (actual == null || "Ver".equals(actual)) {
                porSeccion.put(info.getSeccion(), info.getAccion());
            }
        }
        List<String> partes = new ArrayList<>();
        porSeccion.forEach((seccion, accion) -> partes.add(accion.isEmpty() ? seccion : seccion + ": " + accion.toLowerCase()));
        return String.join(" · ", partes);
    }

    @Override
    protected Formulario<Rol> crearFormulario(Rol existente) {
        return new FormularioRol(existente, rolService.catalogo());
    }

    private static class FormularioRol implements Formulario<Rol> {

        private final boolean esAdministrador;
        private final TextField nombre = new TextField("Nombre del rol");
        private final TextField descripcion = new TextField("Descripción");
        private final Map<String, Checkbox> ver = new LinkedHashMap<>();
        private final Map<String, Checkbox> editar = new LinkedHashMap<>();
        private final VerticalLayout contenido = new VerticalLayout();

        FormularioRol(Rol existente, List<PermisoInfo> catalogo) {
            this.esAdministrador = existente != null && "ADMINISTRADOR".equals(existente.getNombre());
            nombre.setWidthFull();
            descripcion.setWidthFull();
            nombre.setHelperText("Se guarda en mayúsculas, por ejemplo VETERINARIO.");

            contenido.setPadding(false);
            contenido.setSpacing(false);
            contenido.add(nombre, descripcion);
            Span titulo = new Span("Permisos de cada sección");
            titulo.getStyle().set("font-weight", "600").set("margin-top", "1rem");
            Span ayuda = new Span("Editar incluye ver. Quien no tiene «ver» no ve la sección en el menú.");
            ayuda.getStyle().set("font-size", "var(--lumo-font-size-s)").set("color", "var(--lumo-secondary-text-color)");
            contenido.add(titulo, ayuda, fila(new Span("Sección"), new Span("Ver"), new Span("Editar"), true));

            // Una fila por sección, en el orden del catálogo.
            Map<String, String[]> secciones = new LinkedHashMap<>();
            for (PermisoInfo permiso : catalogo) {
                String[] codigos = secciones.computeIfAbsent(permiso.getSeccion(), s -> new String[2]);
                codigos["Ver".equals(permiso.getAccion()) ? 0 : 1] = permiso.getCodigo();
            }
            secciones.forEach((seccion, codigos) -> {
                Checkbox cVer = new Checkbox();
                Checkbox cEditar = new Checkbox();
                ver.put(codigos[0], cVer);
                editar.put(codigos[1], cEditar);
                cEditar.addValueChangeListener(e -> {
                    if (e.getValue()) {
                        cVer.setValue(true);
                    }
                });
                cVer.addValueChangeListener(e -> {
                    if (!e.getValue()) {
                        cEditar.setValue(false);
                    }
                });
                contenido.add(fila(new Span(seccion), cVer, cEditar, false));
            });

            if (existente != null) {
                nombre.setValue(existente.getNombre() == null ? "" : existente.getNombre());
                descripcion.setValue(existente.getDescripcion() == null ? "" : existente.getDescripcion());
                ver.forEach((codigo, caja) -> caja.setValue(existente.getPermisos().contains(codigo)));
                editar.forEach((codigo, caja) -> caja.setValue(existente.getPermisos().contains(codigo)));
                if (existente.isBase()) {
                    nombre.setReadOnly(true);
                }
            }
            if (esAdministrador) {
                descripcion.setReadOnly(true);
                ver.values().forEach(caja -> caja.setEnabled(false));
                editar.values().forEach(caja -> caja.setEnabled(false));
            }
        }

        private HorizontalLayout fila(Component etiqueta, Component cVer, Component cEditar, boolean encabezado) {
            HorizontalLayout fila = new HorizontalLayout(etiqueta, cVer, cEditar);
            etiqueta.getElement().getStyle().set("width", "20em");
            cVer.getElement().getStyle().set("width", "5em");
            cEditar.getElement().getStyle().set("width", "5em");
            if (encabezado) {
                fila.getStyle().set("font-weight", "600").set("border-bottom", "1px solid var(--lumo-contrast-20pct)");
            }
            return fila;
        }

        @Override
        public Component[] campos() {
            return new Component[]{contenido};
        }

        @Override
        public String validar() {
            if (esAdministrador) {
                return "El rol ADMINISTRADOR siempre tiene todos los permisos: no se modifica.";
            }
            if (nombre.isEmpty()) {
                return "Completá el nombre del rol.";
            }
            if (permisosElegidos().isEmpty()) {
                return "Elegí al menos un permiso para el rol.";
            }
            return null;
        }

        private List<String> permisosElegidos() {
            List<String> elegidos = new ArrayList<>();
            ver.forEach((codigo, caja) -> {
                if (caja.getValue()) {
                    elegidos.add(codigo);
                }
            });
            editar.forEach((codigo, caja) -> {
                if (caja.getValue()) {
                    elegidos.add(codigo);
                }
            });
            return elegidos;
        }

        @Override
        public Rol construir() {
            Rol rol = new Rol();
            rol.setNombre(nombre.getValue().trim());
            rol.setDescripcion(descripcion.getValue().trim());
            rol.setPermisos(permisosElegidos());
            return rol;
        }
    }
}
