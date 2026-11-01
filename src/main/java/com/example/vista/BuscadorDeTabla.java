package com.example.vista;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.Collection;
import java.util.List;

/**
 * Buscador para cualquier tabla: filtra las filas que ya estan cargadas segun lo que se escribe,
 * mirando todos los datos de cada fila (tambien los de sus zonas, habitats, etc.).
 * No importan las mayusculas ni las tildes.
 */
final class BuscadorDeTabla<T> {

    private static final int PROFUNDIDAD_MAXIMA = 3;

    private final Grid<T> tabla;
    private final TextField campo = new TextField();
    private ListDataProvider<T> proveedor = new ListDataProvider<>(List.of());

    BuscadorDeTabla(Grid<T> tabla) {
        this.tabla = tabla;
        campo.setPlaceholder("Buscar...");
        campo.setPrefixComponent(VaadinIcon.SEARCH.create());
        campo.setClearButtonVisible(true);
        campo.setWidth("24rem");
        campo.setValueChangeMode(ValueChangeMode.LAZY);
        campo.addValueChangeListener(evento -> aplicarFiltro());
    }

    TextField campo() {
        return campo;
    }

    /** Muestra estos elementos en la tabla, ya filtrados por lo que haya escrito en el buscador. */
    void cargar(Collection<T> elementos) {
        proveedor = new ListDataProvider<>(elementos);
        tabla.setItems(proveedor);
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        String buscado = normalizar(campo.getValue());
        if (buscado.isEmpty()) {
            proveedor.clearFilters();
        } else {
            proveedor.setFilter(elemento -> normalizar(textoDe(elemento, 0)).contains(buscado));
        }
    }

    /** Junta todos los datos simples del elemento (y de lo que contiene) en un solo texto. */
    private static String textoDe(Object valor, int profundidad) {
        if (valor == null || profundidad > PROFUNDIDAD_MAXIMA) {
            return "";
        }
        if (valor instanceof CharSequence || valor instanceof Number || valor instanceof Boolean
                || valor instanceof Enum<?>) {
            return valor.toString();
        }
        if (valor instanceof Collection<?> coleccion) {
            StringBuilder texto = new StringBuilder();
            for (Object elemento : coleccion) {
                texto.append(' ').append(textoDe(elemento, profundidad + 1));
            }
            return texto.toString();
        }
        if (!valor.getClass().getName().startsWith("com.example.modelo")) {
            return "";
        }
        StringBuilder texto = new StringBuilder();
        for (Method metodo : valor.getClass().getMethods()) {
            String nombre = metodo.getName();
            boolean esGetter = metodo.getParameterCount() == 0
                    && (nombre.startsWith("get") || nombre.startsWith("is"))
                    && !nombre.equals("getClass") && !nombre.equals("getId");
            if (esGetter) {
                try {
                    texto.append(' ').append(textoDe(metodo.invoke(valor), profundidad + 1));
                } catch (ReflectiveOperationException | RuntimeException e) {
                    // si un dato no se puede leer, simplemente no entra en la busqueda
                }
            }
        }
        return texto.toString();
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
