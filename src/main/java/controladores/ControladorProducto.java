package controladores;

import entidades.Producto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.RepoProducto; // Nombre de repositorio actualizado

@Named("controladorProducto")
@RequestScoped
public class ControladorProducto implements Serializable {

    @Inject
    private RepoProducto repoProducto;

    private Producto producto;
    private Integer id;

    @Model
    @Produces
    public Producto producto() {
        if (id != null && id > 0) {
            repoProducto.porId(id).ifPresent(p -> producto = p);
        } else {
            producto = new Producto();
        }
        return producto;
    }

    public String guardar() {
        repoProducto.guardar(producto);
        return "/productos/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoProducto.eliminar(id);
        return "/productos/index.xhtml?faces-redirect=true";
    }

    public List<Producto> listarTodos() {
        return repoProducto.listarTodos();
    }

    // --- Getters y Setters ---
    
    public Producto getProducto() {
        if (producto == null) {
            producto();
        }
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}