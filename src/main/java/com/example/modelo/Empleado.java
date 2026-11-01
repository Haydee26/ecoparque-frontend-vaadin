package com.example.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Una persona que trabaja en el parque, tal como la entrega el backend al administrador.
 * Las fechas viajan como texto ISO (aaaa-mm-dd). "anios" y "puestos" los calcula el backend.
 */
public class Empleado implements Identificable {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String fechaNacimiento;
    private String direccion;
    private String telefono;
    private String correo;
    private String fechaIngreso;
    private Long anios;
    private List<String> puestos = new ArrayList<>();
    /** Roles a dejar al guardar: "GUIA" y/o "CUIDADOR". */
    private List<String> roles = new ArrayList<>();

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public List<String> getPuestos() {
        return puestos;
    }

    public void setPuestos(List<String> puestos) {
        this.puestos = puestos;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /** "Nombre Apellido", como se muestra en listas y como se vincula con un usuario. */
    public String nombreCompleto() {
        return (nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido);
    }

    /** Dos empleados son el mismo si tienen el mismo id. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        return id != null && id.equals(((Empleado) otro).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
