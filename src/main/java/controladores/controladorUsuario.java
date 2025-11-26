package controladores;

import entidades.Usuario;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import repositorios.repoUsuario;

@Named(value = "controladorUsuario")
@SessionScoped
public class controladorUsuario implements Serializable {

    @Inject
    private repoUsuario repoUsuario;

    private Usuario usuarioLogueado; // Usuario de la sesión (quien está logueado)
    private Usuario usuario; // Usuario para editar/crear
    private Integer id;
    private String confirmPassword;
    private UIInput passwordComponent;
    private Boolean estado = true;

    public controladorUsuario() {
    }

    /**
     * Inicializa el usuario para editar/crear
     */
    public Usuario getUsuario() {
        if (id != null && id > 0) {
            // Modo edición: cargar el usuario específico por ID
            if (usuario == null || !id.equals(usuario.getIdUsuario())) {
                usuario = repoUsuario.porId(id).orElse(new Usuario());
            }
        } else {
            // Modo nuevo: siempre crear usuario vacío
            if (usuario == null || usuario.getIdUsuario() != null) {
                usuario = new Usuario();
                confirmPassword = null; // Limpiar confirmación de contraseña
            }
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Limpia el usuario de edición/creación Llamar este método al entrar a la
     * página de nuevo usuario
     */
    public void prepararNuevoUsuario() {
        this.usuario = new Usuario();
        this.id = null;
        this.confirmPassword = null;
    }

    /**
     * Obtiene el usuario actualmente logueado
     */
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public void validatePasswordConfirm(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return;
        }

        String confirmPasswordValue = (String) value;

        // Buscar el componente "password" en el formulario
        // Asumimos que el ID del formulario es "formulario" y el del input es
        // "password"
        UIInput passwordInput = (UIInput) component.findComponent("password");

        if (passwordInput == null) {
            // Si no lo encuentra relativo, intentar búsqueda absoluta o por ID directo si
            // están en el mismo naming container
            // En este caso, como están en el mismo form, findComponent("password") debería
            // bastar si 'component' es hermano o está cerca.
            // Pero 'component' es el campo confirmPassword.
            // Una forma segura es buscar desde la raíz o usar binding temporal (pero
            // queremos evitar binding).
            // O simplemente buscar por ID relativo al NamingContainer padre.
            passwordInput = (UIInput) context.getViewRoot().findComponent("formulario:password");
        }

        if (passwordInput == null) {
            return; // No se pudo validar
        }

        String passwordValue = (String) passwordInput.getValue();

        if (passwordValue == null) {
            passwordValue = "";
        }

        if (!passwordValue.equals(confirmPasswordValue)) {
            throw new ValidatorException(new FacesMessage("Las contraseñas no coinciden."));
        }
    }

    public List<Usuario> listar() {
        return repoUsuario.Listar();
    }

    public String guardar() {
        // --- 1: Validar USERNAME (Login único) ---
        Optional<Usuario> userByUsername = repoUsuario.findByUsername(usuario.getUsername());
        if (userByUsername.isPresent()) {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            boolean esOtroUsuario = !userByUsername.get().getIdUsuario().equals(usuario.getIdUsuario());

            if (esNuevo || esOtroUsuario) {
                FacesContext.getCurrentInstance().addMessage("formulario:username",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "El nombre de usuario (login) '" + usuario.getUsername() + "' ya está en uso.",
                                "Login duplicado"));
                return null;
            }
        }

        // --- 2: Validar NOMBRE + ROL ---
        Optional<Usuario> userByNombreRol = repoUsuario.findByNombreAndRol(
                usuario.getNombreCompleto(),
                usuario.getRol());

        if (userByNombreRol.isPresent()) {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            boolean esOtroUsuario = !userByNombreRol.get().getIdUsuario().equals(usuario.getIdUsuario());

            if (esNuevo || esOtroUsuario) {
                FacesContext.getCurrentInstance().addMessage("formulario:nombreCompleto",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Ya existe un usuario con el nombre '" + usuario.getNombreCompleto() + "' y el rol '"
                                        + usuario.getRol() + "'.",
                                "Duplicado por Nombre y Rol"));
                return null;
            }
        }

        repoUsuario.Guardar(usuario);

        // Limpiar el usuario editado después de guardar
        usuario = null;
        id = null;
        confirmPassword = null;

        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoUsuario.Eliminar(id);
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Primero verificar si el usuario existe
        Optional<Usuario> usuarioOpt = repoUsuario.findByUsername(usuario.getUsername());

        if (usuarioOpt.isPresent()) {
            Usuario encontrado = usuarioOpt.get();

            // Verificar si está inactivo
            if (!encontrado.isEstado()) {
                context.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Usuario inactivo",
                        "Tu cuenta ha sido desactivada. Contacta al administrador."));
                return null;
            }

            // Verificar contraseña
            if (!encontrado.getPassword().equals(usuario.getPassword())) {
                context.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Contraseña incorrecta",
                        "La contraseña ingresada no es correcta."));
                return null;
            }

            // Login exitoso
            this.usuarioLogueado = encontrado;
            this.usuario = null;

            // REGISTRAR EL USUARIO ACTUAL EN MySQL PARA LOS TRIGGERS
            repoUsuario.setCurrentUserId(encontrado.getIdUsuario());

            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Bienvenido",
                    "Bienvenido " + encontrado.getUsername()));

            return "/index.xhtml?faces-redirect=true";

        } else {
            // Usuario no existe
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Usuario no encontrado",
                    "El usuario ingresado no existe."));
            return null;
        }
    }

    /**
     * Cierra la sesión del usuario actual Limpia todos los datos de la sesión y
     * redirige al login
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Limpiar el ID del usuario en MySQL (para los triggers)
        try {
            repoUsuario.setCurrentUserId(null);
        } catch (Exception e) {
            System.out.println("Error al limpiar usuario en MySQL: " + e.getMessage());
        }

        // Limpiar todos los datos del controlador
        this.usuarioLogueado = null;
        this.usuario = null;
        this.id = null;
        this.confirmPassword = null;

        // Invalidar la sesión completa
        context.getExternalContext().invalidateSession();

        // Mensaje de despedida (opcional)
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Sesión cerrada",
                "Has cerrado sesión exitosamente"));

        // Redirigir al login
        return "/login.xhtml?faces-redirect=true";
    }

    public String bajaLogica(Integer id) {
        repoUsuario.BajaLogica(id);
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String activarLogica(Integer id) {
        repoUsuario.ActivarLogica(id); // método que pone estado = true
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    /**
     * Verifica si hay un usuario logueado Útil para mostrar/ocultar elementos
     * en la vista
     */
    public boolean isLogueado() {
        return usuarioLogueado != null;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public repoUsuario getRepoUsuario() {
        return repoUsuario;
    }

    public void setRepoUsuario(repoUsuario repoUsuario) {
        this.repoUsuario = repoUsuario;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

}
