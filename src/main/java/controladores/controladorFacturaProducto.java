package controladores;

import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import entidades.Producto;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.repoFacturaProducto;

@Named(value = "controladorFacturaProducto")
@RequestScoped
public class controladorFacturaProducto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private repoFacturaProducto repoFacturaProducto;
    
    private FacturaProducto facturaProducto;
    private FacturaProductoPK idCompuesta;
   // private List<Producto> listaProductos;

    public controladorFacturaProducto() {
    }
    /*
    @PostConstruct
    public void init() {
        listaProductos = repoFacturaProducto.listarActivos();
        
        // Inicializar facturaProducto
        if (idCompuesta != null) {
            repoFacturaProducto.porId(idCompuesta).ifPresent(fp -> {
                facturaProducto = fp;
            });
        } else {
            facturaProducto = new FacturaProducto();
        }
    }
    
    // MÉTODO PARA ACTUALIZAR DESCRIPCIÓN Y PRECIO
    public void actualizarDescripcion() {
        if (facturaProducto == null) {
            facturaProducto = new FacturaProducto();
        }
        
        if (facturaProducto.getProducto() != null) {
            Producto productoSeleccionado = facturaProducto.getProducto();
            
            // Actualizar descripción
            if (productoSeleccionado.getDescripcion() != null) {
                facturaProducto.setDescripcion(productoSeleccionado.getDescripcion());
            } else {
                facturaProducto.setDescripcion("");
            }
            
            // Actualizar precio unitario
            if (productoSeleccionado.getPrecioReferencia() != null) {
                facturaProducto.setPrecioUnitario(productoSeleccionado.getPrecioReferencia());
            } else {
                facturaProducto.setPrecioUnitario(java.math.BigDecimal.ZERO);
            }
        } else {
            facturaProducto.setDescripcion("");
            facturaProducto.setPrecioUnitario(null);
        }
    }
    
    /**
     * Agrega el producto actual a la lista temporal y limpia el formulario
     * Obtiene el controladorFactura desde el contexto de JSF
     *//*
    public void agregarProductoALista() {
        if (facturaProducto == null) {
            facturaProducto = new FacturaProducto();
        }
        
        // Asegurar que la descripción y precio se actualicen antes de agregar
        if (facturaProducto.getProducto() != null) {
            actualizarDescripcion();
        }
        
        if (facturaProducto != null && facturaProducto.getProducto() != null 
            && facturaProducto.getCantidad() > 0) {
            
            // Obtener el controladorFactura desde el contexto de JSF
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.el.ELContext elContext = facesContext.getELContext();
            jakarta.el.ELResolver elResolver = facesContext.getApplication().getELResolver();
            Object bean = elResolver.getValue(elContext, null, "controladorFactura");
            
            if (bean instanceof controladores.controladorFactura) {
                controladores.controladorFactura controladorFactura = (controladores.controladorFactura) bean;
                
                // Crear una copia del producto para agregar a la lista
                FacturaProducto nuevoProducto = new FacturaProducto();
                nuevoProducto.setProducto(facturaProducto.getProducto());
                
                // Si la descripción está vacía, obtenerla del producto
                String descripcion = facturaProducto.getDescripcion();
                if (descripcion == null || descripcion.trim().isEmpty()) {
                    descripcion = facturaProducto.getProducto().getDescripcion();
                }
                nuevoProducto.setDescripcion(descripcion);
                
                // Si el precio unitario es null, obtenerlo del producto
                java.math.BigDecimal precioUnitario = facturaProducto.getPrecioUnitario();
                if (precioUnitario == null) {
                    precioUnitario = facturaProducto.getProducto().getPrecioReferencia();
                }
                nuevoProducto.setPrecioUnitario(precioUnitario);
                
                nuevoProducto.setCantidad(facturaProducto.getCantidad());
                
                // Agregar a la lista temporal en el controlador de factura
                controladorFactura.agregarProducto(nuevoProducto);
                
                // Limpiar el formulario
                limpiarFormulario();
            }
        }
    }*/
    
    /**
     * Limpia el formulario de producto
     */
    public void limpiarFormulario() {
        facturaProducto = new FacturaProducto();
    }
    
    public List<FacturaProducto> listar() {
        return repoFacturaProducto.Listar();
    }

    public String guardar() {
        repoFacturaProducto.Guardar(facturaProducto);
        return "/facturas/index.xhtml?faces-redirect=true";
    }
/*
    // GETTERS Y SETTERS
    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }*/

    public repoFacturaProducto getRepoFacturaProducto() {
        return repoFacturaProducto;
    }

    public void setRepoFacturaProducto(repoFacturaProducto repoFacturaProducto) {
        this.repoFacturaProducto = repoFacturaProducto;
    }

    public FacturaProducto getFacturaProducto() {
        return facturaProducto;
    }

    public void setFacturaProducto(FacturaProducto facturaProducto) {
        this.facturaProducto = facturaProducto;
    }

    public FacturaProductoPK getIdCompuesta() {
        return idCompuesta;
    }

    public void setIdCompuesta(FacturaProductoPK idCompuesta) {
        this.idCompuesta = idCompuesta;
    }
}