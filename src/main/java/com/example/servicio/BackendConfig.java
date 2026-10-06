package com.example.servicio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Concentra la direccion del backend en un unico lugar.
 * El valor se lee de application.properties (propiedad backend.url).
 */
@Component
public class BackendConfig {

    @Value("${backend.url}")
    private String baseUrl;

    public String url(String ruta) {
        return baseUrl.replaceAll("/+$", "") + ruta;
    }
}
