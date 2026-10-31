package com.example.vista;

import com.example.modelo.MisDatos;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.PersonalService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Inicio | EcoParque")
public class InicioView extends VerticalLayout {

    public InicioView(SesionUsuario sesion, PersonalService personalService) {
        add(new H2("Sistema de gestión del Ecoparque"),
                new Paragraph("Bienvenido/a, " + sesion.nombrePersona() + ". Elegí una sección del menú."));

        // Cada persona ve sus propios datos.
        add(panelMisDatos(personalService));
    }

    private VerticalLayout panelMisDatos(PersonalService personalService) {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(false);
        panel.add(new H3("Mis datos"));

        MisDatos datos;
        try {
            datos = personalService.misDatos();
        } catch (RuntimeException e) {
            panel.add(new Paragraph("No se pudieron cargar tus datos. Verificá que el backend esté funcionando."));
            return panel;
        }
        if (datos == null) {
            panel.add(new Paragraph("El backend no devolvió tus datos."));
            return panel;
        }

        VerticalLayout texto = new VerticalLayout();
        texto.setPadding(false);
        texto.setSpacing(false);
        Span nombre = new Span(datos.getNombre() == null ? "" : datos.getNombre());
        nombre.getStyle().set("font-size", "1.3em").set("font-weight", "700").set("color", "#2f4a24");
        texto.add(nombre, linea("Usuario", datos.getNombreUsuario()), linea("Rol", datos.getRol()));

        boolean tieneDatosPersonales = datos.getDireccion() != null || datos.getTelefono() != null
                || datos.getFechaIngreso() != null;
        if (tieneDatosPersonales) {
            texto.add(linea("Domicilio", datos.getDireccion()),
                    linea("Teléfono", datos.getTelefono()),
                    linea("Fecha de ingreso", Formatos.fecha(datos.getFechaIngreso())));
        } else {
            texto.add(new Span("No hay datos personales cargados para este usuario."));
        }
        if (!datos.getItinerarios().isEmpty()) {
            texto.add(linea("Mis itinerarios", unir(datos.getItinerarios())));
        }
        if (!datos.getEspecies().isEmpty()) {
            texto.add(linea("Especies a mi cargo", unir(datos.getEspecies())));
        }

        HorizontalLayout contenido = new HorizontalLayout(espacioFoto(), texto);
        contenido.setWrap(true);
        contenido.setAlignItems(FlexComponent.Alignment.START);
        panel.add(contenido);
        return panel;
    }

    /** Espacio reservado para la foto de la persona. */
    private Component espacioFoto() {
        Div marco = new Div(VaadinIcon.USER.create(), new Span("Sin foto"));
        marco.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "6px")
                .set("width", "140px")
                .set("height", "140px")
                .set("border-radius", "50%")
                .set("background", "#e8e2d0")
                .set("border", "1px solid #d9d2bd")
                .set("color", "#6b6b6b");
        return marco;
    }

    private Component linea(String etiqueta, String valor) {
        Span clave = new Span(etiqueta + ": ");
        clave.getStyle().set("font-weight", "600");
        Span dato = new Span(valor == null || valor.isBlank() ? "No cargado" : valor);
        return new Div(clave, dato);
    }

    private String unir(List<String> elementos) {
        return String.join(", ", elementos);
    }
}
