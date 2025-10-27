/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author roble
 */
@Entity
@Table(name = "factura_producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FacturaProducto.findAll", query = "SELECT f FROM FacturaProducto f"),
    @NamedQuery(name = "FacturaProducto.findByIdFactura", query = "SELECT f FROM FacturaProducto f WHERE f.facturaProductoPK.idFactura = :idFactura"),
    @NamedQuery(name = "FacturaProducto.findByIdProducto", query = "SELECT f FROM FacturaProducto f WHERE f.facturaProductoPK.idProducto = :idProducto"),
    @NamedQuery(name = "FacturaProducto.findByCantidad", query = "SELECT f FROM FacturaProducto f WHERE f.cantidad = :cantidad"),
    @NamedQuery(name = "FacturaProducto.findByPrecioUnitario", query = "SELECT f FROM FacturaProducto f WHERE f.precioUnitario = :precioUnitario"),
    @NamedQuery(name = "FacturaProducto.findBySubtotal", query = "SELECT f FROM FacturaProducto f WHERE f.subtotal = :subtotal")})
public class FacturaProducto implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected FacturaProductoPK facturaProductoPK;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    @Column(precision = 12, scale = 2)
    private BigDecimal subtotal;
    @JoinColumn(name = "id_factura", referencedColumnName = "id_factura", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Factura factura;
    @JoinColumn(name = "id_producto", referencedColumnName = "id_producto", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Producto producto;

    public FacturaProducto() {
    }

    public FacturaProducto(FacturaProductoPK facturaProductoPK) {
        this.facturaProductoPK = facturaProductoPK;
    }

    public FacturaProducto(FacturaProductoPK facturaProductoPK, int cantidad) {
        this.facturaProductoPK = facturaProductoPK;
        this.cantidad = cantidad;
    }

    public FacturaProducto(int idFactura, int idProducto) {
        this.facturaProductoPK = new FacturaProductoPK(idFactura, idProducto);
    }

    public FacturaProductoPK getFacturaProductoPK() {
        return facturaProductoPK;
    }

    public void setFacturaProductoPK(FacturaProductoPK facturaProductoPK) {
        this.facturaProductoPK = facturaProductoPK;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (facturaProductoPK != null ? facturaProductoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FacturaProducto)) {
            return false;
        }
        FacturaProducto other = (FacturaProducto) object;
        if ((this.facturaProductoPK == null && other.facturaProductoPK != null) || (this.facturaProductoPK != null && !this.facturaProductoPK.equals(other.facturaProductoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.FacturaProducto[ facturaProductoPK=" + facturaProductoPK + " ]";
    }
    
}
