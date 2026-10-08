package com.example.modelo;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class Especie implements Identificable {

    private Long id;
    private String nombreEspanol;
    private String nombreCientifico;
    private String descripcion;
    private Zona zona;
    private List<Habitat> habitats = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEspanol() {
        return nombreEspanol;
    }

    public void setNombreEspanol(String nombreEspanol) {
        this.nombreEspanol = nombreEspanol;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public List<Habitat> getHabitats() {
        return habitats;
    }

    public void setHabitats(List<Habitat> habitats) {
        this.habitats = habitats;
    }

    /** Dos elementos son el mismo si tienen el mismo id (necesario para las listas de seleccion). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Especie)) {
            return false;
        }
        Especie elemento = (Especie) otro;
        return id != null && id.equals(elemento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
