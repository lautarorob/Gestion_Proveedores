package controladores;

import entidades.Auditoria;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.RepoAuditoria; // Nombre de repositorio actualizado

@Named("controladorAuditoria")
@RequestScoped
public class ControladorAuditoria implements Serializable {

    @Inject
    private RepoAuditoria repoAuditoria;

    private Auditoria auditoria;
    private Integer id;

    @Model
    @Produces
    public Auditoria auditoria() {
        if (id != null && id > 0) {
            repoAuditoria.porId(id).ifPresent(u -> auditoria = u);
        } else {
            auditoria = new Auditoria();
        }
        return auditoria;
    }

    public String guardar() {
        repoAuditoria.guardar(auditoria);
        return "/auditoria/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoAuditoria.eliminar(id);
        return "/auditoria/index.xhtml?faces-redirect=true";
    }

    public List<Auditoria> listarTodos() {
        return repoAuditoria.listarTodos();
    }

    // --- Getters y Setters ---
    public Auditoria getAuditoria() {
        if (auditoria == null) {
            auditoria();
        }
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
