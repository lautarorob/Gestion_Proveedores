package DTO;

import java.math.BigDecimal;
import java.util.Date;

public class MovimientosDTO implements Comparable<MovimientosDTO> {
    
    private Date fecha;
    private String tipo; // "Factura" u "Orden de Pago"
    private String comprobante; // nro_comprobante
    private String descripcion;
    private BigDecimal debe;
    private BigDecimal haber;
    private BigDecimal saldoParcial;
    private String estado;
    private Integer idProveedor; // Para filtrar por proveedor
    
    // Constructor vacío
    public MovimientosDTO() {
        this.debe = BigDecimal.ZERO;
        this.haber = BigDecimal.ZERO;
        this.saldoParcial = BigDecimal.ZERO;
    }
    
    // Constructor para Facturas
    public static MovimientosDTO fromFactura(
            Date fecha, 
            String nroComprobante, 
            String descripcion,
            BigDecimal total, 
            String estado,
            Integer idProveedor) {
        
        MovimientosDTO dto = new MovimientosDTO();
        dto.setFecha(fecha);
        dto.setTipo("Factura");
        dto.setComprobante(nroComprobante);
        dto.setDescripcion(descripcion);
        dto.setDebe(total); // Las facturas van al DEBE
        dto.setHaber(BigDecimal.ZERO);
        dto.setEstado(estado);
        dto.setIdProveedor(idProveedor);
        return dto;
    }
    
    // Constructor para Órdenes de Pago
    public static MovimientosDTO fromOrdenPago(
            Date fechaPago, 
            String nroOrden, 
            String formaPago,
            BigDecimal montoTotal, 
            Integer idProveedor) {
        
        MovimientosDTO dto = new MovimientosDTO();
        dto.setFecha(fechaPago);
        dto.setTipo("Orden de Pago");
        dto.setComprobante(nroOrden);
        dto.setDescripcion("Pago " + formaPago);
        dto.setDebe(BigDecimal.ZERO);
        dto.setHaber(montoTotal); // Los pagos van al HABER
        dto.setEstado("Pagada");
        dto.setIdProveedor(idProveedor);
        return dto;
    }
    
    // Para ordenar cronológicamente
    @Override
    public int compareTo(MovimientosDTO otro) {
        return this.fecha.compareTo(otro.fecha);
    }
    
    // Getters y Setters
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public BigDecimal getSaldoParcial() {
        return saldoParcial;
    }

    public void setSaldoParcial(BigDecimal saldoParcial) {
        this.saldoParcial = saldoParcial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}