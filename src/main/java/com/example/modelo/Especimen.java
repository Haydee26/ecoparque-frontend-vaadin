
package com.example.modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Copia del especimen del backend: es lo que llega y sale por JSON.
 * No tiene anotaciones de JPA porque Vaadin no habla con la base, habla con el backend.
 */
public class Especimen implements Identificable{
    
    private Long id;
    private String nombre;
    private Especie especie;
    private LocalDate fechaIngreso;
    private EstadoSalud estadoSalud = EstadoSalud.SANO;
    private String observacionSalud;
    private String avisadoPor;
    private LocalDate fechaAvisoSalud;

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

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public EstadoSalud getEstadoSalud() {
        return estadoSalud;
    }

    public void setEstadoSalud(EstadoSalud estadoSalud) {
        this.estadoSalud = estadoSalud;
    }

    public String getObservacionSalud() {
        return observacionSalud;
    }

    public void setObservacionSalud(String observacionSalud) {
        this.observacionSalud = observacionSalud;
    }
    
        public String getAvisadoPor() {
        return avisadoPor;
    }

    public void setAvisadoPor(String avisadoPor) {
        this.avisadoPor = avisadoPor;
    }

    public LocalDate getFechaAvisoSalud() {
        return fechaAvisoSalud;
    }

    public void setFechaAvisoSalud(LocalDate fechaAvisoSalud) {
        this.fechaAvisoSalud = fechaAvisoSalud;
    }
    
      /* Dos especimenes son el mismo si tienen el mismo id (necesario para las tablas y listas). */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Especimen)) {
            return false;
        }
        Especimen elemento = (Especimen) otro;
        return id != null && id.equals(elemento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
