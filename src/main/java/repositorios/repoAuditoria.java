/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Auditoria;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author roble
 */
@Stateless
public class repoAuditoria {

    @PersistenceContext
    private EntityManager em;

    public List<Auditoria> Listar() {
        System.out.println("Ejecutando consulta de Auditoria...");
        return em.createQuery("SELECT a FROM Auditoria a", Auditoria.class)
                .getResultList();
    }

    public void Guardar(Auditoria u) {
        if (u.getIdAuditoria() != null && u.getIdAuditoria() > 0) {
            em.merge(u);
        } else {
            em.persist(u);
        }
    }
}
