package com.example.servicio;

import com.example.modelo.Especie;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class EspecieService extends ServicioRest<Especie> {

    public EspecieService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/especies", Especie.class, Especie[].class);
    }
}
