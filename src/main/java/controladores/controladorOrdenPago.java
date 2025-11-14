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
import repositorios.repoFactura;
import repositorios.repoOrdenPago;
import repositorios.repoProveedor;

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

    private List<Proveedor> listaProveedores;
    private Proveedor proveedorSeleccionado;

    private List<Factura> facturasImpagas;
    private List<Factura> facturasSeleccionadas;

    private OrdenPago ordenPago;
    private String formaPago;

    private BigDecimal totalSubtotal;
    private BigDecimal totalIva;
    private BigDecimal totalOrden;
    private Integer proveedorSeleccionadoId; // <-- usar solo el ID
    private Date fechaInicio;
    private Date fechaFin;

    private List<PagoListadoDTO> listadoPagos;

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
            listadoPagos = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date fechaFinInclusive = cal.getTime();

            List<OrdenPago> ordenes = repoOrdenPago.findByFechaPagoBetween(fechaInicio, fechaFinInclusive);
            System.out.println("Cant ordenes: " + ordenes.size());

            Map<String, PagoListadoDTO> map = new HashMap<>();

            for (OrdenPago op : ordenes) {
                String proveedor = op.getIdProveedor() != null ? op.getIdProveedor().getRazonSocial() : "SIN PROVEEDOR";
                String forma = op.getFormaPago() != null ? op.getFormaPago() : "SIN FORMA";
                Date fecha = op.getFechaPago();

                String key = proveedor + "|" + forma + "|" + fecha;

                if (map.containsKey(key)) {
                    PagoListadoDTO dto = map.get(key);
                    dto.setTotalPagado(dto.getTotalPagado().add(op.getMontoTotal()));
                } else {
                    map.put(key, new PagoListadoDTO(proveedor, forma, fecha, op.getMontoTotal()));
                }
            }

            listadoPagos.addAll(map.values());

            listadoPagos.sort(
                    Comparator.comparing(PagoListadoDTO::getProveedor, Comparator.nullsLast(String::compareTo))
                            .thenComparing(PagoListadoDTO::getFormaPago, Comparator.nullsLast(String::compareTo))
                            .thenComparing(PagoListadoDTO::getFechaPago, Comparator.nullsLast(Date::compareTo))
            );

        } catch (Exception e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error interno al generar el listado: " + e.getMessage(),
                            null)
            );
        }
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

    public List<PagoListadoDTO> getListadoPagos() {
        return listadoPagos;
    }

    public void setListadoPagos(List<PagoListadoDTO> listadoPagos) {
        this.listadoPagos = listadoPagos;
    }

}
