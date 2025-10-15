package controladores;

import entidades.Proveedor;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.RepoProveedores;

@Named("controladorProveedor")
@RequestScoped
public class ControladorProveedor implements Serializable {

    @Inject
    private RepoProveedores repoProveedor;

    private Proveedor proveedor;
    private Integer id;

    @Model
    @Produces
    public Proveedor proveedor() {
        if (id != null && id > 0) {
            repoProveedor.porId(id).ifPresent(p -> proveedor = p);
        } else {
            proveedor = new Proveedor();
        }
        return proveedor;
    }

    public String guardar() {
        repoProveedor.guardar(proveedor);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoProveedor.eliminar(id);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public List<Proveedor> listarTodos() {
        return repoProveedor.listarTodos();
    }

    // --- Getters y Setters ---
    public Proveedor getProveedor() {
        if (proveedor == null) {
            proveedor();
        }
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
