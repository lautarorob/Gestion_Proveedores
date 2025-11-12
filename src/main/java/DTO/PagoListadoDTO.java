/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author bgsof
 */
public class PagoListadoDTO {
    private String proveedor;
    private String formaPago;
    private Date fechaPago;
    private BigDecimal totalPagado;

    // Constructor
    public PagoListadoDTO(String proveedor, String formaPago, Date fechaPago, BigDecimal totalPagado) {
        this.proveedor = proveedor;
        this.formaPago = formaPago;
        this.fechaPago = fechaPago;
        this.totalPagado = totalPagado;
    }

    // Getters y setters
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public BigDecimal getTotalPagado() { return totalPagado; }
    public void setTotalPagado(BigDecimal totalPagado) { this.totalPagado = totalPagado; }
}