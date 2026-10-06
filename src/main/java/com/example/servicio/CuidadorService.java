package com.example.servicio;

import com.example.modelo.Cuidador;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class CuidadorService extends ServicioRest<Cuidador> {

    public CuidadorService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/cuidadores", Cuidador.class, Cuidador[].class);
    }
}
