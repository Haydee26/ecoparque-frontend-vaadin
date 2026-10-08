package com.example.seguridad;

import com.example.vista.InicioView;
import com.example.vista.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Antes de mostrar cualquier pantalla verifica dos cosas:
 * que haya una sesion iniciada y, si la pantalla esta marcada como
 * @SoloAdministrador, que quien entro sea administrador.
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
        if (destino.isAnnotationPresent(SoloAdministrador.class) && !sesion.esAdministrador()) {
            evento.rerouteTo(InicioView.class);
        }
    }
}
