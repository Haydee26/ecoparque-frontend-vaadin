package com.example.vista;

import com.example.modelo.Especie;
import com.example.modelo.Habitat;
import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.EspecieService;
import com.example.servicio.HabitatService;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.web.client.RestClientException;
// ARREGLO: saqué "import java.time.LocalDate;" porque esta pantalla no lo usa
// (la fecha se guarda en el modelo, no acá). Un import sin usar no rompe, pero ensucia.

@Route(value = "especies", layout = MainLayout.class)
@PageTitle("Especies | EcoParque")
public class EspecieView extends VistaCrud<Especie> {

    private final ZonaService zonaService;
    private final HabitatService habitatService;
    private final EspecieService especieService;
    private final SesionUsuario sesion;

    public EspecieView(EspecieService servicio, ZonaService zonaService,
            HabitatService habitatService, SesionUsuario sesion) {
        super(Especie.class, servicio, sesion);
        this.zonaService = zonaService;
        this.habitatService = habitatService;
        this.especieService = servicio;
        this.sesion = sesion;
    }

    @Override
    protected String titulo() {
        return "Gestión de Especies";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nueva especie";
    }

    @Override
    protected String textoModificar() {
        return "Modificar especie";
    }

    @Override
    protected String describir(Especie especie) {
        return especie.getNombreEspanol();
    }

    @Override
    protected void configurarColumnas(Grid<Especie> tabla) {
        tabla.addColumn(Especie::getNombreEspanol).setHeader("Nombre en español").setSortable(true);
        tabla.addColumn(Especie::getNombreCientifico).setHeader("Nombre científico");
        tabla.addColumn(especie -> especie.getZona() == null ? "" : especie.getZona().getNombre())
                .setHeader("Zona");
        tabla.addColumn(especie -> Formatos.unir(especie.getHabitats(), Habitat::getNombre))
                .setHeader("Hábitats");
        tabla.addColumn(especie -> especie.getCantidadEjemplares() == null ? 0 : especie.getCantidadEjemplares())
                .setHeader("Ejemplares").setSortable(true);

        // ARREGLO: acomodé la sangría de esta columna (estaba corrida), no cambia nada del funcionamiento.
        // Muestra "Sana" o "Enferma: lo que escribió el cuidador".
        tabla.addColumn(especie -> especie.isEnferma()
                ? "Enferma: " + (especie.getObservacionSalud() == null ? "" : especie.getObservacionSalud())
                : "Sana").setHeader("Estado");

        // Solo quien tiene el permiso SALUD_EDITAR (el cuidador, el veterinario y el administrador) ve el botón.
        if (sesion.tiene("SALUD_EDITAR")) {
            tabla.addComponentColumn(this::crearBotonSalud).setHeader("Salud").setAutoWidth(true);
        }
    }

    // ARREGLO: ESTO ES LO QUE TE FALTABA (paso 11d). Son los tres métodos que usa la columna "Salud".
    /**
     * El botón cambia según el estado: si está sana ofrece avisar, si está
     * enferma ofrece marcarla sana.
     */
    private Button crearBotonSalud(Especie especie) {
        if (especie.isEnferma()) {
            return new Button("Marcar sana", evento -> marcarSana(especie));
        }
        return new Button("Avisar enferma", evento -> avisarEnferma(especie));
    }

    /**
     * Abre una ventanita para escribir qué le pasa a la especie y manda el
     * aviso al backend.
     */

    private void avisarEnferma(Especie especie) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle("Avisar que está enferma: " + especie.getNombreEspanol());
        TextArea observacion = new TextArea("¿Qué le pasa?");
        observacion.setWidthFull();
        dialogo.add(observacion);

        Button cancelar = new Button("Cancelar", evento -> dialogo.close());
        Button avisar = new Button("Avisar", evento -> {
            if (observacion.isEmpty()) {
                Notification.show("Escribí qué le pasa.", 3000, Notification.Position.MIDDLE);
                return;
            }
            try {
                especieService.avisarEnferma(especie.getId(), observacion.getValue().trim());
                dialogo.close();
                actualizarTabla(); // vuelve a pedir la lista para que se vea el cambio
                Notification.show("Aviso enviado.", 3000, Notification.Position.TOP_CENTER);
            } catch (RestClientException e) {
                Notification.show("No se pudo enviar el aviso.", 4000, Notification.Position.MIDDLE);
            }
        });
        dialogo.getFooter().add(cancelar, avisar);
        dialogo.open();
    }

    /**
     * Vuelve a dejar la especie como sana.
     */
    private void marcarSana(Especie especie) {
        try {
            especieService.marcarSana(especie.getId());
            actualizarTabla();
            Notification.show("Marcada como sana.", 3000, Notification.Position.TOP_CENTER);
        } catch (RestClientException e) {
            Notification.show("No se pudo actualizar.", 4000, Notification.Position.MIDDLE);
        }
    }

    /**
     * Las opciones de zona y hábitats se piden al backend cada vez que se abre
     * el formulario.
     */
    @Override
    protected Formulario<Especie> crearFormulario(Especie existente) {
        return new FormularioEspecie(existente, zonaService.listar(), habitatService.listar());
    }

    private static class FormularioEspecie implements Formulario<Especie> {

        private final TextField nombreEspanol = new TextField("Nombre en español");
        private final TextField nombreCientifico = new TextField("Nombre científico");
        private final TextArea descripcion = new TextArea("Descripción");
        private final IntegerField ejemplares = new IntegerField("Cantidad de ejemplares");
        private final ComboBox<Zona> zona = new ComboBox<>("Zona");
        private final CheckboxGroup<Habitat> habitats = new CheckboxGroup<>("Hábitats");

        FormularioEspecie(Especie existente, List<Zona> zonas, List<Habitat> todosLosHabitats) {
            zona.setItems(zonas);
            zona.setItemLabelGenerator(Zona::getNombre);
            habitats.setItems(todosLosHabitats);
            habitats.setItemLabelGenerator(Habitat::getNombre);
            ejemplares.setMin(0);
            ejemplares.setStepButtonsVisible(true);
            ejemplares.setValue(0);

            if (existente != null) {
                nombreEspanol.setValue(valor(existente.getNombreEspanol()));
                nombreCientifico.setValue(valor(existente.getNombreCientifico()));
                descripcion.setValue(valor(existente.getDescripcion()));
                ejemplares.setValue(existente.getCantidadEjemplares() == null ? 0 : existente.getCantidadEjemplares());
                zona.setValue(existente.getZona());
                if (existente.getHabitats() != null) {
                    habitats.setValue(new HashSet<>(existente.getHabitats()));
                }
            }
        }

        private static String valor(String texto) {
            return texto == null ? "" : texto;
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombreEspanol, nombreCientifico, descripcion, ejemplares, zona, habitats};
        }

        @Override
        public String validar() {
            if (nombreEspanol.isEmpty() || zona.isEmpty()) {
                return "Completá el nombre y elegí una zona.";
            }
            if (ejemplares.isEmpty() || ejemplares.getValue() < 0) {
                return "La cantidad de ejemplares tiene que ser 0 o mayor.";
            }
            return null;
        }

        @Override
        public Especie construir() {
            Especie especie = new Especie();
            especie.setNombreEspanol(nombreEspanol.getValue().trim());
            especie.setNombreCientifico(nombreCientifico.getValue().trim());
            especie.setDescripcion(descripcion.getValue().trim());
            especie.setCantidadEjemplares(ejemplares.getValue());
            especie.setZona(zona.getValue());
            especie.setHabitats(new ArrayList<>(habitats.getValue()));
            return especie;
        }
    }
}
