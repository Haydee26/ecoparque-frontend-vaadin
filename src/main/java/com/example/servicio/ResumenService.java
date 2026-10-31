package com.example.servicio;

import com.example.modelo.Resumen;
import com.example.seguridad.SesionUsuario;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Pide al backend el resumen numerico del parque y permite configurar su area total
 * (solo lo permite al administrador).
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

    /**
     * Fija el nombre y el area total del parque.
     *
     * @throws IllegalStateException con el mensaje del backend si no se puede (por ejemplo, area menor a la ya ocupada)
     */
    public void configurarParque(String nombre, double areaTotal) {
        HttpHeaders encabezados = new HttpHeaders();
        encabezados.putAll(sesion.encabezados());
        encabezados.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> cuerpo = Map.of("nombre", nombre == null ? "" : nombre, "areaTotal", areaTotal);
        try {
            restTemplate.exchange(backend.url("/api/parque"), HttpMethod.PUT,
                    new HttpEntity<>(cuerpo, encabezados), Void.class);
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException(mensajeDe(e), e);
        }
    }

    /** Saca el texto "mensaje" del JSON de error que manda el backend. */
    private String mensajeDe(HttpStatusCodeException e) {
        Matcher m = Pattern.compile("\"mensaje\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(e.getResponseBodyAsString());
        return m.find() ? m.group(1).replace("\\\"", "\"") : "No se pudo guardar el parque.";
    }
}
