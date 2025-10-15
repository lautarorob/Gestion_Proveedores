/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidades;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author roble
 */
public class GestorDB {
    @PersistenceContext(name = "com_GestionProveedores_war_1.0PU")
    
    EntityManager em;
    
    @RequestScoped
    @Produces
    public EntityManager generarEM(){
        return em;
    }
    
}
