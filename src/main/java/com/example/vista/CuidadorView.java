package com.example.vista;

import com.example.modelo.Cuidador;
import com.example.seguridad.SesionUsuario;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.CuidadorService;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "cuidadores", layout = MainLayout.class)
@PageTitle("Cuidadores | EcoParque")
@SoloAdministrador
public class CuidadorView extends VistaPersonas<Cuidador> {

    public CuidadorView(CuidadorService servicio, SesionUsuario sesion) {
        super(Cuidador.class, servicio, sesion, Cuidador::new);
    }

    @Override
    protected String titulo() {
        return "Gestión de Cuidadores";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar cuidador";
    }

    @Override
    protected String textoModificar() {
        return "Modificar cuidador";
    }
}
