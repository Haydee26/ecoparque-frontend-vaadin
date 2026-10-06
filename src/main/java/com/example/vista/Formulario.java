package com.example.vista;

import com.vaadin.flow.component.Component;

/**
 * Contrato de un formulario de alta y modificacion.
 * Cada pantalla define sus propios campos; la base se ocupa del resto.
 */
public interface Formulario<T> {

    /** Campos que se muestran en el dialogo. */
    Component[] campos();

    /** Devuelve un mensaje si hay algo mal cargado, o null si esta todo bien. */
    String validar();

    /** Arma el elemento a partir de lo que hay en los campos. */
    T construir();
}
