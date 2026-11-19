package com.example.servicio;

import com.example.modelo.Especimen;
import com.example.modelo.EstadoSalud;
import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Habla con /api/especimenes del backend. Listar, crear, modificar y borrar los
 * hereda de ServicioRest; aca solo se agregan las dos operaciones de salud.
 */
@Service
public class EspecimenService extends ServicioRest<Especimen> {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public EspecimenService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/especimenes", Especimen.class, Especimen[].class);
        this.backend = backend; // direccion 
        this.sesion = sesion; // firma
    }

    /**
     * Los especimenes enfermos o en tratamiento (lo que ve el veterinario).
     */
    public List<Especimen> listarNoSanos() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        Especimen[] lista = restTemplate.exchange(backend.url("/api/especimenes/salud"),
                HttpMethod.GET, entidad, Especimen[].class).getBody();
        return lista == null ? List.of() : Arrays.asList(lista);
    }

    /**
     * Cambia solo el estado de salud y la observacion de un especimen.
     */
    public Especimen cambiarSalud(Long id, EstadoSalud estado, String observacion) {
        // HashMap y no Map.of: Map.of no acepta valores null, y la observacion puede venir vacia.
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("estado", estado);
        cuerpo.put("observacion", observacion);
        HttpEntity<Map<String, Object>> entidad = new HttpEntity<>(cuerpo, sesion.encabezados());
        return restTemplate.exchange(backend.url("/api/especimenes/" + id + "/salud"),
                HttpMethod.PUT, entidad, Especimen.class).getBody();
    }

}
