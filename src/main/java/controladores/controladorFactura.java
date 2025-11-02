/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Factura;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.List;
import repositorios.repoFactura;

/**
 *
 * @author roble
 */
@Named(value = "controladorFactura")
@RequestScoped
public class controladorFactura {

    @Inject
    private repoFactura repoFactura;

    private Integer id;

    private Factura factura;

    /**
     * Creates a new instance of controladorFactura
     */
    public controladorFactura() {
    }

    @Model
    @Produces
    public Factura factura() {
        System.out.println("id" + id);
        if (id != null && id > 0) {
            repoFactura.porId(id).ifPresent(f -> {
                factura = f;
            });
        } else {
            factura = new Factura();
        }
        return factura;
    }
    
    public List<Factura> listar() {
        return repoFactura.Listar();
    }

    public String guardar() {
        repoFactura.Guardar(factura);
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
            factura();
        }
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    
    

}
