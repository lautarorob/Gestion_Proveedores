package controladores;

import entidades.Usuario;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.view.ViewScoped; // <--- OJO: CAMBIO DE IMPORT A VIEW SCOPED
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import repositorios.repoUsuario;

@Named(value = "controladorUsuario")
@ViewScoped // <--- CLAVE: Los datos viven solo mientras estás en el formulario
public class controladorUsuario implements Serializable {

    @Inject
    private repoUsuario repoUsuario;

    @Inject
    private controladorSesion controladorSesion; // Para acceder al usuario logueado

    private Usuario usuario;
    private Integer id; // Captura ?id=X de la URL
    private String confirmPassword;
    private UIInput passwordComponent;

    // CONSTRUCTOR: Inicializa un usuario limpio automáticamente
    public controladorUsuario() {
        this.usuario = new Usuario();
    }

    // --- LÓGICA DE CARGA ---
    // Este método se llama desde <f:viewAction> en editar.xhtml
    public void prepararEditar() {
        if (this.id != null && this.id != 0) {
            Optional<Usuario> opUsuario = repoUsuario.buscarPorId(this.id);
            if (opUsuario.isPresent()) {
                this.usuario = opUsuario.get();
                // Rellenamos el confirmPassword para que coincida y no de error al guardar
                this.confirmPassword = this.usuario.getPassword();
            } else {
                System.out.println("No se encontró usuario con ID: " + this.id);
            }
        }
    }

    // YA NO NECESITAS 'prepararNuevo'. Al ser ViewScoped, si entras de nuevo
    // se crea una instancia nueva y limpia sola.
    public List<Usuario> listar() {
        return repoUsuario.Listar();
    }

    public String guardar() {

        // --- 1: Validar USERNAME (Login único) ---
        Optional<Usuario> userByUsername = repoUsuario.findByUsername(usuario.getUsername());
        if (userByUsername.isPresent()) {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            // Si el ID es diferente, es otro usuario robando el username
            boolean esOtroUsuario = !userByUsername.get().getIdUsuario().equals(usuario.getIdUsuario());

            if (esNuevo || esOtroUsuario) {
                FacesContext.getCurrentInstance().addMessage("formulario:username",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "El nombre de usuario (login) '" + usuario.getUsername() + "' ya está en uso.",
                                "Login duplicado")
                );
                return null; // Nos quedamos en la página
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

        // --- 3: PREPARAR AUDITORÍA (FIX PARA TRIGGERS) ---
        // Antes de guardar, le decimos a la BD quién está haciendo esto
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }

        // --- 4: GUARDAR ---
        repoUsuario.Guardar(usuario);

        // Redirigimos para limpiar el formulario y volver a la lista
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        // --- 1: PREPARAR AUDITORÍA (FIX PARA TRIGGERS) ---
        // Antes de eliminar, le decimos a la BD quién dio la orden
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }

        // --- 2: ELIMINAR ---
        repoUsuario.Eliminar(id);

        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    // --- VALIDACIONES ---
    public void validatePasswordConfirm(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return;
        }

        String confirmVal = (String) value;
        String originalVal = (String) passwordComponent.getValue();

        if (originalVal == null) {
            originalVal = "";
        }

        if (!originalVal.equals(confirmVal)) {
            throw new ValidatorException(new FacesMessage("Las contraseñas no coinciden."));
        }
    }

    // --- GETTERS Y SETTERS ---
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

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
}
