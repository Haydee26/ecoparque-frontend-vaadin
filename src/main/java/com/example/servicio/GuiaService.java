package com.example.servicio;

import com.example.modelo.Guia;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class GuiaService extends ServicioRest<Guia> {

    public GuiaService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/guias", Guia.class, Guia[].class);
    }
}
