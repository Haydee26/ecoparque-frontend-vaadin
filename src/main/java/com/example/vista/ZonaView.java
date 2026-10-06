package com.example.vista;

import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.ZonaService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "zonas", layout = MainLayout.class)
@PageTitle("Zonas | EcoParque")
public class ZonaView extends VistaCrud<Zona> {

    public ZonaView(ZonaService servicio, SesionUsuario sesion) {
        super(Zona.class, servicio, sesion);
    }

    @Override
    protected String titulo() {
        return "Gestión de Zonas";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nueva zona";
    }

    @Override
    protected String textoModificar() {
        return "Modificar zona";
    }

    @Override
    protected String describir(Zona zona) {
        return zona.getNombre();
    }

    @Override
    protected void configurarColumnas(Grid<Zona> tabla) {
        tabla.addColumn(Zona::getNombre).setHeader("Nombre").setSortable(true);
        tabla.addColumn(zona -> Formatos.metrosCuadrados(zona.getExtension())).setHeader("Extensión");
    }

    @Override
    protected Formulario<Zona> crearFormulario(Zona existente) {
        return new FormularioZona(existente);
    }

    private static class FormularioZona implements Formulario<Zona> {

        private final TextField nombre = new TextField("Nombre");
        private final NumberField extension = new NumberField("Extensión (m²)");

        FormularioZona(Zona existente) {
            if (existente != null) {
                nombre.setValue(existente.getNombre() == null ? "" : existente.getNombre());
                if (existente.getExtension() != null) {
                    extension.setValue(existente.getExtension());
                }
            }
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombre, extension};
        }

        @Override
        public String validar() {
            if (nombre.isEmpty() || extension.isEmpty() || extension.getValue() <= 0) {
                return "Completá el nombre y una extensión mayor a cero.";
            }
            return null;
        }

        @Override
        public Zona construir() {
            Zona zona = new Zona();
            zona.setNombre(nombre.getValue().trim());
            zona.setExtension(extension.getValue());
            return zona;
        }
    }
}
