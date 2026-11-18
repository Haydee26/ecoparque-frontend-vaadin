package com.example.vista;

import com.example.modelo.Especimen;
import com.example.modelo.EstadoSalud;
import com.example.servicio.EspecimenService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.springframework.web.client.RestClientException;

/**
 * Ventana para cambiar la salud de un especimen (estado y observacion).
 * La usan dos pantallas: Especimenes y Salud de los especimenes.
 * Por eso esta en una clase aparte y no repetida en cada pantalla.
 */
public class DialogoSalud extends Dialog {

    /**
     * alGuardar: lo que tiene que hacer la pantalla que abrio la ventana
     * despues de guardar (normalmente, recargar su tabla).
     */
    public DialogoSalud(Especimen especimen, EspecimenService servicio, Runnable alGuardar) {
        setHeaderTitle("Salud de " + especimen.getNombre());

        ComboBox<EstadoSalud> estado = new ComboBox<>("Estado de salud");
        estado.setItems(EstadoSalud.values());
        estado.setItemLabelGenerator(EstadoSalud::getTexto);
        estado.setValue(especimen.getEstadoSalud());

        TextArea observacion = new TextArea("Observación");
        observacion.setWidth("400px");
        observacion.setValue(especimen.getObservacionSalud() == null ? "" : especimen.getObservacionSalud());

        add(new VerticalLayout(estado, observacion));

        Button cancelar = new Button("Cancelar", evento -> close());
        Button guardar = new Button("Guardar", evento -> {
            if (estado.isEmpty()) {
                Notification.show("Elegí el estado de salud.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (estado.getValue() != EstadoSalud.SANO && observacion.getValue().isBlank()) {
                Notification.show("Si no está sano, escribí qué le pasa.", 3000, Notification.Position.MIDDLE);
                return;
            }
            try {
                String texto = observacion.getValue().isBlank() ? null : observacion.getValue().trim();
                servicio.cambiarSalud(especimen.getId(), estado.getValue(), texto);
                close();
                alGuardar.run();
                Notification.show("Salud actualizada.", 3000, Notification.Position.TOP_CENTER);
            } catch (RestClientException e) {
                Notification.show("No se pudo actualizar la salud.", 4000, Notification.Position.MIDDLE);
            }
        });
        getFooter().add(cancelar, guardar);
    }
}