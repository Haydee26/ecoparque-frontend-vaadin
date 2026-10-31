package com.example.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos de la persona que inicio sesion, tal como los entrega el backend
 * (GET /api/personal/yo). La fecha de ingreso viaja como texto ISO (aaaa-mm-dd).
 */
public class MisDatos {

    private String nombreUsuario;
    private String rol;
    private String nombre;
    private String direccion;
    private String telefono;
    private String fechaIngreso;
    private List<String> itinerarios = new ArrayList<>();
    private List<String> especies = new ArrayList<>();

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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

    public List<String> getItinerarios() {
        return itinerarios;
    }

    public void setItinerarios(List<String> itinerarios) {
        this.itinerarios = itinerarios;
    }

    public List<String> getEspecies() {
        return especies;
    }

    public void setEspecies(List<String> especies) {
        this.especies = especies;
    }
}
