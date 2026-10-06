package com.example.seguridad;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Guarda y consulta los datos de quien inicio sesion, usando la sesion de Vaadin.
 */
@Component
public class SesionUsuario {

    private static final String NOMBRE_USUARIO = "nombreUsuario";
    private static final String CLAVE = "clave";
    private static final String ROL = "rol";
    private static final String NOMBRE_PERSONA = "nombrePersona";

    public void iniciar(String nombreUsuario, String clave, String rol, String nombrePersona) {
        VaadinSession sesion = VaadinSession.getCurrent();
        sesion.setAttribute(NOMBRE_USUARIO, nombreUsuario);
        sesion.setAttribute(CLAVE, clave);
        sesion.setAttribute(ROL, rol);
        sesion.setAttribute(NOMBRE_PERSONA, nombrePersona);
    }

    public boolean estaLogueado() {
        return atributo(NOMBRE_USUARIO) != null;
    }

    public boolean esAdministrador() {
        return "ADMINISTRADOR".equals(atributo(ROL));
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
