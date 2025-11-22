/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Proveedor;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.List;
import repositorios.repoProveedor;
import repositorios.repoUsuario; // <--- IMPORTANTE

/**
 *
 * @author roble
 */
@Named(value = "controladorProveedor")
@RequestScoped
public class controladorProveedor {

    @Inject
    private repoProveedor repoProveedor;

    // --- INYECCIONES PARA AUDITORÍA ---
    @Inject
    private repoUsuario repoUsuario;

    private Proveedor proveedor;

    private Integer id;

    private String proveedorSeleccionado; // ID del proveedor seleccionado en el filtro de búsqueda

    private List<Proveedor> listaProveedores;

    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    public String getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    public void setProveedorSeleccionado(String proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }

    public controladorProveedor() {
    }

    @Model
    @Produces
    public Proveedor proveedor() {
        System.out.println("id" + id);
        if (id != null && id > 0) {
            repoProveedor.porId(id).ifPresent(p -> {
                proveedor = p;
            });
        } else {
            proveedor = new Proveedor();
        }
        return proveedor;
    }

    @PostConstruct
    public void init() {
        listaProveedores = repoProveedor.Listar();
    }

    public void limpiarFiltro() {
        proveedorSeleccionado = null; 
        listaProveedores = repoProveedor.Listar();
    }

    public void buscarProveedor() {
        if (proveedorSeleccionado == null || proveedorSeleccionado.isEmpty()) {
            // Si no hay filtro, mostrar todos
            listaProveedores = repoProveedor.Listar();
        } else {
            // Si hay filtro, buscar por ID
            listaProveedores = repoProveedor.buscarPorProveedor(Integer.valueOf(proveedorSeleccionado));
        }
    }

    public List<Proveedor> listar() {
        return repoProveedor.Listar();
    }

    public String guardar() {
        repoProveedor.Guardar(proveedor);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoProveedor.Eliminar(id);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String bajaLogica(Integer id) {
        repoProveedor.BajaLogica(id);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String reactivar(Integer id) {
        repoProveedor.Reactivar(id);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    // ------------------------------------
    public repoProveedor getRepoProveedor() {
        return repoProveedor;
    }

    public void setRepoProveedor(repoProveedor repoProveedor) {
        this.repoProveedor = repoProveedor;
    }

    public Proveedor getProveedor() {
        if (proveedor == null) {
            proveedor();
        }
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
