package com.example.modelo;

import java.util.Objects;

public class Zona implements Identificable {

    private Long id;
    private String nombre;
    private Double extension;
    private Integer columna;
    private Integer fila;
    private Integer ancho;
    private Integer alto;

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

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getAncho() {
        return ancho;
    }

    public void setAncho(Integer ancho) {
        this.ancho = ancho;
    }

    public Integer getAlto() {
        return alto;
    }

    public void setAlto(Integer alto) {
        this.alto = alto;
    }

    /** True si la zona ya tiene su lugar en el mapa. */
    public boolean ubicada() {
        return columna != null && fila != null && ancho != null && alto != null;
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
