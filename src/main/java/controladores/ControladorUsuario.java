package controladores;

import entidades.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
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
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoUsuario.eliminar(id);
        return "/usuarios/index.xhtml?faces-redirect=true";
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
}