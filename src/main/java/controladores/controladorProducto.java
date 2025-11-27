package controladores;

import entidades.Producto;
import entidades.Proveedor;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;  // <--- AGREGAR
import jakarta.faces.context.FacesContext;      // <--- AGREGAR
import java.util.List;
import repositorios.repoProducto;
import repositorios.repoUsuario;

@Named(value = "controladorProducto")
@RequestScoped
public class controladorProducto {

    @Inject
    private repoProducto repoProducto;

    private Integer id;
    private Producto producto;
    private String proveedorSeleccionado;
    private String nombreBusqueda;
    private List<Proveedor> listaProveedores;
    private List<Producto> listaProductos;

    public controladorProducto() {
    }

    @Model
    @Produces
    public Producto producto() {
        System.out.println("id" + id);
        if (id != null && id > 0) {
            repoProducto.porId(id).ifPresent(p -> {
                producto = p;
            });
        } else {
            producto = new Producto();
        }
        return producto;
    }

    public void buscarProductos() {
        if ((proveedorSeleccionado == null || proveedorSeleccionado.isEmpty())
                && (nombreBusqueda == null || nombreBusqueda.trim().isEmpty())) {
            listaProductos = repoProducto.Listar();
        } else if (proveedorSeleccionado != null && !proveedorSeleccionado.isEmpty()
                && (nombreBusqueda == null || nombreBusqueda.trim().isEmpty())) {
            listaProductos = repoProducto.buscarPorProveedor(Integer.valueOf(proveedorSeleccionado));
        } else if ((proveedorSeleccionado == null || proveedorSeleccionado.isEmpty())
                && nombreBusqueda != null && !nombreBusqueda.trim().isEmpty()) {
            listaProductos = repoProducto.buscarPorNombre(nombreBusqueda);
        } else {
            listaProductos = repoProducto.buscarPorProveedorYNombre(
                    Integer.valueOf(proveedorSeleccionado), nombreBusqueda);
        }
    }

    @PostConstruct
    public void init() {
        listaProveedores = repoProducto.listarActivos();
        listaProductos = repoProducto.Listar();
    }

    public List<Producto> listar() {
        return repoProducto.Listar();
    }

    public String guardar() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();

            // Validar código duplicado ANTES de guardar
            if (repoProducto.existeCodProd(producto.getCodProd(), producto.getIdProducto())) {
                context.addMessage("formulario:codProd",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Código duplicado",
                                "El producto '" + producto.getCodProd() + "' ya existe"));
                return null;
            }

            // Validación adicional: nombre no puede estar vacío
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                context.addMessage("formulario:errorNombre",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Campo requerido",
                                "El nombre del producto es obligatorio"));
                return null;
            }
      

            // Si no está duplicado, guardar
            String resultado = repoProducto.Guardar(producto);

            if ("OK".equals(resultado)) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Éxito",
                                "Producto guardado correctamente"));
                limpiar();
                return "/productos/index.xhtml?faces-redirect=true";
            } else {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error",
                                "No se pudo guardar el producto"));
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error",
                            "Ocurrió un error al guardar el producto"));
            return null;
        }
    }

    // AGREGAR ESTE MÉTODO
    public void limpiar() {
        producto = new Producto();
        id = null;
    }

    public String eliminar(Integer id) {
        repoProducto.Eliminar(id);
        return "/productos/index.xhtml?faces-redirect=true";
    }

    public String bajaLogica(Integer id) {
        repoProducto.BajaLogica(id);
        return "/productos/index.xhtml?faces-redirect=true";
    }

    public String reactivar(Integer id) {
        repoProducto.Reactivar(id);
        return "/productos/index.xhtml?faces-redirect=true";
    }

    // GETTERS Y SETTERS...
    public repoProducto getRepoProducto() {
        return repoProducto;
    }

    public void setRepoProducto(repoProducto repoProducto) {
        this.repoProducto = repoProducto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Producto getProducto() {
        if (producto == null) {
            producto();
        }
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public String getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProveedorSeleccionado(String proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    public String getNombreBusqueda() {
        return nombreBusqueda;
    }

    public void setNombreBusqueda(String nombreBusqueda) {
        this.nombreBusqueda = nombreBusqueda;
    }
}
