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
import java.util.Date;
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

    public List<Auditoria> filtrar(Integer idUsuario, Date fechaDesde, Date fechaHasta) {
        String jpql = "SELECT a FROM Auditoria a WHERE 1=1";

        if (idUsuario != null) {
            jpql += " AND a.idusuarioUltimo.idUsuario = :usuario";
        }
        if (fechaDesde != null) {
            jpql += " AND a.fechaMovimiento >= :desde";
        }
        if (fechaHasta != null) {
            jpql += " AND a.fechaMovimiento <= :hasta";
        }
        jpql += " ORDER BY a.fechaMovimiento DESC";

        var query = em.createQuery(jpql, Auditoria.class);

        if (idUsuario != null) {
            query.setParameter("usuario", idUsuario);
        }
        if (fechaDesde != null) {
            query.setParameter("desde", fechaDesde);
        }
        if (fechaHasta != null) {
            query.setParameter("hasta", fechaHasta);
        }

        return query.getResultList();
    }

}
