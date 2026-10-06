package com.example.vista;

import com.example.modelo.Usuario;
import com.example.seguridad.AutenticacionService;
import com.example.seguridad.SesionUsuario;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Route("login")
@PageTitle("Ingresar | EcoParque")
public class LoginView extends VerticalLayout {

    public LoginView(AutenticacionService autenticacion, SesionUsuario sesion) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        LoginForm formulario = new LoginForm();
        formulario.setForgotPasswordButtonVisible(false);
        formulario.setI18n(crearTextos());
        formulario.addLoginListener(evento -> {
            try {
                Usuario usuario = autenticacion.autenticar(evento.getUsername(), evento.getPassword());
                if (usuario == null) {
                    formulario.setError(true);
                    return;
                }
                sesion.iniciar(evento.getUsername(), evento.getPassword(),
                        usuario.getRol(), usuario.getNombrePersona());
                UI.getCurrent().navigate("");
            } catch (ResourceAccessException e) {
                Notification.show("No se pudo conectar con el backend. ¿Está encendido?",
                        4000, Notification.Position.MIDDLE);
            } catch (RestClientException e) {
                formulario.setError(true);
            }
        });

        add(new H1("EcoParque"), formulario);
    }

    private LoginI18n crearTextos() {
        LoginI18n textos = LoginI18n.createDefault();
        textos.getForm().setTitle("Iniciar sesión");
        textos.getForm().setUsername("Usuario");
        textos.getForm().setPassword("Contraseña");
        textos.getForm().setSubmit("Ingresar");
        textos.getErrorMessage().setTitle("Usuario o clave incorrectos");
        textos.getErrorMessage().setMessage("Revisá los datos e intentá de nuevo.");
        return textos;
    }
}
