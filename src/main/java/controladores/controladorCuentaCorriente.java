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
import java.util.stream.Collectors;
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
    private String estadoPago;

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

        // 2. Cargar ÓRDENES DE PAGO del proveedor (igual que antes)
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

        // --- 3. NUEVO: FILTRAR LA LISTA EN MEMORIA ---
        // Verificamos si el filtro 'estadoPago' tiene un valor
        if (estadoPago != null && !estadoPago.trim().isEmpty()) {

            final String filtroEstado = estadoPago; // Variable final para usar en lambda

            movimientos = movimientos.stream()
                    .filter(mov -> {
                        String estadoMovimiento = mov.getEstado(); // "Pendiente", "Pagado" o null

                        // Lógica del filtro:
                        if ("Impaga".equals(filtroEstado)) {
                            // Si el filtro es "Impago", solo mustra movimientos
                            // cuyo estado sea "Pendiente"
                            return "Pendiente".equals(estadoMovimiento);
                        }

                        if ("Pagada".equals(filtroEstado)) {
                            // Si el filtro es "Pagado", muestra facturas "Pagado"
                            // Y también las Órdenes de Pago 
                            return "Pagada".equals(estadoMovimiento) || estadoMovimiento == null;
                        }

                        return false; // Caso inesperado
                    })
                    .collect(Collectors.toList()); // Creamos una nueva lista filtrada
        }
        // Si 'estadoPago' es null, este bloque se salta y se usan todos los movimientos

        // 4. Ordenar cronológicamente
        Collections.sort(movimientos);

        calcularSaldosParciales();

        // 5. Calcular saldo acumulado 
        // NOTA: Este saldo es el saldo TOTAL del proveedor, no el saldo de la lista filtrada.
        saldoActual = (BigDecimal) repoFactura.getSaldoPendiente(idProveedorSeleccionado);

        System.out.println("=== MOVIMIENTOS CARGADOS ===");
        
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

    public repoFactura getRepoFactura() {
        return repoFactura;
    }

    public void setRepoFactura(repoFactura repoFactura) {
        this.repoFactura = repoFactura;
    }

    public repoOrdenPago getRepoOrdenPago() {
        return repoOrdenPago;
    }

    public void setRepoOrdenPago(repoOrdenPago repoOrdenPago) {
        this.repoOrdenPago = repoOrdenPago;
    }

    public repoProveedor getRepoProveedor() {
        return repoProveedor;
    }

    public void setRepoProveedor(repoProveedor repoProveedor) {
        this.repoProveedor = repoProveedor;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

}
