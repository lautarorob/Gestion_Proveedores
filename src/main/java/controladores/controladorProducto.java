/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Producto;
import entidades.Proveedor;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.List;
import repositorios.repoProducto;
import repositorios.repoUsuario; // <--- IMPORTANTE

/**
 *
 * @author roble
 */
@Named(value = "controladorProducto")
@RequestScoped
public class controladorProducto {

    @Inject
    private repoProducto repoProducto;

    // --- INYECCIONES PARA AUDITORÍA ---
    @Inject
    private repoUsuario repoUsuario;

    private Integer id;

    private Producto producto;

    private String proveedorSeleccionado; // ID del proveedor seleccionado en el filtro de búsqueda

    private String nombreBusqueda; // Para busqueda con barra

    private List<Proveedor> listaProveedores; // Lista de proveedores activos

    private List<Producto> listaProductos; // Lista de productos a mostrar en la tabla

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

    //Filtra por nombre de producto, proveedor, ambos o ninguno
    public void buscarProductos() {
        if ((proveedorSeleccionado == null || proveedorSeleccionado.isEmpty())
                && (nombreBusqueda == null || nombreBusqueda.trim().isEmpty())) {
            // Sin filtros, mostrar todos
            listaProductos = repoProducto.Listar();
        } else if (proveedorSeleccionado != null && !proveedorSeleccionado.isEmpty()
                && (nombreBusqueda == null || nombreBusqueda.trim().isEmpty())) {
            // Solo filtrar por proveedor
            listaProductos = repoProducto.buscarPorProveedor(Integer.valueOf(proveedorSeleccionado));
        } else if ((proveedorSeleccionado == null || proveedorSeleccionado.isEmpty())
                && nombreBusqueda != null && !nombreBusqueda.trim().isEmpty()) {
            // Solo filtrar por nombre
            listaProductos = repoProducto.buscarPorNombre(nombreBusqueda);
        } else {
            // Filtrar por ambos
            listaProductos = repoProducto.buscarPorProveedorYNombre(
                    Integer.valueOf(proveedorSeleccionado), nombreBusqueda);
        }
    }

    //Incializacion de listas para filtrado por proveedor
    @PostConstruct
    public void init() {
        listaProveedores = repoProducto.listarActivos();
        listaProductos = repoProducto.Listar();
    }

    public List<Producto> listar() {
        return repoProducto.Listar();
    }

    public String guardar() {
        repoProducto.Guardar(producto);
        return "/productos/index.xhtml?faces-redirect=true";
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

    // ------------------------------------
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
