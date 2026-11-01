package com.example.servicio;

import com.example.modelo.Empleado;
import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Base de datos de empleados en el backend: alta, baja, modificacion y busqueda
 * por nombre, apellido o DNI.
 */
@Service
public class EmpleadoService extends ServicioRest<Empleado> {

    private static final String RUTA = "/api/empleados";

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;

    public EmpleadoService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, RUTA, Empleado.class, Empleado[].class);
        this.backend = backend;
        this.sesion = sesion;
    }

    /** Empleados que coinciden con el texto (nombre, apellido o DNI); sin texto, todos. */
    public List<Empleado> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listar();
        }
        String url = UriComponentsBuilder.fromUriString(backend.url(RUTA))
                .queryParam("q", texto.trim()).build().encode().toUriString();
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        Empleado[] empleados = restTemplate.exchange(url, HttpMethod.GET, entidad, Empleado[].class).getBody();
        return empleados == null ? List.of() : Arrays.asList(empleados);
    }
}
