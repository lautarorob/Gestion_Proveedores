package controladores;

import entidades.OrdenPago;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.RepoOrdenPago; // Nombre de repositorio actualizado

@Named("controladorOrdenPago")
@RequestScoped
public class ControladorOrdenPago implements Serializable {

    @Inject
    private RepoOrdenPago repoOrdenPago;

    private OrdenPago ordenPago;
    private Integer id;

    @Model
    @Produces
    public OrdenPago ordenPago() {
        if (id != null && id > 0) {
            repoOrdenPago.porId(id).ifPresent(o -> ordenPago = o);
        } else {
            ordenPago = new OrdenPago();
        }
        return ordenPago;
    }

    public String guardar() {
        repoOrdenPago.guardar(ordenPago);
        return "/ordenes_pago/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoOrdenPago.eliminar(id);
        return "/ordenes_pago/index.xhtml?faces-redirect=true";
    }

    public List<OrdenPago> listarTodas() {
        return repoOrdenPago.listarTodas();
    }

    // --- Getters y Setters ---
    
    public OrdenPago getOrdenPago() {
        if (ordenPago == null) {
            ordenPago();
        }
        return ordenPago;
    }

    public void setOrdenPago(OrdenPago ordenPago) {
        this.ordenPago = ordenPago;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}