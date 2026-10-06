package com.example.servicio;

import com.example.modelo.Zona;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class ZonaService extends ServicioRest<Zona> {

    public ZonaService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/zonas", Zona.class, Zona[].class);
    }
}
