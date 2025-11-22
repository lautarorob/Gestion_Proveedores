/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Auditoria;
import entidades.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import repositorios.repoAuditoria;
import repositorios.repoUsuario;

/**
 *
 * @author bgsof
 */
@Named(value = "controladorAuditoria")
@ViewScoped
public class controladorAuditoria implements Serializable {

    /**
     * Creates a new instance of controladorAuditoria
     */
    public controladorAuditoria() {
        System.out.println("Bean CONTROLADOR AUDITORIA creado!");
    }
    // Inyección del Repositorio (EJB/CDI)
    @Inject
    private repoAuditoria repo;

    private List<Auditoria> lista; // La propiedad que usará la vista
    private Integer idUsuario; // filtro
    private Date fechaDesde;   // filtro
    private Date fechaHasta;   // filtro
    private List<Usuario> listaUsuarios; // lista para el combo
    @Inject
    private repoUsuario repoUsuario;  // EJB inyectado

    /**
     * Se ejecuta inmediatamente después de que el bean es construido e
     * inyectado. Es el lugar ideal para cargar datos iniciales.
     */
    @PostConstruct
    public void init() {
        try {
            idUsuario = null;     // Para que el SelectOneMenu no muestre un usuario por defecto
            fechaDesde = null;
            fechaHasta = null;
            this.lista = repo.Listar();
            listaUsuarios = repoUsuario.Listar();
            // Esto es crucial para la depuración. Si esto imprime 0, el problema es el repo o el persistence.xml.
            System.out.println("Carga de Auditoría exitosa. Registros: " + this.lista.size());
        } catch (Exception e) {
            System.err.println("Error al cargar la lista de auditoría: " + e.getMessage());
            e.printStackTrace();
            // Aquí puedes añadir un FacesMessage para notificar al usuario en la vista
        }
    }

    public void filtrar() {
        Date fechaHastaAjustada = null;

        if (fechaHasta != null) {
            // Sumar 1 día menos 1 milisegundo para abarcar todo el día
            fechaHastaAjustada = new Date(fechaHasta.getTime() + (24 * 60 * 60 * 1000) - 1);
        }

        lista = repo.filtrar(idUsuario, fechaDesde, fechaHastaAjustada);

        if (lista == null || lista.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Sin resultados",
                            "No se encontraron registros con los filtros aplicados."));
        }
    }

    public void limpiar() {
        idUsuario = null;
        fechaDesde = null;
        fechaHasta = null;
        lista = repo.Listar(); // recarga todo
    }

    // --- GETTER OBLIGATORIO PARA JSF ---
    /**
     * JSF usa este getter para acceder a los datos: #{auditoriaBean.lista}
     */
    public List<Auditoria> getLista() {
        return lista;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

}
