package com.example.seguridad;

import com.example.modelo.Usuario;
import com.example.servicio.BackendConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Valida usuario y clave preguntandole al backend quien es.
 * Si la clave es incorrecta, el backend contesta 401 y RestTemplate lanza una excepcion.
 */
@Service
public class AutenticacionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;

    public AutenticacionService(BackendConfig backend) {
        this.backend = backend;
    }

    /** Devuelve los datos del usuario que se identifica (sin la clave). */
    public Usuario autenticar(String nombreUsuario, String clave) {
        HttpHeaders encabezados = new HttpHeaders();
        encabezados.setBasicAuth(nombreUsuario, clave);
        HttpEntity<Void> entidad = new HttpEntity<>(encabezados);

        return restTemplate.exchange(backend.url("/api/usuarios/yo"),
                HttpMethod.GET, entidad, Usuario.class).getBody();
    }
}
