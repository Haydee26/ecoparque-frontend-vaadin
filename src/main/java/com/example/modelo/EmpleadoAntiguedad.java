package com.example.modelo;

/** Una persona del personal (guia o cuidador) con su fecha de ingreso y anios en el parque. */
public class EmpleadoAntiguedad {

    private Long id;
    private String nombre;
    private String tipo;
    private String fechaIngreso;
    private Long anios;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Long getAnios() {
        return anios;
    }

    public void setAnios(Long anios) {
        this.anios = anios;
    }
}
