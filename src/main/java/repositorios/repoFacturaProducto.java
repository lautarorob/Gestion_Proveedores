/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import entidades.Producto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author roble
 */
@Stateless
public class repoFacturaProducto {

    @Inject
    EntityManager em;

    public void Guardar(FacturaProducto fp) {
        if (fp.getFacturaProductoPK() != null) {
            em.merge(fp);
        } else {
            em.persist(fp);
        }
    }

    public void Eliminar(FacturaProductoPK facturaProductoPK) {
        porId(facturaProductoPK).ifPresent(fp -> {
            em.remove(fp);
        });
    }

    public Optional<FacturaProducto> porId(FacturaProductoPK facturaProductoPK) {
        return Optional.ofNullable(em.find(FacturaProducto.class, facturaProductoPK));
    }

    public List<FacturaProducto> Listar() {
        return em.createQuery("SELECT fp FROM FacturaProducto fp", FacturaProducto.class).getResultList();
    }

    public List<Producto> listarActivos() {
        return em.createQuery("SELECT p FROM Producto p WHERE p.estado = TRUE", Producto.class)
                .getResultList();
    }

}
