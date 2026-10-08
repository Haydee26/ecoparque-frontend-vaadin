package com.example.servicio;

import com.example.modelo.Usuario;
import com.example.seguridad.SesionUsuario;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends ServicioRest<Usuario> {

    public UsuarioService(BackendConfig backend, SesionUsuario sesion) {
        super(backend, sesion, "/api/usuarios", Usuario.class, Usuario[].class);
    }
}
