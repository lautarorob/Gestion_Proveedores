/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Auditoria;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import repositorios.repoAuditoria;

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

    /**
     * Se ejecuta inmediatamente después de que el bean es construido e
     * inyectado. Es el lugar ideal para cargar datos iniciales.
     */
    @PostConstruct
    public void init() {
        try {
            this.lista = repo.Listar();
            // Esto es crucial para la depuración. Si esto imprime 0, el problema es el repo o el persistence.xml.
            System.out.println("Carga de Auditoría exitosa. Registros: " + this.lista.size());
        } catch (Exception e) {
            System.err.println("Error al cargar la lista de auditoría: " + e.getMessage());
            e.printStackTrace();
            // Aquí puedes añadir un FacesMessage para notificar al usuario en la vista
        }
    }

    // --- GETTER OBLIGATORIO PARA JSF ---
    /**
     * JSF usa este getter para acceder a los datos: #{auditoriaBean.lista}
     */
    public List<Auditoria> getLista() {
        return lista;
    }

}
