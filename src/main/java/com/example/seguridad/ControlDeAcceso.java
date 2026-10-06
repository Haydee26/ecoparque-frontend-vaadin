package com.example.seguridad;

import com.example.vista.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Antes de mostrar cualquier pantalla, verifica que haya una sesion iniciada.
 * Si no la hay, redirige al login.
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
        boolean esLogin = LoginView.class.equals(evento.getNavigationTarget());
        if (!esLogin && !sesion.estaLogueado()) {
            evento.rerouteTo(LoginView.class);
        }
    }
}
