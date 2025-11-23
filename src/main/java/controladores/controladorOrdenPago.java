package controladores;

import DTO.PagoListadoDTO;
import entidades.Factura;
import entidades.OrdenPago;
import entidades.Proveedor;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import repositorios.repoFactura;
import repositorios.repoOrdenPago;
import repositorios.repoProveedor;
import repositorios.repoUsuario; // <--- IMPORTANTE

@Named(value = "controladorOrdenPago")
@ViewScoped
public class controladorOrdenPago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private repoOrdenPago repoOrdenPago;

    @Inject
    private repoFactura repoFactura;

    @Inject
    private repoProveedor repoProveedor;

    // --- INYECCIONES PARA AUDITORÍA ---
    //@Inject
    //private repoUsuario repoUsuario;

    private List<Proveedor> listaProveedores;
    private Proveedor proveedorSeleccionado;

    private List<Factura> facturasImpagas;
    private List<Factura> facturasSeleccionadas;
    private List<OrdenPago> listadoPagos;

    private OrdenPago ordenPago;
    private String formaPago;

    private BigDecimal totalSubtotal;
    private BigDecimal totalIva;
    private BigDecimal totalOrden;
    private Integer proveedorSeleccionadoId; // <-- usar solo el ID
    private Date fechaInicio;
    private Date fechaFin;

    private Integer proveedorFiltroId;
    private String formaPagoFiltro;

    public Integer getProveedorFiltroId() {
        return proveedorFiltroId;
    }

    public void setProveedorFiltroId(Integer proveedorFiltroId) {
        this.proveedorFiltroId = proveedorFiltroId;
    }

    public String getFormaPagoFiltro() {
        return formaPagoFiltro;
    }

    public void setFormaPagoFiltro(String formaPagoFiltro) {
        this.formaPagoFiltro = formaPagoFiltro;
    }

    public Integer getProveedorSeleccionadoId() {
        return proveedorSeleccionadoId;
    }

    public void setProveedorSeleccionadoId(Integer proveedorSeleccionadoId) {
        this.proveedorSeleccionadoId = proveedorSeleccionadoId;
    }

    @PostConstruct
    public void init() {
        listaProveedores = repoProveedor.Listar();
        if (listaProveedores == null) {
            listaProveedores = new ArrayList<>();
        }

        facturasImpagas = new ArrayList<>();
        facturasSeleccionadas = new ArrayList<>();
        ordenPago = new OrdenPago();

        totalSubtotal = BigDecimal.ZERO;
        totalIva = BigDecimal.ZERO;
        totalOrden = BigDecimal.ZERO;
        listarTodo();
    }

    // ============================
    // FILTRAR FACTURAS IMPAGAS
    // ============================
    public void filtrarFacturasPorProveedor() {
        if (proveedorSeleccionadoId != null) {
            proveedorSeleccionado = listaProveedores.stream()
                    .filter(p -> p.getIdProveedor().equals(proveedorSeleccionadoId))
                    .findFirst()
                    .orElse(null);

            System.out.println("Proveedor seleccionado: " + proveedorSeleccionado.getRazonSocial());

            facturasImpagas = repoFactura.listarImpagasPorProveedor(proveedorSeleccionadoId);

            System.out.println("Facturas impagas encontradas: " + facturasImpagas.size());
            for (Factura f : facturasImpagas) {
                System.out.println(" - " + f.getNroComprobante() + " | Total: " + f.getTotal());
            }
        } else {
            facturasImpagas = new ArrayList<>();
            System.out.println("No se seleccionó ningún proveedor.");
        }

        facturasSeleccionadas.clear();
        recalcularTotales();
    }

    // ============================
    // ACTUALIZAR FACTURAS SELECCIONADAS
    // ============================
    public void actualizarFacturasSeleccionadas() {
        facturasSeleccionadas.clear();
        for (Factura f : facturasImpagas) {
            if (f.isSeleccionada()) {
                facturasSeleccionadas.add(f);
            }
        }
        recalcularTotales();
    }

    public void actualizarSeleccion(Factura f) {
        System.out.println("Factura: " + f.getNroComprobante() + " seleccionada? " + f.isSeleccionada());

        if (f.isSeleccionada()) {
            if (!facturasSeleccionadas.contains(f)) {
                facturasSeleccionadas.add(f);
            }
        } else {
            facturasSeleccionadas.remove(f);
        }

        recalcularTotales();

        System.out.println("Total Subtotal: " + totalSubtotal);
        System.out.println("Total IVA: " + totalIva);
        System.out.println("Total Orden: " + totalOrden);
    }
// ============================
// SELECCIONAR / DESELECCIONAR FACTURA
// ============================

    public void seleccionarFactura(Factura f) {
        if (f.isSeleccionada()) {
            // Si no estaba ya en la lista, agregar
            if (!facturasSeleccionadas.contains(f)) {
                facturasSeleccionadas.add(f);
            }
        } else {
            // Si se deselecciona, quitar de la lista
            facturasSeleccionadas.remove(f);
        }
        // Recalcular los totales cada vez que se selecciona o deselecciona
        recalcularTotales();
    }

    // ============================
    // RECALCULAR TOTALES
    // ============================
    public void recalcularTotales() {
        totalSubtotal = BigDecimal.ZERO;
        totalIva = BigDecimal.ZERO;
        totalOrden = BigDecimal.ZERO;

        for (Factura f : facturasSeleccionadas) {
            if (f.getSubtotal() != null) {
                totalSubtotal = totalSubtotal.add(f.getSubtotal());
            }
            if (f.getIva() != null) {
                totalIva = totalIva.add(f.getIva());
            }
            if (f.getTotal() != null) {
                totalOrden = totalOrden.add(f.getTotal());
            }
        }

        totalSubtotal = totalSubtotal.setScale(2, RoundingMode.HALF_UP);
        totalIva = totalIva.setScale(2, RoundingMode.HALF_UP);
        totalOrden = totalOrden.setScale(2, RoundingMode.HALF_UP);
    }

    // ============================
    // GENERAR NRO DE ORDEN
    // ============================
    private String generarNumeroOrden() {
        String ultimo = repoOrdenPago.obtenerUltimoNumeroOrden();
        String prefijo = "OP-" + java.time.LocalDate.now().getYear();

        if (ultimo == null) {
            return prefijo + "-000001";
        }
        try {
            String[] partes = ultimo.split("-");
            int num = Integer.parseInt(partes[2]);
            num++;
            return prefijo + "-" + String.format("%06d", num);

        } catch (Exception e) {
            return prefijo + "-000001";

        }

    }

    // ============================
    // CONFIRMAR ORDEN DE PAGO
    // ============================
    public String confirmarPago() {
        if (proveedorSeleccionado == null || facturasSeleccionadas.isEmpty()) {
            System.out.println("Debe seleccionar un proveedor y al menos una factura.");
            return null;
        }

        ordenPago.setNroOrden(generarNumeroOrden());
        ordenPago.setFechaPago(new Date());
        ordenPago.setFormaPago(formaPago);
        ordenPago.setIdProveedor(proveedorSeleccionado);
        ordenPago.setMontoTotal(totalOrden);

        repoOrdenPago.Guardar(ordenPago);

        for (Factura f : facturasSeleccionadas) {
            f.setEstado("Pagada");
            f.setIdOrdenPago(ordenPago);
            repoFactura.Guardar(f);
        }

        reiniciarOrden();

        return "/pagos/index.xhtml?faces-redirect=true";
    }

    public void reiniciarOrden() {
        facturasImpagas.clear();
        facturasSeleccionadas.clear();
        ordenPago = new OrdenPago();
        totalSubtotal = BigDecimal.ZERO;
        totalIva = BigDecimal.ZERO;
        totalOrden = BigDecimal.ZERO;
        proveedorSeleccionado = null;
        formaPago = null;
    }

    public void generarListadoPagos() {
        try {
            if (fechaInicio != null && fechaFin != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaFin);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                fechaFin = cal.getTime();
            }

            List<OrdenPago> ordenes = repoOrdenPago.buscarConFiltros(
                    proveedorSeleccionadoId,
                    formaPago,
                    fechaInicio,
                    fechaFin
            );

            // Aplicar filtros adicionales
            listadoPagos = ordenes.stream()
                    .filter(op -> proveedorFiltroId == null
                    || (op.getIdProveedor() != null
                    && op.getIdProveedor().getIdProveedor().equals(proveedorFiltroId)))
                    .filter(op -> formaPagoFiltro == null || formaPagoFiltro.isEmpty()
                    || (op.getFormaPago() != null
                    && op.getFormaPago().equalsIgnoreCase(formaPagoFiltro)))
                    .collect(Collectors.toList());

            // Ordenamiento
            listadoPagos.sort(
                    Comparator.comparing(
                            (OrdenPago op)
                            -> op.getIdProveedor() != null ? op.getIdProveedor().getRazonSocial() : "SIN PROVEEDOR",
                            Comparator.nullsLast(String::compareTo)
                    )
                            .thenComparing(
                                    op -> op.getFormaPago() != null ? op.getFormaPago() : "SIN FORMA",
                                    Comparator.nullsLast(String::compareTo)
                            )
                            .thenComparing(OrdenPago::getFechaPago, Comparator.nullsLast(Date::compareTo))
                            .thenComparing(OrdenPago::getNroOrden, Comparator.nullsLast(String::compareTo)) // opcional
            );

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error interno al generar el listado: " + e.getMessage(), null)
            );
        }
    }

    public void limpiarFiltros() {
        fechaInicio = null;
        fechaFin = null;
        proveedorSeleccionadoId = null;
        formaPago = null;
        proveedorFiltroId = null;
        formaPagoFiltro = null;
        listarTodo();

    }

    // ============================
    // GETTERS Y SETTERS
    // ============================
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    public List<Factura> getFacturasImpagas() {
        return facturasImpagas;
    }

    public List<Factura> getFacturasSeleccionadas() {
        return facturasSeleccionadas;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public BigDecimal getTotalSubtotal() {
        return totalSubtotal;
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public BigDecimal getTotalOrden() {
        return totalOrden;
    }

    public OrdenPago getOrdenPago() {
        return ordenPago;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<OrdenPago> getListadoPagos() {
        return listadoPagos;
    }

    public void setListadoPagos(List<OrdenPago> listadoPagos) {
        this.listadoPagos = listadoPagos;
    }

    public void listarTodo() {
        listadoPagos = repoOrdenPago.buscarConFiltros(null, null, null, null);
    }
}
