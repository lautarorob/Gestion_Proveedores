package controladores;

import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import repositorios.RepoFacturaProducto; // Nombre de repositorio actualizado

@Named("controladorFacturaProducto")
@RequestScoped
public class ControladorFacturaProducto implements Serializable {

    @Inject
    private RepoFacturaProducto repoFP;

    private FacturaProducto facturaProducto;
    
    private Integer idFactura;
    private Integer idProducto;

    @Model
    @Produces
    public FacturaProducto facturaProducto() {
        if (idFactura != null && idProducto != null) {
            FacturaProductoPK pk = new FacturaProductoPK(idFactura, idProducto);
            repoFP.porId(pk).ifPresent(fp -> facturaProducto = fp);
        } else {
            facturaProducto = new FacturaProducto();
        }
        return facturaProducto;
    }

    public String guardar() {
        repoFP.guardar(facturaProducto);
        return "/facturas/detalle.xhtml?id=" + facturaProducto.getFacturaProductoPK().getIdFactura() + "&faces-redirect=true";
    }

    public String eliminar(Integer idFactura, Integer idProducto) {
        FacturaProductoPK pk = new FacturaProductoPK(idFactura, idProducto);
        repoFP.eliminar(pk);
        return "/facturas/detalle.xhtml?id=" + idFactura + "&faces-redirect=true";
    }

    // --- Getters y Setters ---

    public FacturaProducto getFacturaProducto() {
        if (facturaProducto == null) {
            facturaProducto();
        }
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
}