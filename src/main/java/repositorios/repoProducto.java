/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Producto;
import entidades.Proveedor;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author roble
 */
@Stateless
public class repoProducto {

    @Inject
    EntityManager em;

    public String Guardar(Producto pr) {
        try {
            if (pr.getIdProducto() != null && pr.getIdProducto() > 0) {
                em.merge(pr);
            } else {
                em.persist(pr);
            }
            em.flush(); // Forzar la operación para capturar excepciones
            return "OK";
        } catch (Exception e) {
            System.out.println("Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            return "ERROR";
        }
    }

    public void BajaLogica(Integer id) {
        porId(id).ifPresent(pr -> {
            pr.setEstado(false);
            em.merge(pr);
        });
    }

    public void Reactivar(Integer id) {
        porId(id).ifPresent(pr -> {
            pr.setEstado(true);
            em.merge(pr);
        });
    }

    public void Eliminar(Integer id) {
        porId(id).ifPresent(pr -> {
            em.remove(pr);
        });
    }

    public Optional<Producto> porId(Integer id) {
        return Optional.ofNullable(em.find(Producto.class, id));
    }

    public List<Producto> Listar() {
        return em.createQuery("SELECT pr FROM Producto pr", Producto.class).getResultList();
    }

    public List<Producto> buscarPorProveedor(Integer idProveedor) {
        try {
            String jpql = "SELECT p FROM Producto p "
                    + "WHERE p.idProveedor.idProveedor = :idProveedor "
                    + "AND p.estado = true "
                    + // Solo productos activos
                    "ORDER BY p.nombre";

            return em.createQuery(jpql, Producto.class)
                    .setParameter("idProveedor", idProveedor)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error al buscar productos activos por proveedor: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Proveedor> obtenerTodosProveedores() {
        return em.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();
    }

    public List<Proveedor> listarActivos() {
        return em.createQuery("SELECT p FROM Proveedor p WHERE p.estado = TRUE", Proveedor.class)
                .getResultList();
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return em.createQuery("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)", Producto.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    public List<Producto> buscarPorProveedorYNombre(Integer idProveedor, String nombre) {
        return em.createQuery("SELECT p FROM Producto p WHERE p.idProveedor.idProveedor = :idProveedor AND LOWER(p.nombre) LIKE LOWER(:nombre)", Producto.class)
                .setParameter("idProveedor", idProveedor)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

  
    public boolean existeCodProd(String codProd, Integer idProductoActual) {
        try {
            String jpql = "SELECT COUNT(p) FROM Producto p WHERE p.codProd = :codigo";

            if (idProductoActual != null) {
                jpql += " AND p.idProducto != :idActual";
            }

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("codigo", codProd);

            if (idProductoActual != null) {
                query.setParameter("idActual", idProductoActual);
            }

            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.out.println("Error al verificar código duplicado: " + e.getMessage());
            return false;
        }
    }

}
