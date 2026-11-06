package com.example.servicio;

import com.example.modelo.PermisoInfo;
import com.example.modelo.Rol;
import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RolService extends ServicioRest<Rol> {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public RolService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/roles", Rol.class, Rol[].class);
        this.backend = backend;
        this.sesion = sesion;
    }

    /** Catálogo completo de permisos (cada sección con sus acciones Ver y Editar). */
    public List<PermisoInfo> catalogo() {
        PermisoInfo[] permisos = restTemplate.exchange(backend.url("/api/roles/permisos"), HttpMethod.GET,
                new HttpEntity<>(sesion.encabezados()), PermisoInfo[].class).getBody();
        return permisos == null ? List.of() : Arrays.asList(permisos);
    }
}
