/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import DTO.MovimientosDTO;
import entidades.Factura;
import entidades.OrdenPago;
import entidades.Proveedor;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import repositorios.repoFactura;
import repositorios.repoOrdenPago;
import repositorios.repoProveedor;

/**
 *
 * @author roble
 */
@Named(value = "controladorCuentaCorriente")
@ViewScoped
public class controladorCuentaCorriente implements Serializable {

    /**
     * Creates a new instance of controladorCuentaCorriente
     */
    public controladorCuentaCorriente() {
    }

    private static final long serialVersionUID = 1L;

    @Inject
    private transient repoFactura repoFactura;

    @Inject
    private transient repoOrdenPago repoOrdenPago;

    @Inject
    private transient repoProveedor repoProveedor;

    private Integer idProveedorSeleccionado;
    private Proveedor proveedorSeleccionado;
    private List<Proveedor> listaProveedores;
    private List<MovimientosDTO> movimientos;
    private BigDecimal saldoActual;

    @PostConstruct
    public void init() {
        listaProveedores = repoProveedor.Listar();
        movimientos = new ArrayList<>();
        saldoActual = BigDecimal.ZERO;
    }

    /**
     * Carga todos los movimientos (facturas + pagos) de un proveedor
     */
    public void cargarMovimientos() {
        if (idProveedorSeleccionado == null || idProveedorSeleccionado <= 0) {
            System.out.println("ERROR: Debe seleccionar un proveedor");
            movimientos = new ArrayList<>();
            saldoActual = BigDecimal.ZERO;
            return;
        }

        // Cargar proveedor seleccionado
        proveedorSeleccionado = repoProveedor.porId(idProveedorSeleccionado)
                .orElse(null);

        if (proveedorSeleccionado == null) {
            System.out.println("ERROR: Proveedor no encontrado");
            return;
        }

        movimientos = new ArrayList<>();

        // 1. Cargar FACTURAS del proveedor
        List<Factura> facturas = repoFactura.listarPorProveedor(idProveedorSeleccionado);
        for (Factura f : facturas) {
            MovimientosDTO dto = MovimientosDTO.fromFactura(
                    f.getFechaComprobante(),
                    f.getNroComprobante(),
                    "Compra " + f.getTipo() + " " + f.getNroComprobante(),
                    f.getTotal(),
                    f.getEstado(),
                    f.getIdProveedor().getIdProveedor()
            );
            movimientos.add(dto);
        }

        // 2. Cargar ÓRDENES DE PAGO del proveedor
        List<OrdenPago> pagos = repoOrdenPago.listarPorProveedor(idProveedorSeleccionado);
        for (OrdenPago op : pagos) {
            MovimientosDTO dto = MovimientosDTO.fromOrdenPago(
                    op.getFechaPago(),
                    op.getNroOrden(),
                    op.getFormaPago(),
                    op.getMontoTotal(),
                    op.getIdProveedor().getIdProveedor()
            );
            movimientos.add(dto);
        }

        // 3. Ordenar cronológicamente
        Collections.sort(movimientos);

        calcularSaldosParciales(); // Renombramos el método

        // 4. Calcular saldo acumulado
        saldoActual = (BigDecimal) repoFactura.getSaldoPendiente(idProveedorSeleccionado);

        System.out.println("=== MOVIMIENTOS CARGADOS ===");
        System.out.println("Proveedor: " + proveedorSeleccionado.getNombreComercial());
        System.out.println("Total movimientos: " + movimientos.size());
        System.out.println("Saldo actual: $" + saldoActual);
    }

    /**
     * Calcula el saldo parcial de cada movimiento y el saldo final
     */
    private void calcularSaldosParciales() { // Nombre cambiado
        BigDecimal saldo = BigDecimal.ZERO;

        for (MovimientosDTO mov : movimientos) {
            // Saldo = Saldo anterior + Debe - Haber
            saldo = saldo.add(mov.getDebe()).subtract(mov.getHaber());
            mov.setSaldoParcial(saldo);
        }
        
        // ¡Ya NO asignamos saldoActual = saldo; aquí!
    }

    // ===== GETTERS Y SETTERS =====
    public Integer getIdProveedorSeleccionado() {
        return idProveedorSeleccionado;
    }

    public void setIdProveedorSeleccionado(Integer idProveedorSeleccionado) {
        this.idProveedorSeleccionado = idProveedorSeleccionado;
    }

    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public List<MovimientosDTO> getMovimientos() {
        return movimientos;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

}
