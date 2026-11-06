package com.example.seguridad;

import com.example.vista.AsignacionGuiaView;
import com.example.vista.CuidadorView;
import com.example.vista.EmpleadoView;
import com.example.vista.EspecieView;
import com.example.vista.GuiaView;
import com.example.vista.HabitatView;
import com.example.vista.ItinerarioView;
import com.example.vista.MapaParqueView;
import com.example.vista.RegistroParqueView;
import com.example.vista.ResponsabilidadCuidadorView;
import com.example.vista.RolView;
import com.example.vista.UsuarioView;
import com.example.vista.ZonaView;
import com.example.vista.EspeciesEnfermasView;
import java.util.Map;

/**
 * Dice a qué sección de permisos pertenece cada pantalla. Para entrar a una pantalla hace falta
 * el permiso SECCION_VER; para ver los botones de alta, baja y modificación, SECCION_EDITAR.
 * Las pantallas que no figuran acá (inicio, login, guías públicas) las puede abrir cualquier usuario con sesión.
 */
public final class Permisos {

    private static final Map<Class<?>, String> SECCIONES = Map.ofEntries(
            Map.entry(ZonaView.class, "ZONAS"),
            Map.entry(MapaParqueView.class, "ZONAS"),
            Map.entry(HabitatView.class, "HABITATS"),
            Map.entry(EspecieView.class, "ESPECIES"),
            Map.entry(ItinerarioView.class, "ITINERARIOS"),
            Map.entry(AsignacionGuiaView.class, "ASIGNACIONES"),
            Map.entry(ResponsabilidadCuidadorView.class, "RESPONSABILIDADES"),
            Map.entry(EmpleadoView.class, "EMPLEADOS"),
            Map.entry(GuiaView.class, "GUIAS"),
            Map.entry(CuidadorView.class, "CUIDADORES"),
            Map.entry(UsuarioView.class, "USUARIOS"),
            Map.entry(RolView.class, "ROLES"),
            Map.entry(RegistroParqueView.class, "PARQUE"),
            Map.entry(EspeciesEnfermasView.class, "SALUD"));

    private Permisos() {
    }

    /** Sección de la pantalla, o null si no pide permiso. */
    public static String seccionDe(Class<?> pantalla) {
        return SECCIONES.get(pantalla);
    }
}
