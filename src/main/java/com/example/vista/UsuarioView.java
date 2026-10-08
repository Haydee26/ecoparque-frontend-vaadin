package com.example.vista;

import com.example.modelo.Usuario;
import com.example.seguridad.SesionUsuario;
import com.example.seguridad.SoloAdministrador;
import com.example.servicio.UsuarioService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

/**
 * Gestion de usuarios del sistema. Solo para administradores.
 * Al modificar, la contrasena es opcional: si se deja vacia, se conserva la anterior.
 */
@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Usuarios | EcoParque")
@SoloAdministrador
public class UsuarioView extends VistaCrud<Usuario> {

    public UsuarioView(UsuarioService servicio, SesionUsuario sesion) {
        super(Usuario.class, servicio, sesion);
    }

    @Override
    protected String titulo() {
        return "Gestión de Usuarios";
    }

    @Override
    protected String textoBotonNuevo() {
        return "Registrar nuevo usuario";
    }

    @Override
    protected String textoModificar() {
        return "Modificar usuario";
    }

    @Override
    protected String describir(Usuario usuario) {
        return usuario.getNombreUsuario();
    }

    @Override
    protected void configurarColumnas(Grid<Usuario> tabla) {
        tabla.addColumn(Usuario::getNombreUsuario).setHeader("Usuario").setSortable(true);
        tabla.addColumn(Usuario::getRol).setHeader("Rol");
        tabla.addColumn(Usuario::getNombrePersona).setHeader("Nombre de la persona");
    }

    @Override
    protected Formulario<Usuario> crearFormulario(Usuario existente) {
        return new FormularioUsuario(existente);
    }

    private static class FormularioUsuario implements Formulario<Usuario> {

        private final boolean esNuevo;
        private final TextField nombreUsuario = new TextField("Usuario");
        private final PasswordField clave = new PasswordField("Contraseña");
        private final ComboBox<String> rol = new ComboBox<>("Rol");
        private final TextField nombrePersona = new TextField("Nombre de la persona");

        FormularioUsuario(Usuario existente) {
            this.esNuevo = existente == null;
            rol.setItems(List.of("ADMINISTRADOR", "GUIA", "CUIDADOR"));

            if (!esNuevo) {
                nombreUsuario.setValue(valor(existente.getNombreUsuario()));
                rol.setValue(existente.getRol());
                nombrePersona.setValue(valor(existente.getNombrePersona()));
                clave.setHelperText("Dejala vacía para conservar la actual.");
            }
        }

        private static String valor(String texto) {
            return texto == null ? "" : texto;
        }

        @Override
        public Component[] campos() {
            return new Component[]{nombreUsuario, clave, rol, nombrePersona};
        }

        @Override
        public String validar() {
            if (nombreUsuario.isEmpty() || rol.isEmpty()) {
                return "Completá el usuario y el rol.";
            }
            if (esNuevo && clave.isEmpty()) {
                return "Completá la contraseña.";
            }
            return null;
        }

        @Override
        public Usuario construir() {
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(nombreUsuario.getValue().trim());
            usuario.setClave(clave.getValue());
            usuario.setRol(rol.getValue());
            usuario.setNombrePersona(nombrePersona.getValue().trim());
            return usuario;
        }
    }
}
