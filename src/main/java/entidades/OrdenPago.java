/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author roble
 */
@Entity
@Table(name = "ordenes_pago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OrdenesPago.findAll", query = "SELECT o FROM OrdenesPago o"),
    @NamedQuery(name = "OrdenesPago.findByIdOrdenPago", query = "SELECT o FROM OrdenesPago o WHERE o.idOrdenPago = :idOrdenPago"),
    @NamedQuery(name = "OrdenesPago.findByNroOrden", query = "SELECT o FROM OrdenesPago o WHERE o.nroOrden = :nroOrden"),
    @NamedQuery(name = "OrdenesPago.findByFechaPago", query = "SELECT o FROM OrdenesPago o WHERE o.fechaPago = :fechaPago"),
    @NamedQuery(name = "OrdenesPago.findByFormaPago", query = "SELECT o FROM OrdenesPago o WHERE o.formaPago = :formaPago"),
    @NamedQuery(name = "OrdenesPago.findByMontoTotal", query = "SELECT o FROM OrdenesPago o WHERE o.montoTotal = :montoTotal")})
public class OrdenPago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_orden_pago", nullable = false)
    private Integer idOrdenPago;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nro_orden", nullable = false, length = 50)
    private String nroOrden;
    @Column(name = "fecha_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;
    @Size(max = 50)
    @Column(name = "forma_pago", length = 50)
    private String formaPago;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto_total", precision = 12, scale = 2)
    private BigDecimal montoTotal;
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id_proveedor")
    @ManyToOne
    private Proveedor idProveedor;
    @OneToMany(mappedBy = "idOrdenPago")
    private List<Factura> facturaList;

    public OrdenPago() {
    }

    public OrdenPago(Integer idOrdenPago) {
        this.idOrdenPago = idOrdenPago;
    }

    public OrdenPago(Integer idOrdenPago, String nroOrden) {
        this.idOrdenPago = idOrdenPago;
        this.nroOrden = nroOrden;
    }

    public Integer getIdOrdenPago() {
        return idOrdenPago;
    }

    public void setIdOrdenPago(Integer idOrdenPago) {
        this.idOrdenPago = idOrdenPago;
    }

    public String getNroOrden() {
        return nroOrden;
    }

    public void setNroOrden(String nroOrden) {
        this.nroOrden = nroOrden;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Proveedor getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Proveedor idProveedor) {
        this.idProveedor = idProveedor;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOrdenPago != null ? idOrdenPago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdenPago)) {
            return false;
        }
        OrdenPago other = (OrdenPago) object;
        if ((this.idOrdenPago == null && other.idOrdenPago != null) || (this.idOrdenPago != null && !this.idOrdenPago.equals(other.idOrdenPago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.OrdenesPago[ idOrdenPago=" + idOrdenPago + " ]";
    }
    
}
