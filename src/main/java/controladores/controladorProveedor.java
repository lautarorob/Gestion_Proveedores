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

    @Inject
    private controladorSesion controladorSesion;
    // ----------------------------------

    private Proveedor proveedor;

    private Integer id;

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

    public List<Proveedor> listar() {
        return repoProveedor.Listar();
    }

    // --- MÉTODOS CON FIX DE AUDITORÍA ---
    public String guardar() {
        // FIX AUDITORÍA
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }

        repoProveedor.Guardar(proveedor);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        // FIX AUDITORÍA
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }

        repoProveedor.Eliminar(id);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String bajaLogica(Integer id) {
        // FIX AUDITORÍA
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }

        repoProveedor.BajaLogica(id);
        return "/proveedores/index.xhtml?faces-redirect=true";
    }

    public String reactivar(Integer id) {
        // FIX AUDITORÍA
        if (controladorSesion != null && controladorSesion.isLogueado()) {
            repoUsuario.setCurrentUserId(controladorSesion.getUsuarioLogueado().getIdUsuario());
        }

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
