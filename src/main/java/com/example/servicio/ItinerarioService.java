package com.example.servicio;

import com.example.modelo.Itinerario;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class ItinerarioService extends ServicioRest<Itinerario> {

    public ItinerarioService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/itinerarios", Itinerario.class, Itinerario[].class);
    }
}
