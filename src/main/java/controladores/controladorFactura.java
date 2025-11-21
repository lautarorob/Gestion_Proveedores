package controladores;

import entidades.Factura;
import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import entidades.Producto;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import repositorios.repoFactura;
import repositorios.repoFacturaProducto;
import repositorios.repoProducto;
import repositorios.repoUsuario; // <--- IMPORTANTE

@Named(value = "controladorFactura")
@ViewScoped
public class controladorFactura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private repoFactura repoFactura;

    @Inject
    private repoFacturaProducto repoFacturaProducto;

    @Inject
    private repoProducto repoProducto;

    // --- NUEVA INYECCIÓN NECESARIA PARA LA AUDITORÍA ---
    @Inject
    private repoUsuario repoUsuario;
    // ---------------------------------------------------

    @Inject
    private controladorSesion controladorSesion;

    private List<Producto> listaProductos;
    private Integer id;
    private Factura factura;

    // Lista temporal de productos (no persistidos aún)
    private List<FacturaProducto> productosTemporales;

    // Índice para eliminar (usado por f:setPropertyActionListener)
    private Integer indiceEliminar;

    // Constante para IVA (21% típico en Argentina)
    private static final BigDecimal IVA_PORCENTAJE = new BigDecimal("0.21");

    public controladorFactura() {
    }

    @PostConstruct
    public void init() {
        productosTemporales = new ArrayList<>();
        listaProductos = repoProducto.Listar();

        if (factura == null) {
            factura = new Factura();
            factura.setFechaComprobante(new Date());
            factura.setFechaRegistro(new Date());
            // factura.setNroComprobante(generarNumeroComprobante());
        }

        facturaProducto = new FacturaProducto();
    }

    // --- LÓGICA DE PRODUCTOS ---
    private FacturaProducto facturaProducto = new FacturaProducto();

    public void agregarProducto(FacturaProducto nuevo) {
        // Validar que el objeto y el producto no sean nulos
        if (nuevo == null || nuevo.getProducto() == null) {
            FacesContext.getCurrentInstance().addMessage("formulario:producto",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Producto requerido",
                            "Debe seleccionar un producto.")
            );
            return;
        }

        // Validar cantidad
        if (nuevo.getCantidad() <= 0) {
            FacesContext.getCurrentInstance().addMessage("formulario:cantidad",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Cantidad inválida",
                            "La cantidad debe ser mayor a 0.")
            );
            return;
        }

        // Calcular subtotal
        if (nuevo.getPrecioUnitario() != null) {
            BigDecimal subtotal = nuevo.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(nuevo.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            nuevo.setSubtotal(subtotal);
        }

        nuevo.setFactura(factura);

        // Crear copia
        FacturaProducto copia = new FacturaProducto();
        copia.setFactura(factura);
        copia.setProducto(nuevo.getProducto());
        copia.setDescripcion(nuevo.getDescripcion());
        copia.setPrecioUnitario(nuevo.getPrecioUnitario());
        copia.setCantidad(nuevo.getCantidad());
        copia.setSubtotal(nuevo.getSubtotal());

        productosTemporales.add(copia);
        calcularTotales();

        // Reiniciar objeto
        facturaProducto = new FacturaProducto();
        facturaProducto.setProducto(null);
        facturaProducto.setDescripcion(null);
        facturaProducto.setPrecioUnitario(null);
        facturaProducto.setCantidad(0);
    }

    public void eliminarProductoPorIndice(int index) {
        if (productosTemporales != null && index >= 0 && index < productosTemporales.size()) {
            productosTemporales.remove(index);
            calcularTotales();
        }
    }

    public void actualizarCamposProducto() {
        if (facturaProducto != null && facturaProducto.getProducto() != null) {
            facturaProducto.setDescripcion(facturaProducto.getProducto().getDescripcion());
            facturaProducto.setPrecioUnitario(facturaProducto.getProducto().getPrecioReferencia());
        }
    }

    public void calcularTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (FacturaProducto fp : productosTemporales) {
            if (fp.getSubtotal() == null && fp.getPrecioUnitario() != null) {
                BigDecimal productoSubtotal = fp.getPrecioUnitario()
                        .multiply(new BigDecimal(fp.getCantidad()))
                        .setScale(2, RoundingMode.HALF_UP);
                fp.setSubtotal(productoSubtotal);
            }

            if (fp.getSubtotal() != null) {
                subtotal = subtotal.add(fp.getSubtotal());
            }
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        factura.setSubtotal(subtotal);

        BigDecimal iva = BigDecimal.ZERO;
        if ("A".equalsIgnoreCase(factura.getTipo())) {
            iva = subtotal.multiply(IVA_PORCENTAJE).setScale(2, RoundingMode.HALF_UP);
        }
        factura.setIva(iva);

        BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);
        factura.setTotal(total);
    }

    // --- MÉTODOS DE GUARDADO (CON FIX DE AUDITORÍA) ---
    public String guardar() {
        if (productosTemporales == null || productosTemporales.isEmpty()) {
            return null; // O mensaje de error
        }

        // Estado según forma de pago
        if (factura.getFormaPago() != null) {
            if (factura.getFormaPago().equalsIgnoreCase("contado")) {
                factura.setEstado("Pagada");
            } else if (factura.getFormaPago().equalsIgnoreCase("cuenta corriente")) {
                factura.setEstado("Impaga");
            } else {
                factura.setEstado("Desconocido");
            }
        } else {
            factura.setEstado("Impaga");
        }

        // ---------------------------------------------------------------
        // FIX AUDITORÍA: Setear el ID del usuario antes de guardar
        // ---------------------------------------------------------------
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }
        // ---------------------------------------------------------------

        // Guardar Factura
        repoFactura.Guardar(factura);

        // Guardar Productos
        for (FacturaProducto fp : productosTemporales) {
            if (fp.getFacturaProductoPK() == null) {
                FacturaProductoPK pk = new FacturaProductoPK(
                        factura.getIdFactura(),
                        fp.getProducto().getIdProducto()
                );
                fp.setFacturaProductoPK(pk);
            }
            fp.setFactura(factura);
            repoFacturaProducto.Guardar(fp);
        }

        return "/facturas/index.xhtml?faces-redirect=true";
    }

    public String guardarCambios() {
        if (factura != null && factura.getIdFactura() != null) {
            Optional<Factura> opt = repoFactura.porId(factura.getIdFactura());
            if (opt.isPresent()) {
                Factura fExistente = opt.get();

                fExistente.setFechaRegistro(factura.getFechaRegistro());
                fExistente.setFormaPago(factura.getFormaPago());

                if (fExistente.getFormaPago() != null) {
                    String forma = fExistente.getFormaPago().trim().toLowerCase();
                    if (forma.equals("contado")) {
                        fExistente.setEstado("Pagada");
                    } else if (forma.equals("cuenta corriente")) {
                        fExistente.setEstado("Impaga");
                    } else {
                        fExistente.setEstado("Desconocido");
                    }
                }

                // ---------------------------------------------------------------
                // FIX AUDITORÍA: Setear el ID del usuario antes de actualizar
                // ---------------------------------------------------------------
                if (controladorSesion != null && controladorSesion.isLogueado()) {
                    repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
                }
                // ---------------------------------------------------------------

                repoFactura.Guardar(fExistente);
            }
        }
        return "/facturas/index.xhtml?faces-redirect=true";
    }

    public String actualizarFechaYFormaPago() {
        if (factura != null && factura.getIdFactura() != null) {
            Factura facturaExistente = repoFactura.porId(factura.getIdFactura()).orElse(null);
            if (facturaExistente != null) {
                facturaExistente.setFechaRegistro(factura.getFechaRegistro());
                facturaExistente.setFormaPago(factura.getFormaPago());

                if ("Contado".equalsIgnoreCase(factura.getFormaPago())) {
                    facturaExistente.setEstado("Pagada");
                }

                // ---------------------------------------------------------------
                // FIX AUDITORÍA
                // ---------------------------------------------------------------
                if (controladorSesion != null && controladorSesion.isLogueado()) {
                    repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
                }
                // ---------------------------------------------------------------

                repoFactura.Guardar(facturaExistente);
            }
        }
        return "/facturas/index.xhtml?faces-redirect=true";
    }

    // --- OTROS MÉTODOS ---
    public void cargarFacturaPorVista() {
        if (id != null && id > 0 && (factura == null || factura.getIdFactura() == null)) {
            Optional<Factura> opt = repoFactura.obtenerFacturaConProductos(id);
            if (opt.isPresent()) {
                factura = opt.get();
                productosTemporales = new ArrayList<>(factura.getFacturaProductoList());
            } else {
                factura = new Factura();
                factura.setFechaComprobante(new Date());
            }
            calcularTotales();
        } else if (factura == null) {
            factura = new Factura();
            factura.setFechaComprobante(new Date());
        }
    }

    public List<Factura> listarImpagas() {
        return repoFactura.listarImpagas();
    }

    public List<Factura> listar() {
        return repoFactura.Listar();
    }

    // --- GETTERS Y SETTERS ---
    public repoFactura getRepoFactura() {
        return repoFactura;
    }

    public void setRepoFactura(repoFactura repoFactura) {
        this.repoFactura = repoFactura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Factura getFactura() {
        if (factura == null) {
            factura = new Factura();
            factura.setFechaComprobante(new Date());
        }
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public List<FacturaProducto> getProductosTemporales() {
        if (productosTemporales == null) {
            productosTemporales = new ArrayList<>();
        }
        return productosTemporales;
    }

    public void setProductosTemporales(List<FacturaProducto> productosTemporales) {
        this.productosTemporales = productosTemporales;
    }

    public Integer getIndiceEliminar() {
        return indiceEliminar;
    }

    public void setIndiceEliminar(Integer indiceEliminar) {
        this.indiceEliminar = indiceEliminar;
    }

    public FacturaProducto getFacturaProducto() {
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
