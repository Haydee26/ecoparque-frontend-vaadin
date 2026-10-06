package com.example.vista;

import com.example.modelo.Habitat;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.HabitatService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "habitats", layout = MainLayout.class)
@PageTitle("Hábitats | EcoParque")
public class HabitatView extends VistaCrud<Habitat> {

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
    }

    @Override
    protected Formulario<Habitat> crearFormulario(Habitat existente) {
        return new FormularioHabitat(existente);
    }

    private static class FormularioHabitat implements Formulario<Habitat> {

        private final TextField nombre = new TextField("Nombre");
        private final TextField clima = new TextField("Clima");
        private final TextField vegetacion = new TextField("Vegetación predominante");

        FormularioHabitat(Habitat existente) {
            if (existente != null) {
                nombre.setValue(valor(existente.getNombre()));
                clima.setValue(valor(existente.getClima()));
                vegetacion.setValue(valor(existente.getVegetacionPredominante()));
            }
        }

        private static String valor(String texto) {
            return texto == null ? "" : texto;
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombre, clima, vegetacion};
        }

        @Override
        public String validar() {
            if (nombre.isEmpty()) {
                return "Completá el nombre del hábitat.";
            }
            return null;
        }

        @Override
        public Habitat construir() {
            Habitat habitat = new Habitat();
            habitat.setNombre(nombre.getValue().trim());
            habitat.setClima(clima.getValue().trim());
            habitat.setVegetacionPredominante(vegetacion.getValue().trim());
            return habitat;
        }
    }
}
