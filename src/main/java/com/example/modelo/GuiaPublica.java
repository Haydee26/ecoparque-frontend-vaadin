package com.example.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Lo que se muestra de una guia a quien no es administrador:
 * nombre y de que es guia. No incluye datos personales.
 */
public class GuiaPublica {

    private Long id;
    private String nombre;
    private List<String> itinerarios = new ArrayList<>();
    private List<String> zonas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(List<String> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public List<String> getZonas() {
        return zonas;
    }

    public void setZonas(List<String> zonas) {
        this.zonas = zonas;
    }
}
