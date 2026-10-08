package com.example.modelo;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class Itinerario implements Identificable {

    private Long id;
    private String codigo;
    private Integer duracionMinutos;
    private Double longitudMetros;
    private Integer maximoVisitantes;
    private Integer cantidadEspecies;
    private List<Zona> zonas = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Double getLongitudMetros() {
        return longitudMetros;
    }

    public void setLongitudMetros(Double longitudMetros) {
        this.longitudMetros = longitudMetros;
    }

    public Integer getMaximoVisitantes() {
        return maximoVisitantes;
    }

    public void setMaximoVisitantes(Integer maximoVisitantes) {
        this.maximoVisitantes = maximoVisitantes;
    }

    public Integer getCantidadEspecies() {
        return cantidadEspecies;
    }

    public void setCantidadEspecies(Integer cantidadEspecies) {
        this.cantidadEspecies = cantidadEspecies;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(List<Zona> zonas) {
        this.zonas = zonas;
    }

    /** Dos elementos son el mismo si tienen el mismo id (necesario para las listas de seleccion). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Itinerario)) {
            return false;
        }
        Itinerario elemento = (Itinerario) otro;
        return id != null && id.equals(elemento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
