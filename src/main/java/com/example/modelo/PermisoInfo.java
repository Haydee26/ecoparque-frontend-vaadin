package com.example.modelo;

/** Un permiso del catálogo del backend (por ejemplo ZONAS_EDITAR: sección Zonas, acción Editar). */
public class PermisoInfo {

    private String codigo;
    private String seccion;
    private String accion;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}
