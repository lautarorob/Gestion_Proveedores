/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
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

/**
 *
 * @author roble
 */
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
    
    // Constante para IVA (21% típico en Argentina)
    private static final BigDecimal IVA_PORCENTAJE = new BigDecimal("0.21");

    /**
     * Creates a new instance of controladorFactura
     */
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
                // Establece la fecha SOLO si es una factura nueva
                factura.setFechaRegistro(new Date());
                // Establece el número de comprobante SOLO si es una factura nueva
                factura.setNroComprobante(generarNumeroComprobante());
            }
        }
        calcularTotales();
    }

    public String generarNumeroComprobante() {
        String ultimoNumero = repoFactura.obtenerUltimoComprobante();

        if (ultimoNumero == null || ultimoNumero.isEmpty()) {
            return "0001-00001000"; //primera factura
        }

        try {
            // Separacion de partes: "0001-00000123" -> ["0001", "00000123"]
            String[] partes = ultimoNumero.split("-");
            String prefijo = partes[0];
            int numero = Integer.parseInt(partes[1]); //se pasa entero para poder incrementarlo

            numero++;

            // Formateo con ceros a la izquierda
            String nuevoNumero = String.format("%08d", numero);

            return prefijo + "-" + nuevoNumero;
        } catch (Exception e) {
            return "0001-00001000"; //por defecto
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
        }
    }
    
    /**
     * Elimina un producto de la lista temporal
     */
    public void eliminarProducto(int index) {
        if (index >= 0 && index < productosTemporales.size()) {
            productosTemporales.remove(index);
            calcularTotales();
        }
    }
    
    /**
     * Elimina un producto por índice desde parámetro (para JSF)
     */
    public void eliminarProductoPorIndice() {
        jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
        
        // Obtener el parámetro "indice" de diferentes formas
        String indiceStr = null;
        
        // Intentar obtener del parámetro de la petición
        indiceStr = facesContext.getExternalContext().getRequestParameterMap().get("indice");
        
        // Si no está ahí, intentar obtener del formulario
        if (indiceStr == null || indiceStr.isEmpty()) {
            jakarta.faces.component.UIComponent form = facesContext.getViewRoot().findComponent("formulario");
            if (form != null) {
                jakarta.faces.component.UIInput input = (jakarta.faces.component.UIInput) 
                    form.findComponent("indice");
                if (input != null) {
                    indiceStr = (String) input.getValue();
                }
            }
        }
        
        // También intentar obtener del mapa de parámetros con el nombre completo del formulario
        if (indiceStr == null || indiceStr.isEmpty()) {
            indiceStr = facesContext.getExternalContext().getRequestParameterMap().get("formulario:indice");
        }
        
        System.out.println("=== ELIMINAR PRODUCTO ===");
        System.out.println("Parámetro indice recibido: " + indiceStr);
        System.out.println("Todos los parámetros: " + facesContext.getExternalContext().getRequestParameterMap());
        
        if (indiceStr != null && !indiceStr.isEmpty()) {
            try {
                int index = Integer.parseInt(indiceStr);
                System.out.println("Índice parseado: " + index);
                System.out.println("Tamaño de lista antes: " + productosTemporales.size());
                
                if (index >= 0 && index < productosTemporales.size()) {
                    productosTemporales.remove(index);
                    calcularTotales();
                    System.out.println("Producto eliminado. Tamaño de lista después: " + productosTemporales.size());
                } else {
                    System.out.println("Índice fuera de rango: " + index + " (tamaño: " + productosTemporales.size() + ")");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error al parsear índice: " + indiceStr);
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: No se recibió el parámetro 'indice'");
        }
    }
    
    /**
     * Método alternativo que recibe el índice directamente
     */
    public void eliminarProductoPorIndice(int index) {
        System.out.println("=== ELIMINAR PRODUCTO (método directo) ===");
        System.out.println("Índice recibido: " + index);
        System.out.println("Tamaño de lista antes: " + productosTemporales.size());
        
        if (index >= 0 && index < productosTemporales.size()) {
            productosTemporales.remove(index);
            calcularTotales();
            System.out.println("Producto eliminado. Tamaño de lista después: " + productosTemporales.size());
        }
    }
    
    /**
     * Calcula subtotal, IVA y total de la factura
     * El IVA solo se aplica a facturas de tipo A
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
        factura.setSubtotal(subtotal != null ? subtotal : BigDecimal.ZERO);
        
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
    }
    
    /**
     * Guarda la factura y todos sus productos
     */
    public String guardar() {
        // Primero guardar la factura para obtener el ID
        repoFactura.Guardar(factura);
        
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
        }
        
        return "/facturas/index.xhtml?faces-redirect=true";
    }

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
        return productosTemporales;
    }

    public void setProductosTemporales(List<FacturaProducto> productosTemporales) {
        this.productosTemporales = productosTemporales;
    }

}
