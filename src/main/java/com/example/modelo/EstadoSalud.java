
package com.example.modelo;

/**
 * Estados de salud de un especimen. Tiene que tener los mismos valores que el
 * enum del backend, porque viajan por JSON con ese nombre ("SANO", "ENFERMO"...).
 */
public enum EstadoSalud {
    SANO("Sano"),
    ENFERMO("Enfermo"),
    EN_TRATAMIENTO("En tratamiento");
    
    private final String texto;
    
    EstadoSalud(String texto){
        this.texto = texto;
    }
    
    
        /** Como se muestra en pantalla. */
    public String getTexto() {
        return texto;
    }
    
}
