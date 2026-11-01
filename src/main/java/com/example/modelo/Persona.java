package com.example.modelo;

import java.util.Objects;

/**
 * Rol de un empleado en el parque (guia o cuidador). Los datos personales (nombre completo,
 * direccion y telefono) los entrega el backend solo para mostrar; se editan en Empleados.
 * Para dar el rol se manda el id del empleado.
 */
public abstract class Persona implements Identificable {

    private Long id;
    private Long empleadoId;
    private String nombre;
    private String direccion;
    private String telefono;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
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

    /** Dos personas son la misma si son de la misma clase y tienen el mismo id. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        Persona persona = (Persona) otro;
        return id != null && id.equals(persona.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
