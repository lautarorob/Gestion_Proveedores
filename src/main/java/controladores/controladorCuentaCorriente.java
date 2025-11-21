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
    private List<MovimientosDTO> movimientos; // Esta es la lista que ve la vista (potencialmente filtrada)
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

        // --- CAMBIO: Usamos una lista temporal para TODOS los movimientos ---
        List<MovimientosDTO> allMovimientos = new ArrayList<>();

        
        // --- INICIO DE LA MODIFICACIÓN ---
        
        // 1. Cargar FACTURAS del proveedor
        List<Factura> facturas = repoFactura.listarPorProveedor(idProveedorSeleccionado);
        for (Factura f : facturas) {
            
            // Creamos la descripción base
            String descripcion = "Compra " + f.getTipo() + " " + f.getNroComprobante();

            // Creamos el DTO usando el método 'fromFactura'
            // Este método pone f.getTotal() en el 'Debe' y 0 en el 'Haber'
            MovimientosDTO dto = MovimientosDTO.fromFactura(
                    f.getFechaComprobante(),
                    f.getNroComprobante(),
                    descripcion,
                    f.getTotal(),
                    f.getEstado(),
                    f.getIdProveedor().getIdProveedor()
            );

            // --- LÓGICA DE FACTURA DE CONTADO ---
            // Basado en RF2.3: Si la factura está "Pagada" PERO no tiene una 
            // Orden de Pago asociada (f.getIdOrdenPago() == null), 
            // significa que se pagó de contado al momento de su creación.
            
            // (Necesito la entidad Factura.java para confirmar el 'getIdOrdenPago()',
            // pero asumo que existe por el 'mappedBy' en tu entidad OrdenPago)
            try {
                if ("Pagada".equals(f.getEstado()) && f.getIdOrdenPago() == null) {
                    // Si es de contado, ponemos el mismo valor en el HABER
                    dto.setHaber(f.getTotal()); 
                    // Y actualizamos la descripción para más claridad
                    dto.setDescripcion("Compra (Contado) " + f.getTipo() + " " + f.getNroComprobante());
                }
            } catch (Exception e) {
                System.out.println("Error al verificar factura de contado: " + e.getMessage());
                // Si hay un error (ej. f.getIdOrdenPago() no existe), 
                // simplemente la trata como una factura normal.
            }
            // --- FIN LÓGICA DE FACTURA DE CONTADO ---

            allMovimientos.add(dto); // Añadir a la lista temporal
        }
        
        // --- FIN DE LA MODIFICACIÓN ---
        

        // 2. Cargar ÓRDENES DE PAGO del proveedor (Sin cambios)
        List<OrdenPago> pagos = repoOrdenPago.listarPorProveedor(idProveedorSeleccionado);
        
        for (OrdenPago op : pagos) {
            
            // --- Lógica para generar la descripción detallada ---
            String descripcionPago = "Pago " + op.getFormaPago(); 
            try {
                List<Factura> facturasPagadas = op.getFacturaList();

                if (facturasPagadas != null && !facturasPagadas.isEmpty()) {
                    if (facturasPagadas.size() == 1) {
                        Factura facturaPagada = facturasPagadas.get(0); 
                        if (facturaPagada != null) {
                            descripcionPago = "Pago a Fact " + facturaPagada.getTipo() + " " + facturaPagada.getNroComprobante();
                        }
                    } else {
                        descripcionPago = "Pago Múltiple (" + facturasPagadas.size() + " facturas)";
                    }
                }
            } catch (Exception e) {
                System.out.println("No se pudo generar descripción detallada de OP: " + e.getMessage());
            }
            // --- FIN DE LA LÓGICA DE DESCRIPCIÓN ---

            MovimientosDTO dto = MovimientosDTO.fromOrdenPago(
                    op.getFechaPago(),
                    op.getNroOrden(),
                    descripcionPago, 
                    op.getMontoTotal(),
                    op.getIdProveedor().getIdProveedor()
            );
            allMovimientos.add(dto); 
        }
        

        // 3. Ordenar cronológicamente la lista COMPLETA
        Collections.sort(allMovimientos);

        // 4. Calcular saldos parciales sobre la lista COMPLETA
        // (Este método está CORRECTO y no necesita cambios)
        calcularSaldosParciales(allMovimientos);

        // 5. FILTRAR LA LISTA (si es necesario)
        if (estadoPago != null && !estadoPago.trim().isEmpty()) {

            final String filtroEstado = estadoPago; 
            movimientos = allMovimientos.stream()
                    .filter(mov -> {
                        String estadoMovimiento = mov.getEstado(); 
                        if ("Impaga".equals(filtroEstado)) {
                            return "Impaga".equals(estadoMovimiento);
                        }
                        if ("Pagada".equals(filtroEstado)) {
                            // Tu lógica original:
                            return "Pagada".equals(estadoMovimiento) || estadoMovimiento == null;
                        }
                        return false; 
                    })
                    .collect(Collectors.toList()); 
        } else {
            movimientos = allMovimientos;
        }

        // 6. Calcular saldo acumulado 
        saldoActual = (BigDecimal) repoFactura.getSaldoPendiente(idProveedorSeleccionado);

        System.out.println("=== MOVIMIENTOS CARGADOS ===");
    }

    /**
     * Calcula el saldo parcial de cada movimiento en la lista proporcionada.
     * (Este método ES CORRECTO y no necesita cambios)
     *
     * @param listaParaCalcular La lista (completa) de movimientos.
     */
    private void calcularSaldosParciales(List<MovimientosDTO> listaParaCalcular) {
        BigDecimal saldo = BigDecimal.ZERO;

        for (MovimientosDTO mov : listaParaCalcular) {
            // Saldo = Saldo anterior + Debe - Haber
            // Esto ya maneja tus dos casos.
            saldo = saldo.add(mov.getDebe()).subtract(mov.getHaber());
            mov.setSaldoParcial(saldo);
        }
    }

    // ===== GETTERS Y SETTERS =====
    // (Sin cambios)
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