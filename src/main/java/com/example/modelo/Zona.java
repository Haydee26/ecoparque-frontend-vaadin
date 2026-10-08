package com.example.modelo;

import java.util.Objects;

public class Zona implements Identificable {

    private Long id;
    private String nombre;
    private Double extension;

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

    public Double getExtension() {
        return extension;
    }

    public void setExtension(Double extension) {
        this.extension = extension;
    }

    /** Dos zonas son la misma si tienen el mismo id (necesario para las listas de seleccion). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Zona)) {
            return false;
        }
        Zona zona = (Zona) otro;
        return id != null && id.equals(zona.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
