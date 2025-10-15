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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author roble
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Auditoria.findAll", query = "SELECT a FROM Auditoria a"),
    @NamedQuery(name = "Auditoria.findByIdAuditoria", query = "SELECT a FROM Auditoria a WHERE a.idAuditoria = :idAuditoria"),
    @NamedQuery(name = "Auditoria.findByFechaMovimiento", query = "SELECT a FROM Auditoria a WHERE a.fechaMovimiento = :fechaMovimiento"),
    @NamedQuery(name = "Auditoria.findByDescripcion", query = "SELECT a FROM Auditoria a WHERE a.descripcion = :descripcion")})
public class Auditoria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_auditoria", nullable = false)
    private Integer idAuditoria;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_movimiento", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaMovimiento;
    @Size(max = 255)
    @Column(length = 255)
    private String descripcion;
    @JoinColumn(name = "id_usuarioUltimo", referencedColumnName = "id_usuario")
    @ManyToOne
    private Usuario idusuarioUltimo;

    public Auditoria() {
    }

    public Auditoria(Integer idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Auditoria(Integer idAuditoria, Date fechaMovimiento) {
        this.idAuditoria = idAuditoria;
        this.fechaMovimiento = fechaMovimiento;
    }

    public Integer getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Integer idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario getIdusuarioUltimo() {
        return idusuarioUltimo;
    }

    public void setIdusuarioUltimo(Usuario idusuarioUltimo) {
        this.idusuarioUltimo = idusuarioUltimo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAuditoria != null ? idAuditoria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Auditoria)) {
            return false;
        }
        Auditoria other = (Auditoria) object;
        if ((this.idAuditoria == null && other.idAuditoria != null) || (this.idAuditoria != null && !this.idAuditoria.equals(other.idAuditoria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Auditoria[ idAuditoria=" + idAuditoria + " ]";
    }
    
}
