package controladores;

import entidades.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import repositorios.repoUsuario;

@Named(value = "controladorSesion")
@SessionScoped
public class controladorSesion implements Serializable {

    @Inject
    private repoUsuario repoUsuario;

    private Usuario usuarioLogueado;

    // Variables temporales solo para el formulario de login
    private String usernameInput;
    private String passwordInput;

    public controladorSesion() {
    }

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        Usuario encontrado = repoUsuario.login(this.usernameInput, this.passwordInput);

        if (encontrado != null) {
            this.usuarioLogueado = encontrado;

            // REGISTRAR EL ID EN LA BD PARA TUS TRIGGERS DE AUDITORÍA
            repoUsuario.setCurrentUserId(encontrado.getIdUsuario());

            // Mensaje de bienvenida (opcional, a veces se pierde al redireccionar)
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Bienvenido", "Hola " + encontrado.getNombreCompleto()));

            return "/index.xhtml?faces-redirect=true";
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error de acceso", "Usuario o contraseña incorrectos"));
            return null;
        }
    }

    public String logout() {
        // Invalidar la sesión completa
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    // Método auxiliar para verificar en las vistas si alguien está logueado
    public boolean isLogueado() {
        return usuarioLogueado != null;
    }

    // --- Getters y Setters ---
    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public String getUsernameInput() {
        return usernameInput;
    }

    public void setUsernameInput(String usernameInput) {
        this.usernameInput = usernameInput;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }
}
