package controladores;

import entidades.Factura;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.RepoFactura; // Nombre de repositorio actualizado

@Named("controladorFactura")
@RequestScoped
public class ControladorFactura implements Serializable {

    @Inject
    private RepoFactura repoFactura;

    private Factura factura;
    private Integer id;

    @Model
    @Produces
    public Factura factura() {
        if (id != null && id > 0) {
            repoFactura.porId(id).ifPresent(f -> factura = f);
        } else {
            factura = new Factura();
        }
        return factura;
    }

    public String guardar() {
        repoFactura.guardar(factura);
        return "/facturas/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoFactura.eliminar(id);
        return "/facturas/index.xhtml?faces-redirect=true";
    }

    public List<Factura> listarTodas() {
        return repoFactura.listarTodas();
    }

    // --- Getters y Setters ---

    public Factura getFactura() {
        if (factura == null) {
            factura();
        }
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}