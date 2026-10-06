package com.example.servicio;

import com.example.modelo.Habitat;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class HabitatService extends ServicioRest<Habitat> {

    public HabitatService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/habitats", Habitat.class, Habitat[].class);
    }
}
