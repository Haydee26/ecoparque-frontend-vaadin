package com.example.modelo;

import java.util.ArrayList;
import java.util.List;

/** Rol de usuario con sus permisos, tal como lo entrega el backend. */
public class Rol implements Identificable {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean base;
    private List<String> permisos = new ArrayList<>();
    private long usuarios;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isBase() {
        return base;
    }

    public void setBase(boolean base) {
        this.base = base;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public long getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(long usuarios) {
        this.usuarios = usuarios;
    }
}
