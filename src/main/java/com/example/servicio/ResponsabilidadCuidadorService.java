package com.example.servicio;

import com.example.modelo.ResponsabilidadCuidador;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class ResponsabilidadCuidadorService extends ServicioRest<ResponsabilidadCuidador> {

    public ResponsabilidadCuidadorService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/responsabilidades-cuidador", ResponsabilidadCuidador.class, ResponsabilidadCuidador[].class);
    }
}
