package com.example.modelo;

import java.util.Objects;

public class Habitat implements Identificable {

    private Long id;
    private String nombre;
    private String clima;
    private String vegetacionPredominante;

    @Override
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

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public String getVegetacionPredominante() {
        return vegetacionPredominante;
    }

    public void setVegetacionPredominante(String vegetacionPredominante) {
        this.vegetacionPredominante = vegetacionPredominante;
    }

    /** Dos habitats son el mismo si tienen el mismo id (necesario para las listas de seleccion). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Habitat)) {
            return false;
        }
        Habitat habitat = (Habitat) otro;
        return id != null && id.equals(habitat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
