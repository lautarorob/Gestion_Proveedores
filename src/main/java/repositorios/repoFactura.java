/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Factura;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author roble
 */
@Stateless
public class repoFactura {
    @Inject
    EntityManager em;
    
     public void Guardar(Factura f) {
        if (f.getIdFactura() != null && f.getIdFactura() > 0) {
            em.merge(f);
        } else {
            em.persist(f);
        }
    }
/*
    public void BajaLogica(Integer id) {
        porId(id).ifPresent(f -> {
            f.setEstado(false);
            em.merge(f);
        });
    }
    
    public void Reactivar(Integer id) {
        porId(id).ifPresent(f -> {
            f.setEstado(true);
            em.merge(f);
        });
    }
*/
    public void Eliminar(Integer id) {
        porId(id).ifPresent(p -> {
            em.remove(p);
        });
    }

    public Optional<Factura> porId(Integer id) {
        return Optional.ofNullable(em.find(Factura.class, id));
    }

    public List<Factura> Listar() {
        return em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();
    }
    
}
