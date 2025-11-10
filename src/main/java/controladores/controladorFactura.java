package controladores;

import entidades.Factura;
import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import entidades.Producto;
import jakarta.annotation.PostConstruct;
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

@Named(value = "controladorFactura")
@ViewScoped
public class controladorFactura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private repoFactura repoFactura;

    @Inject
    private repoFacturaProducto repoFacturaProducto;
    private List<Producto> listaProductos;

    @Inject
    private repoProducto repoProducto; // o el nombre de tu repositorio real

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

    public void eliminarProducto() {
        if (indiceEliminar != null && productosTemporales != null && !productosTemporales.isEmpty()) {
            int index = indiceEliminar;
            if (index >= 0 && index < productosTemporales.size()) {
                FacturaProducto eliminado = productosTemporales.remove(index);
                calcularTotales();
                System.out.println("Producto eliminado: " + eliminado.getDescripcion());
            } else {
                System.out.println("Índice fuera de rango: " + index);
            }
        } else {
            System.out.println("No hay productos o índice nulo");
        }
    }

//  @PostConstruct
//public void init() {
//    productosTemporales = new ArrayList<>();
//    listaProductos = repoProducto.Listar(); // carga todos los productos
//
//    if (factura == null) {
//        if (id != null && id > 0) {
//            factura = repoFactura.porId(id).orElse(new Factura());
//            if (factura.getIdFactura() != null) {
//                productosTemporales = new ArrayList<>(factura.getFacturaProductoList());
//            }
//        } else {
//            factura = new Factura();
//            factura.setFechaComprobante(new Date());
//            factura.setNroComprobante(generarNumeroComprobante());
//        }
//    }
//
//    facturaProducto = new FacturaProducto();
//    calcularTotales();
//}
    @PostConstruct
    public void init() {
        productosTemporales = new ArrayList<>();
        listaProductos = repoProducto.Listar();

        if (factura == null) {
            factura = new Factura();
            factura.setFechaComprobante(new Date());
            factura.setFechaRegistro(new Date());
            factura.setNroComprobante(generarNumeroComprobante());
        }

        facturaProducto = new FacturaProducto();
    }

    public String actualizarFechaYFormaPago() {
        if (factura != null && factura.getIdFactura() != null) {
            Factura facturaExistente = repoFactura.porId(factura.getIdFactura()).orElse(null);
            if (facturaExistente != null) {
                facturaExistente.setFechaRegistro(factura.getFechaRegistro());
                facturaExistente.setFormaPago(factura.getFormaPago());

                if ("Contado".equalsIgnoreCase(factura.getFormaPago())) {
                    facturaExistente.setEstado("Pagado");
                }

                repoFactura.Guardar(facturaExistente);
                System.out.println("Factura actualizada correctamente.");
            }
        }
        return "/facturas/index.xhtml?faces-redirect=true";
    }

    public String generarNumeroComprobante() {
        String ultimoNumero = repoFactura.obtenerUltimoComprobante();

        if (ultimoNumero == null || ultimoNumero.isEmpty()) {
            return "0001-00001000";
        }

        try {
            String[] partes = ultimoNumero.split("-");
            String prefijo = partes[0];
            int numero = Integer.parseInt(partes[1]);
            numero++;
            String nuevoNumero = String.format("%08d", numero);
            return prefijo + "-" + nuevoNumero;
        } catch (Exception e) {
            return "0001-00001000";
        }
    }

    public List<Factura> listar() {
        return repoFactura.Listar();
    }

    /**
     * Agrega un producto a la lista temporal
     */
    public void agregarProducto(FacturaProducto nuevo) {
        if (nuevo != null && nuevo.getProducto() != null && nuevo.getCantidad() > 0) {

            // Calcular subtotal
            if (nuevo.getPrecioUnitario() != null) {
                BigDecimal subtotal = nuevo.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(nuevo.getCantidad()))
                        .setScale(2, RoundingMode.HALF_UP);
                nuevo.setSubtotal(subtotal);
            }

            // Asociar la factura
            nuevo.setFactura(factura);

            // ️ Crear una copia nueva del objeto para evitar referencias duplicadas
            FacturaProducto copia = new FacturaProducto();
            copia.setFactura(factura);
            copia.setProducto(nuevo.getProducto());
            copia.setDescripcion(nuevo.getDescripcion());
            copia.setPrecioUnitario(nuevo.getPrecioUnitario());
            copia.setCantidad(nuevo.getCantidad());
            copia.setSubtotal(nuevo.getSubtotal());

            // Agregar la copia a la lista
            productosTemporales.add(copia);

            // Recalcular totales
            calcularTotales();

            //  Reiniciar el objeto para el siguiente ingreso
            facturaProducto = new FacturaProducto();

            System.out.println("=== PRODUCTO AGREGADO ===");
            System.out.println("Total productos en lista: " + productosTemporales.size());
        }
    }

    /**
     * MÉTODO ÚNICO PARA ELIMINAR - Recibe el índice directamente desde JSF
     */
    public void eliminarProductoPorIndice(int index) {
        System.out.println("=== ELIMINAR PRODUCTO ===");
        System.out.println("Índice recibido: " + index);
        System.out.println("Tamaño de lista ANTES: " + productosTemporales.size());

        if (productosTemporales != null && index >= 0 && index < productosTemporales.size()) {
            FacturaProducto productoEliminado = productosTemporales.remove(index);
            calcularTotales();

            System.out.println("Producto eliminado: " + productoEliminado.getDescripcion());
            System.out.println("Tamaño de lista DESPUÉS: " + productosTemporales.size());
        } else {
            System.out.println("ERROR: Índice fuera de rango o lista nula");
            System.out.println("Lista es null: " + (productosTemporales == null));
            if (productosTemporales != null) {
                System.out.println("Tamaño lista: " + productosTemporales.size());
            }
        }
    }

    /**
     * Calcula subtotal, IVA y total de la factura
     */
    public void calcularTotales() {
        BigDecimal subtotal = BigDecimal.ZERO;

        // Calcular subtotal sumando todos los productos
        for (FacturaProducto fp : productosTemporales) {
            // Asegurar que cada producto tenga su subtotal calculado
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

        // Calcular IVA (21%) solo si el tipo de factura es "A"
        BigDecimal iva = BigDecimal.ZERO;
        if ("A".equalsIgnoreCase(factura.getTipo())) {
            iva = subtotal.multiply(IVA_PORCENTAJE)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        factura.setIva(iva);

        // Calcular total (subtotal + IVA)
        BigDecimal total = subtotal.add(iva)
                .setScale(2, RoundingMode.HALF_UP);
        factura.setTotal(total);

        System.out.println("=== TOTALES CALCULADOS ===");
        System.out.println("Subtotal: " + subtotal);
        System.out.println("IVA: " + iva);
        System.out.println("Total: " + total);
    }

    /**
     * Guarda la factura y todos sus productos
     */
    public String guardar() {
        // Validar que haya productos
        if (productosTemporales == null || productosTemporales.isEmpty()) {
            System.out.println("ERROR: No hay productos para guardar");
            return null;
        }

        // Setear estado según la forma de pago ANTES de guardar
        if (factura.getFormaPago() != null) {
            if (factura.getFormaPago().equalsIgnoreCase("contado")) {
                factura.setEstado("Pagada");
            } else if (factura.getFormaPago().equalsIgnoreCase("cuenta corriente")) {
                factura.setEstado("Pendiente");
            } else {
                factura.setEstado("Desconocido");
            }
        } else {
            factura.setEstado("Pendiente"); // por defecto
        }

        // Primero guardar la factura para obtener el ID
        repoFactura.Guardar(factura);

        System.out.println("=== GUARDANDO FACTURA ===");
        System.out.println("ID Factura: " + factura.getIdFactura());
        System.out.println("Forma de pago: " + factura.getFormaPago());
        System.out.println("Estado asignado: " + factura.getEstado());
        System.out.println("Total productos a guardar: " + productosTemporales.size());

        // Guardar cada producto de la lista temporal
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
            System.out.println("Guardado: " + fp.getDescripcion());
        }

        return "/facturas/index.xhtml?faces-redirect=true";
    }

    // GETTERS Y SETTERS
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
            factura.setNroComprobante(generarNumeroComprobante());
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
    private FacturaProducto facturaProducto = new FacturaProducto();

    public FacturaProducto getFacturaProducto() {
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }

    public void actualizarCamposProducto() {
        if (facturaProducto != null && facturaProducto.getProducto() != null) {
            facturaProducto.setDescripcion(facturaProducto.getProducto().getDescripcion());
            facturaProducto.setPrecioUnitario(facturaProducto.getProducto().getPrecioReferencia());
        }
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public void cargarFacturaPorVista() {
        System.out.println("Cargando factura con id: " + id);

        if (id != null && id > 0 && (factura == null || factura.getIdFactura() == null)) {
            Optional<Factura> opt = repoFactura.obtenerFacturaConProductos(id); // trae factura con productos
            if (opt.isPresent()) {
                factura = opt.get();
                productosTemporales = new ArrayList<>(factura.getFacturaProductoList());
            } else {
                System.out.println("No se encontró la factura con ID: " + id);
                factura = new Factura(); // inicializa vacía
                factura.setFechaComprobante(new Date());
                factura.setNroComprobante(generarNumeroComprobante());
            }
            calcularTotales();
        } else if (factura == null) {
            // Si no hay id, es nueva factura
            factura = new Factura();
            factura.setFechaComprobante(new Date());
            factura.setNroComprobante(generarNumeroComprobante());
        }
    }

    public String guardarCambios() {
        if (factura != null && factura.getIdFactura() != null) {
            // Guardar únicamente fechaRegistro y formaPago
            Optional<Factura> opt = repoFactura.porId(factura.getIdFactura());
            if (opt.isPresent()) {
                Factura fExistente = opt.get();
                fExistente.setFechaRegistro(factura.getFechaRegistro());
                fExistente.setFormaPago(factura.getFormaPago());

                repoFactura.Guardar(fExistente);
                System.out.println("Factura actualizada correctamente.");
            }
        }
        return "/facturas/index.xhtml?faces-redirect=true";
    }

}
