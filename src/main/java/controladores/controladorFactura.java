package controladores;

import entidades.Factura;
import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import repositorios.repoFactura;
import repositorios.repoFacturaProducto;

@Named(value = "controladorFactura")
@ViewScoped
public class controladorFactura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private repoFactura repoFactura;

    @Inject
    private repoFacturaProducto repoFacturaProducto;

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

        if (factura == null) {
            if (id != null && id > 0) {
                factura = repoFactura.porId(id).orElse(new Factura());
                // Si es una factura existente, cargar sus productos
                if (factura.getIdFactura() != null) {
                    productosTemporales = new ArrayList<>(factura.getFacturaProductoList());
                }
            } else {
                factura = new Factura();
                factura.setFechaRegistro(new Date());
                factura.setNroComprobante(generarNumeroComprobante());
            }
        }
        calcularTotales();
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
    public void agregarProducto(FacturaProducto facturaProducto) {
        if (facturaProducto != null && facturaProducto.getProducto() != null
                && facturaProducto.getCantidad() > 0) {

            // Calcular subtotal del producto
            if (facturaProducto.getPrecioUnitario() != null) {
                BigDecimal subtotal = facturaProducto.getPrecioUnitario()
                        .multiply(new BigDecimal(facturaProducto.getCantidad()))
                        .setScale(2, RoundingMode.HALF_UP);
                facturaProducto.setSubtotal(subtotal);
            }

            // Establecer la factura (aunque aún no tenga ID)
            facturaProducto.setFactura(factura);

            // Agregar a la lista temporal
            productosTemporales.add(facturaProducto);

            // Recalcular totales
            calcularTotales();

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

        // Primero guardar la factura para obtener el ID
        repoFactura.Guardar(factura);

        System.out.println("=== GUARDANDO FACTURA ===");
        System.out.println("ID Factura: " + factura.getIdFactura());
        System.out.println("Total productos a guardar: " + productosTemporales.size());

        // Guardar cada producto de la lista temporal
        for (FacturaProducto fp : productosTemporales) {
            // Establecer la clave compuesta con el ID de la factura
            if (fp.getFacturaProductoPK() == null) {
                FacturaProductoPK pk = new FacturaProductoPK(
                        factura.getIdFactura(),
                        fp.getProducto().getIdProducto()
                );
                fp.setFacturaProductoPK(pk);
            }
            // Asegurar que la factura esté asignada
            fp.setFactura(factura);

            // Guardar el producto
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
            factura.setFechaRegistro(new Date());
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
}
