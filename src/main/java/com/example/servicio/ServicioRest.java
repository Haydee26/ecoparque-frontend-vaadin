package com.example.servicio;

import com.example.seguridad.SesionUsuario;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * Operaciones comunes (listar, crear, actualizar, eliminar) contra una API REST del backend.
 * Cada servicio concreto solo indica su ruta y su tipo.
 */
public abstract class ServicioRest<T> {

    private final RestTemplate restTemplate = new RestTemplate();
    private final BackendConfig backend;
    private final SesionUsuario sesion;
    private final String ruta;
    private final Class<T> tipo;
    private final Class<T[]> tipoLista;

    protected ServicioRest(BackendConfig backend, SesionUsuario sesion,
            String ruta, Class<T> tipo, Class<T[]> tipoLista) {
        this.backend = backend;
        this.sesion = sesion;
        this.ruta = ruta;
        this.tipo = tipo;
        this.tipoLista = tipoLista;
    }

    public List<T> listar() {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        T[] elementos = restTemplate.exchange(backend.url(ruta), HttpMethod.GET, entidad, tipoLista).getBody();
        return elementos == null ? List.of() : Arrays.asList(elementos);
    }

    public T crear(T elemento) {
        HttpEntity<T> entidad = new HttpEntity<>(elemento, sesion.encabezados());
        return restTemplate.exchange(backend.url(ruta), HttpMethod.POST, entidad, tipo).getBody();
    }

    public T actualizar(Long id, T elemento) {
        HttpEntity<T> entidad = new HttpEntity<>(elemento, sesion.encabezados());
        return restTemplate.exchange(backend.url(ruta + "/" + id), HttpMethod.PUT, entidad, tipo).getBody();
    }

    public void eliminar(Long id) {
        HttpEntity<Void> entidad = new HttpEntity<>(sesion.encabezados());
        restTemplate.exchange(backend.url(ruta + "/" + id), HttpMethod.DELETE, entidad, Void.class);
    }
}
