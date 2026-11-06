package com.example.vista;

import com.example.modelo.Usuario;
import com.example.seguridad.SesionUsuario;
import com.example.servicio.RolService;
import com.example.servicio.UsuarioService;
import com.example.modelo.Rol;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

/**
 * Gestion de usuarios del sistema. El rol se elige entre los que existen (los crea el administrador en Roles).
 * Al modificar, la contrasena es opcional: si se deja vacia, se conserva la anterior.
 */
@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Usuarios | EcoParque")
public class UsuarioView extends VistaCrud<Usuario> {

    private final RolService rolService;

    public UsuarioView(UsuarioService servicio, RolService rolService, SesionUsuario sesion) {
        super(Usuario.class, servicio, sesion);
        this.rolService = rolService;
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
        List<String> roles = rolService.listar().stream().map(Rol::getNombre).toList();
        return new FormularioUsuario(existente, roles);
    }

    private static class FormularioUsuario implements Formulario<Usuario> {

        private final boolean esNuevo;
        private final TextField nombreUsuario = new TextField("Usuario");
        private final PasswordField clave = new PasswordField("Contraseña");
        private final ComboBox<String> rol = new ComboBox<>("Rol");
        private final TextField nombrePersona = new TextField("Nombre de la persona");

        FormularioUsuario(Usuario existente, List<String> roles) {
            this.esNuevo = existente == null;
            rol.setItems(roles);
            nombrePersona.setHelperText("Administrador, guía o cuidador: nombre y apellido del empleado, como figura en Empleados. "
                    + "Invitado o un rol nuevo: el nombre que quieras mostrar (si es un empleado, su nombre y apellido).");

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
