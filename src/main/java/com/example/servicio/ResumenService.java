package com.example.servicio;

import com.example.modelo.Resumen;
import com.example.seguridad.SesionUsuario;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Pide al backend el resumen numerico del parque (solo lo permite al administrador).
 */
@Service
public class ResumenService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public ResumenService(BackendConfig backend, SesionUsuario sesion) {
        this.backend = backend;
        this.sesion = sesion;
    }

    public Resumen obtener() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        return restTemplate.exchange(backend.url("/api/resumen"), HttpMethod.GET, entidad, Resumen.class).getBody();
    }
}
