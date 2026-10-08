package com.example.servicio;

import com.example.modelo.AsignacionGuia;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class AsignacionGuiaService extends ServicioRest<AsignacionGuia> {

    public AsignacionGuiaService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/asignaciones-guia", AsignacionGuia.class, AsignacionGuia[].class);
    }
}
