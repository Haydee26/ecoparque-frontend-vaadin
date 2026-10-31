package com.example.vista;

import com.example.modelo.Resumen;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.ResumenService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.Locale;

/**
 * Registro numerico del parque: animales, personal y area. Solo para el administrador.
 */
@Route(value = "registro-parque", layout = MainLayout.class)
@PageTitle("Registro del parque | EcoParque")
@SoloAdministrador
public class RegistroParqueView extends VerticalLayout {

    private static final Locale LOCAL = Locale.of("es", "AR");

    public RegistroParqueView(ResumenService resumenService) {
        add(new H2("Registro del parque"), panelRegistro(resumenService));
    }

    private VerticalLayout panelRegistro(ResumenService resumenService) {
        VerticalLayout panel = new VerticalLayout();
        panel.setPadding(false);

        Resumen resumen;
        try {
            resumen = resumenService.obtener();
        } catch (RuntimeException e) {
            panel.add(new Paragraph("No se pudo cargar el resumen del parque. Verificá que el backend esté funcionando."));
            return panel;
        }
        if (resumen == null) {
            panel.add(new Paragraph("El backend no devolvió datos."));
            return panel;
        }

        HorizontalLayout tarjetas = new HorizontalLayout();
        tarjetas.setWrap(true);
        tarjetas.add(
                tarjeta("Animales", numero(resumen.getAnimalesTotal()),
                        numero(resumen.getEspeciesTotal()) + " especies"),
                tarjeta("Personal", numero(resumen.getPersonalTotal()),
                        numero(resumen.getGuias()) + " guías · " + numero(resumen.getCuidadores()) + " cuidadores"),
                tarjeta("Área usada", metros(resumen.getAreaUsada()),
                        resumen.getAreaTotal() == null ? "Falta configurar el área total"
                        : "de " + metros(resumen.getAreaTotal())),
                tarjeta("Área libre", metros(resumen.getAreaLibre()),
                        resumen.getOcupacionPorcentaje() == null ? ""
                        : "Ocupación: " + String.format(LOCAL, "%.1f", resumen.getOcupacionPorcentaje()) + "%"));
        panel.add(tarjetas);

        if (resumen.getOcupacionPorcentaje() != null) {
            ProgressBar barra = new ProgressBar(0, 100, Math.min(100, resumen.getOcupacionPorcentaje()));
            barra.setWidth("420px");
            panel.add(new Span("Ocupación del área del parque"), barra);
        }

        panel.add(new H3("Alertas"));
        if (resumen.getAlertas() == null || resumen.getAlertas().isEmpty()) {
            panel.add(new Paragraph("Sin alertas: todo en orden."));
        } else {
            for (String alerta : resumen.getAlertas()) {
                Span linea = new Span("⚠ " + alerta);
                linea.getStyle().set("color", "#a8432d");
                panel.add(linea);
            }
        }
        return panel;
    }

    private Div tarjeta(String titulo, String valor, String detalle) {
        Span etiqueta = new Span(titulo);
        etiqueta.getStyle().set("color", "#5b6b4f").set("font-size", "0.9em");
        Span grande = new Span(valor);
        grande.getStyle().set("font-size", "1.8em").set("font-weight", "700").set("color", "#2f4a24");
        Span chico = new Span(detalle);
        chico.getStyle().set("color", "#6b6b6b").set("font-size", "0.85em");

        Div tarjeta = new Div(etiqueta, grande, chico);
        tarjeta.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "4px")
                .set("min-width", "200px")
                .set("padding", "16px")
                .set("background", "#fbf7ee")
                .set("border", "1px solid #d9d2bd")
                .set("border-radius", "8px");
        return tarjeta;
    }

    private String numero(Long valor) {
        return String.format(LOCAL, "%,d", valor == null ? 0L : valor);
    }

    private String metros(Double valor) {
        return valor == null ? "-" : String.format(LOCAL, "%,.2f m²", valor);
    }
}
