package com.example.seguridad;

import com.example.vista.InicioView;
import com.example.vista.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Antes de mostrar cualquier pantalla verifica dos cosas:
 * que haya una sesion iniciada y que el rol del usuario tenga el permiso
 * de ver esa pantalla (ver la clase Permisos).
 */
@Component
public class ControlDeAcceso implements VaadinServiceInitListener {

    private final SesionUsuario sesion;

    public ControlDeAcceso(SesionUsuario sesion) {
        this.sesion = sesion;
    }

    @Override
    public void serviceInit(ServiceInitEvent evento) {
        evento.getSource().addUIInitListener(
                inicio -> inicio.getUI().addBeforeEnterListener(this::verificarAcceso));
    }

    private void verificarAcceso(BeforeEnterEvent evento) {
        Class<?> destino = evento.getNavigationTarget();

        if (LoginView.class.equals(destino)) {
            return;
        }
        if (!sesion.estaLogueado()) {
            evento.rerouteTo(LoginView.class);
            return;
        }
        if (!sesion.puedeVer(destino)) {
            evento.rerouteTo(InicioView.class);
        }
    }
}
