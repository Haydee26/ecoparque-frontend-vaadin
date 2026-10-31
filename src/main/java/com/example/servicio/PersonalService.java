package com.example.servicio;

import com.example.modelo.GuiaPublica;
import com.example.modelo.MisDatos;
import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Pide al backend los datos del personal que puede ver cualquier usuario con sesion:
 * la lista de guias sin datos personales y los datos de quien ingreso.
 */
@Service
public class PersonalService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public PersonalService(BackendConfig backend, SesionUsuario sesion) {
        this.backend = backend;
        this.sesion = sesion;
    }

    public List<GuiaPublica> guiasPublicas() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        GuiaPublica[] guias = restTemplate.exchange(backend.url("/api/guias/publico"),
                HttpMethod.GET, entidad, GuiaPublica[].class).getBody();
        return guias == null ? List.of() : Arrays.asList(guias);
    }

    public MisDatos misDatos() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        return restTemplate.exchange(backend.url("/api/personal/yo"),
                HttpMethod.GET, entidad, MisDatos.class).getBody();
    }
}
