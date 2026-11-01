package com.example.vista;

import com.example.modelo.Habitat;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.HabitatService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

@Route(value = "habitats", layout = MainLayout.class)
@PageTitle("Hábitats | EcoParque")
public class HabitatView extends VistaCrud<Habitat> {

    /** Mismos continentes que acepta el backend. */
    private static final List<String> CONTINENTES = List.of("África", "América del Norte", "América Central",
            "América del Sur", "Antártida", "Asia", "Europa", "Oceanía");

    public HabitatView(HabitatService servicio, SesionUsuario sesion) {
        super(Habitat.class, servicio, sesion);
    }

    @Override
    protected String titulo() {
        return "Gestión de Hábitats";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nuevo hábitat";
    }

    @Override
    protected String textoModificar() {
        return "Modificar hábitat";
    }

    @Override
    protected String describir(Habitat habitat) {
        return habitat.getNombre();
    }

    @Override
    protected void configurarColumnas(Grid<Habitat> tabla) {
        tabla.addColumn(Habitat::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(Habitat::getClima).setHeader("Clima");
        tabla.addColumn(Habitat::getVegetacionPredominante).setHeader("Vegetación predominante");
        tabla.addColumn(habitat -> String.join(", ", habitat.getContinentes())).setHeader("Continentes");
    }

    @Override
    protected Formulario<Habitat> crearFormulario(Habitat existente) {
        return new FormularioHabitat(existente);
    }

    private static class FormularioHabitat implements Formulario<Habitat> {

        private final TextField nombre = new TextField("Nombre");
        private final TextField clima = new TextField("Clima");
        private final TextField vegetacion = new TextField("Vegetación predominante");
        private final CheckboxGroup<String> continentes = new CheckboxGroup<>("Continentes");

        FormularioHabitat(Habitat existente) {
            continentes.setItems(CONTINENTES);
            if (existente != null) {
                nombre.setValue(valor(existente.getNombre()));
                clima.setValue(valor(existente.getClima()));
                vegetacion.setValue(valor(existente.getVegetacionPredominante()));
                // Se compara sin tildes ni mayúsculas para marcar también los datos cargados de otra forma.
                LinkedHashSet<String> marcados = new LinkedHashSet<>();
                for (String guardado : existente.getContinentes()) {
                    for (String oficial : CONTINENTES) {
                        if (clave(oficial).equals(clave(guardado))) {
                            marcados.add(oficial);
                        }
                    }
                }
                continentes.setValue(marcados);
            }
        }

        private static String clave(String texto) {
            return Normalizer.normalize(texto == null ? "" : texto.trim(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        }

        private static String valor(String texto) {
            return texto == null ? "" : texto;
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombre, clima, vegetacion, continentes};
        }

        @Override
        public String validar() {
            if (nombre.isEmpty()) {
                return "Completá el nombre del hábitat.";
            }
            if (continentes.getValue().isEmpty()) {
                return "Elegí al menos un continente.";
            }
            return null;
        }

        @Override
        public Habitat construir() {
            Habitat habitat = new Habitat();
            habitat.setNombre(nombre.getValue().trim());
            habitat.setClima(clima.getValue().trim());
            habitat.setVegetacionPredominante(vegetacion.getValue().trim());
            habitat.setContinentes(new LinkedHashSet<>(continentes.getValue()));
            return habitat;
        }
    }
}
