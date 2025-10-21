package controladores;

import entidades.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.RepoUsuario;

@Named("controladorUsuario")
@RequestScoped
public class ControladorUsuario implements Serializable {

    @Inject
    private RepoUsuario repoUsuario;

    private Usuario usuario;
    private Integer id;

    @Model
    @Produces
    public Usuario usuario() {
        if (id != null && id > 0) {
            repoUsuario.porId(id).ifPresent(u -> usuario = u);
        } else {
            usuario = new Usuario();
        }
        return usuario;
    }

    public String guardar() {
        repoUsuario.guardar(usuario);
        return "/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoUsuario.eliminar(id);
        return "/index.xhtml?faces-redirect=true";
    }

    public List<Usuario> listarTodos() {
        return repoUsuario.listarTodos();
    }

    // --- Getters y Setters ---
    public Usuario getUsuario() {
        if (usuario == null) {
            usuario();
        }
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

    public String loginVerificacion() {
        String email = this.usuario.getEmail();
        String password = this.usuario.getPassword();

        Usuario usuarioEncontrado = repoUsuario.loginVerificacion(email, password);

        if (usuarioEncontrado == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email o contraseña incorrectos", "Error"));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            return "/login.xhtml?faces-redirect=true";
        }

        FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put("usuarioLogueado", usuarioEncontrado);

        return "/index.xhtml?faces-redirect=true";
    }

}
