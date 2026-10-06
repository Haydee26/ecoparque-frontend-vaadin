package com.example.modelo;

/**
 * Datos que comparten las personas del ecoparque (guias y cuidadores).
 * La fecha de ingreso viaja como texto ISO (aaaa-mm-dd), igual que en el backend.
 */
public abstract class Persona implements Identificable {

    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String fechaIngreso;

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
}
