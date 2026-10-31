package com.example.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Resumen numerico del parque, tal como lo devuelve el backend (GET /api/resumen).
 */
public class Resumen {

    private Long animalesTotal;
    private Long especiesTotal;
    private Long personalTotal;
    private Long guias;
    private Long cuidadores;
    private Double areaTotal;
    private Double areaUsada;
    private Double areaLibre;
    private Double ocupacionPorcentaje;
    private List<String> alertas = new ArrayList<>();

    public Long getAnimalesTotal() {
        return animalesTotal;
    }

    public void setAnimalesTotal(Long animalesTotal) {
        this.animalesTotal = animalesTotal;
    }

    public Long getEspeciesTotal() {
        return especiesTotal;
    }

    public void setEspeciesTotal(Long especiesTotal) {
        this.especiesTotal = especiesTotal;
    }

    public Long getPersonalTotal() {
        return personalTotal;
    }

    public void setPersonalTotal(Long personalTotal) {
        this.personalTotal = personalTotal;
    }

    public Long getGuias() {
        return guias;
    }

    public void setGuias(Long guias) {
        this.guias = guias;
    }

    public Long getCuidadores() {
        return cuidadores;
    }

    public void setCuidadores(Long cuidadores) {
        this.cuidadores = cuidadores;
    }

    public Double getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(Double areaTotal) {
        this.areaTotal = areaTotal;
    }

    public Double getAreaUsada() {
        return areaUsada;
    }

    public void setAreaUsada(Double areaUsada) {
        this.areaUsada = areaUsada;
    }

    public Double getAreaLibre() {
        return areaLibre;
    }

    public void setAreaLibre(Double areaLibre) {
        this.areaLibre = areaLibre;
    }

    public Double getOcupacionPorcentaje() {
        return ocupacionPorcentaje;
    }

    public void setOcupacionPorcentaje(Double ocupacionPorcentaje) {
        this.ocupacionPorcentaje = ocupacionPorcentaje;
    }

    public List<String> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<String> alertas) {
        this.alertas = alertas;
    }
}
