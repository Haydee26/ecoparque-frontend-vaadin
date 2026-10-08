package com.example.vista;

import com.example.modelo.Especie;
import com.example.modelo.Habitat;
import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.EspecieService;
import com.example.servicio.HabitatService;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Route(value = "especies", layout = MainLayout.class)
@PageTitle("Especies | EcoParque")
public class EspecieView extends VistaCrud<Especie> {

    private final ZonaService zonaService;
    private final HabitatService habitatService;

    public EspecieView(EspecieService servicio, ZonaService zonaService,
            HabitatService habitatService, SesionUsuario sesion) {
        super(Especie.class, servicio, sesion);
        this.zonaService = zonaService;
        this.habitatService = habitatService;
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
    }

    /** Las opciones de zona y hábitats se piden al backend cada vez que se abre el formulario. */
    @Override
    protected Formulario<Especie> crearFormulario(Especie existente) {
        return new FormularioEspecie(existente, zonaService.listar(), habitatService.listar());
    }

    private static class FormularioEspecie implements Formulario<Especie> {

        private final TextField nombreEspanol = new TextField("Nombre en español");
        private final TextField nombreCientifico = new TextField("Nombre científico");
        private final TextArea descripcion = new TextArea("Descripción");
        private final ComboBox<Zona> zona = new ComboBox<>("Zona");
        private final CheckboxGroup<Habitat> habitats = new CheckboxGroup<>("Hábitats");

        FormularioEspecie(Especie existente, List<Zona> zonas, List<Habitat> todosLosHabitats) {
            zona.setItems(zonas);
            zona.setItemLabelGenerator(Zona::getNombre);
            habitats.setItems(todosLosHabitats);
            habitats.setItemLabelGenerator(Habitat::getNombre);

            if (existente != null) {
                nombreEspanol.setValue(valor(existente.getNombreEspanol()));
                nombreCientifico.setValue(valor(existente.getNombreCientifico()));
                descripcion.setValue(valor(existente.getDescripcion()));
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
            return new Component[]{nombreEspanol, nombreCientifico, descripcion, zona, habitats};
        }

        @Override
        public String validar() {
            if (nombreEspanol.isEmpty() || zona.isEmpty()) {
                return "Completá el nombre y elegí una zona.";
            }
            return null;
        }

        @Override
        public Especie construir() {
            Especie especie = new Especie();
            especie.setNombreEspanol(nombreEspanol.getValue().trim());
            especie.setNombreCientifico(nombreCientifico.getValue().trim());
            especie.setDescripcion(descripcion.getValue().trim());
            especie.setZona(zona.getValue());
            especie.setHabitats(new ArrayList<>(habitats.getValue()));
            return especie;
        }
    }
}
