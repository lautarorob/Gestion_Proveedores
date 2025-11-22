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

    public controladorUsuario() {
    }

    /**
     * Inicializa el usuario para editar/crear
     * Este método debe ser llamado cuando se carga la página de edición/nuevo
     */
    public Usuario getUsuario() {
        if (id != null && id > 0) {
            // Modo edición: cargar el usuario específico por ID
            if (usuario == null || !id.equals(usuario.getIdUsuario())) {
                usuario = repoUsuario.porId(id).orElse(new Usuario());
            }
        } else if (usuario == null) {
            // Modo nuevo: crear usuario vacío
            usuario = new Usuario();
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        String passwordValue = (String) passwordComponent.getValue();

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
                FacesContext.getCurrentInstance().addMessage("formulario:userName",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "El nombre de usuario (login) '" + usuario.getUsername() + "' ya está en uso.",
                                "Login duplicado")
                );
                return null;
            }
        }

        // --- 2: Validar NOMBRE + ROL ---
        Optional<Usuario> userByNombreRol = repoUsuario.findByNombreAndRol(
                usuario.getNombreCompleto(),
                usuario.getRol()
        );

        if (userByNombreRol.isPresent()) {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            boolean esOtroUsuario = !userByNombreRol.get().getIdUsuario().equals(usuario.getIdUsuario());

            if (esNuevo || esOtroUsuario) {
                FacesContext.getCurrentInstance().addMessage("formulario:nombreCompleto",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Ya existe un usuario con el nombre '" + usuario.getNombreCompleto() + "' y el rol '" + usuario.getRol() + "'.",
                                "Duplicado por Nombre y Rol")
                );
                return null;
            }
        }

        repoUsuario.Guardar(usuario);
        
        // Limpiar el usuario editado después de guardar
        usuario = null;
        id = null;
        
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoUsuario.Eliminar(id);
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario encontrado = repoUsuario.login(usuario.getUsername(), usuario.getPassword());

        if (encontrado != null) {
            this.usuarioLogueado = encontrado; // Guardar como usuario logueado
            this.usuario = null; // Limpiar el usuario de edición

            // REGISTRAR EL USUARIO ACTUAL EN MySQL PARA LOS TRIGGERS
            repoUsuario.setCurrentUserId(encontrado.getIdUsuario());

            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Bienvenido",
                    "Bienvenido " + encontrado.getUsername()
            ));

            return "/index.xhtml?faces-redirect=true";
        } else {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Usuario o contraseña incorrectos",
                    "Error"
            ));
            return null;
        }
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

    public UIInput getPasswordComponent() {
        return passwordComponent;
    }

    public void setPasswordComponent(UIInput passwordComponent) {
        this.passwordComponent = passwordComponent;
    }

    public repoUsuario getRepoUsuario() {
        return repoUsuario;
    }

    public void setRepoUsuario(repoUsuario repoUsuario) {
        this.repoUsuario = repoUsuario;
    }
}