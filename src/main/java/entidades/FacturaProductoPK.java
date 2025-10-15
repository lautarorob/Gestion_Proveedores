/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author roble
 */
@Embeddable
public class FacturaProductoPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_factura", nullable = false)
    private int idFactura;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_producto", nullable = false)
    private int idProducto;

    public FacturaProductoPK() {
    }

    public FacturaProductoPK(int idFactura, int idProducto) {
        this.idFactura = idFactura;
        this.idProducto = idProducto;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idFactura;
        hash += (int) idProducto;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FacturaProductoPK)) {
            return false;
        }
        FacturaProductoPK other = (FacturaProductoPK) object;
        if (this.idFactura != other.idFactura) {
            return false;
        }
        if (this.idProducto != other.idProducto) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.FacturaProductoPK[ idFactura=" + idFactura + ", idProducto=" + idProducto + " ]";
    }
    
}
