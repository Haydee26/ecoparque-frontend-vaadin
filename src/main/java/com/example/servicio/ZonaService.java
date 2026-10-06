package com.example.servicio;

import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Unica responsabilidad: hablar con la API de zonas del backend.
 */
@Service
public class ZonaService {

    private static final String RUTA = "/api/zonas";

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public ZonaService(BackendConfig backend, SesionUsuario sesion) {
        this.backend = backend;
        this.sesion = sesion;
    }

    public List<Zona> listar() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        Zona[] zonas = restTemplate.exchange(backend.url(RUTA), HttpMethod.GET, entidad, Zona[].class).getBody();
        return zonas == null ? List.of() : Arrays.asList(zonas);
    }

    public Zona crear(Zona zona) {
        HttpEntity<Zona> entidad = new HttpEntity<>(zona, sesion.encabezados());
        return restTemplate.exchange(backend.url(RUTA), HttpMethod.POST, entidad, Zona.class).getBody();
    }

    public Zona actualizar(Long id, Zona zona) {
        HttpEntity<Zona> entidad = new HttpEntity<>(zona, sesion.encabezados());
        return restTemplate.exchange(backend.url(RUTA + "/" + id), HttpMethod.PUT, entidad, Zona.class).getBody();
    }

    public void eliminar(Long id) {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        restTemplate.exchange(backend.url(RUTA + "/" + id), HttpMethod.DELETE, entidad, Void.class);
    }
}
