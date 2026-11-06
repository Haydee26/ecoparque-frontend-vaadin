package com.example.servicio;

import com.example.modelo.Especie;
import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EspecieService extends ServicioRest<Especie> {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public EspecieService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/especies", Especie.class, Especie[].class);
        this.backend = backend;
        this.sesion = sesion;
    }

    /** Especies que un cuidador marcó como enfermas. */
    public List<Especie> listarEnfermas() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        Especie[] enfermas = restTemplate.exchange(backend.url("/api/especies/enfermas"),
                HttpMethod.GET, entidad, Especie[].class).getBody();
        return enfermas == null ? List.of() : Arrays.asList(enfermas);
    }

    /** El cuidador avisa que la especie está enferma y explica qué le pasa. */
    public Especie avisarEnferma(Long id, String observacion) {
        HttpEntity<Map<String, String>> entidad = new HttpEntity<>(
                Map.of("observacion", observacion), sesion.encabezados());
        return restTemplate.exchange(backend.url("/api/especies/" + id + "/enferma"),
                HttpMethod.PUT, entidad, Especie.class).getBody();
    }

    /** Se vuelve a marcar como sana. */
    public Especie marcarSana(Long id) {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        return restTemplate.exchange(backend.url("/api/especies/" + id + "/sana"),
                HttpMethod.PUT, entidad, Especie.class).getBody();
    }
}