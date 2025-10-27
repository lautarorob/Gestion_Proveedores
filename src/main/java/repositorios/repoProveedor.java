/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Proveedor;
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
public class repoProveedor {

    @Inject
    EntityManager em;

    public void Guardar(Proveedor p) {
        if (p.getIdProveedor() != null && p.getIdProveedor() > 0) {
            em.merge(p);
        } else {
            em.persist(p);
        }
    }

    public void Eliminar(Integer id) {
        porId(id).ifPresent(p -> {
            em.remove(p);
        });
    }

    public Optional<Proveedor> porId(Integer id) {
        return Optional.ofNullable(em.find(Proveedor.class, id));
    }

    public List<Proveedor> Listar() {
        return em.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();
    }

}
