package com.example.seguridad;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca las pantallas que solo puede abrir un administrador.
 * ControlDeAcceso la consulta antes de mostrar cada pantalla.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SoloAdministrador {
}
