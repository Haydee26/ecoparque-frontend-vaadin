package com.example.seguridad;

import com.example.modelo.Usuario;
import com.example.servicio.BackendConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Valida usuario y clave consultando al backend con Basic Auth.
 */
@Service
public class AutenticacionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;

    public AutenticacionService(BackendConfig backend) {
        this.backend = backend;
    }

    /** Devuelve el usuario si existe, o null si no se lo encuentra. */
    public Usuario autenticar(String nombreUsuario, String clave) {
        HttpHeaders encabezados = new HttpHeaders();
        encabezados.setBasicAuth(nombreUsuario, clave);
        HttpEntity<Void> entidad = new HttpEntity<>(encabezados);

        Usuario[] usuarios = restTemplate.exchange(backend.url("/api/usuarios"),
                HttpMethod.GET, entidad, Usuario[].class).getBody();

        if (usuarios == null) {
            return null;
        }
        for (Usuario usuario : usuarios) {
            if (nombreUsuario.equals(usuario.getNombreUsuario())) {
                return usuario;
            }
        }
        return null;
    }
}
