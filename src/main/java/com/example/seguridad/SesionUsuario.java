package com.example.seguridad;

import com.example.servicio.BackendConfig;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Guarda y consulta los datos de quien inicio sesion, usando la sesion de Vaadin.
 */
@Component
public class SesionUsuario {

    private static final String NOMBRE_USUARIO = "nombreUsuario";
    private static final String CLAVE = "clave";
    private static final String ROL = "rol";
    private static final String NOMBRE_PERSONA = "nombrePersona";
    private static final String PERMISOS = "permisos";

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;

    public SesionUsuario(BackendConfig backend) {
        this.backend = backend;
    }

    public void iniciar(String nombreUsuario, String clave, String rol, String nombrePersona) {
        VaadinSession sesion = VaadinSession.getCurrent();
        sesion.setAttribute(NOMBRE_USUARIO, nombreUsuario);
        sesion.setAttribute(CLAVE, clave);
        sesion.setAttribute(ROL, rol);
        sesion.setAttribute(NOMBRE_PERSONA, nombrePersona);
        // Los permisos se piden al backend la primera vez que hacen falta.
        sesion.setAttribute(PERMISOS, null);
    }

    public boolean estaLogueado() {
        return atributo(NOMBRE_USUARIO) != null;
    }

    public boolean esAdministrador() {
        return "ADMINISTRADOR".equals(atributo(ROL));
    }

    /** Dice si el rol del usuario le otorga el permiso (por ejemplo "ZONAS_EDITAR"). */
    public boolean tiene(String permiso) {
        return permisos().contains(permiso);
    }

    /** Puede abrir la pantalla: no pide permiso o tiene el de ver su sección. */
    public boolean puedeVer(Class<?> pantalla) {
        String seccion = Permisos.seccionDe(pantalla);
        return seccion == null || tiene(seccion + "_VER");
    }

    /** Ve los botones de alta, baja y modificación de la pantalla. */
    public boolean puedeEditar(Class<?> pantalla) {
        String seccion = Permisos.seccionDe(pantalla);
        return seccion != null && tiene(seccion + "_EDITAR");
    }

    @SuppressWarnings("unchecked")
    private Set<String> permisos() {
        VaadinSession sesion = VaadinSession.getCurrent();
        if (sesion == null || !estaLogueado()) {
            return Set.of();
        }
        Set<String> permisos = (Set<String>) sesion.getAttribute(PERMISOS);
        if (permisos == null) {
            try {
                String[] recibidos = restTemplate.exchange(backend.url("/api/roles/mis-permisos"), HttpMethod.GET,
                        new HttpEntity<>(encabezados()), String[].class).getBody();
                permisos = new HashSet<>(Arrays.asList(recibidos == null ? new String[0] : recibidos));
            } catch (RuntimeException e) {
                return Set.of();
            }
            sesion.setAttribute(PERMISOS, permisos);
        }
        return permisos;
    }

    public String rol() {
        String rol = atributo(ROL);
        return rol == null ? "" : rol;
    }

    /** Nombre de la persona; si no tiene cargado, usa el nombre de usuario. */
    public String nombrePersona() {
        String nombre = atributo(NOMBRE_PERSONA);
        if (nombre == null || nombre.isBlank()) {
            String usuario = atributo(NOMBRE_USUARIO);
            return usuario == null ? "" : usuario;
        }
        return nombre;
    }

    /** Encabezados con Basic Auth para llamar al backend en nombre del usuario. */
    public HttpHeaders encabezados() {
        HttpHeaders encabezados = new HttpHeaders();
        encabezados.setBasicAuth(atributo(NOMBRE_USUARIO), atributo(CLAVE));
        return encabezados;
    }

    public void cerrar() {
        UI ui = UI.getCurrent();
        VaadinSession sesion = VaadinSession.getCurrent();
        if (ui != null) {
            ui.getPage().setLocation("login");
        }
        if (sesion != null) {
            sesion.getSession().invalidate();
        }
    }

    private String atributo(String clave) {
        VaadinSession sesion = VaadinSession.getCurrent();
        if (sesion == null) {
            return null;
        }
        return (String) sesion.getAttribute(clave);
    }
}
