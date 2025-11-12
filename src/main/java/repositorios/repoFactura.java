/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Factura;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public String obtenerUltimoComprobante() {
        try {
            String jpql = "SELECT f.nroComprobante FROM Factura f ORDER BY f.idFactura DESC";
            List<String> resultados = em.createQuery(jpql, String.class)
                    .setMaxResults(1)
                    .getResultList();

            if (resultados != null && !resultados.isEmpty()) {
                return resultados.get(0);
            }
            return null; // No hay facturas previas
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Factura> obtenerFacturaConProductos(Integer id) {
        try {
            List<Factura> resultados = em.createQuery(
                    "SELECT f FROM Factura f LEFT JOIN FETCH f.facturaProductoList fp LEFT JOIN FETCH fp.producto WHERE f.idFactura = :id",
                    Factura.class)
                    .setParameter("id", id)
                    .getResultList(); // no usar getSingleResult

            if (!resultados.isEmpty()) {
                return Optional.of(resultados.get(0));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            System.out.println("Error cargando factura con productos: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Factura> listarPorProveedor(Integer idProveedor) {
        try {
            return em.createQuery(
                    "SELECT f FROM Factura f WHERE f.idProveedor.idProveedor = :idProv ORDER BY f.fechaComprobante",
                    Factura.class)
                    .setParameter("idProv", idProveedor)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error al listar facturas por proveedor: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    
    public BigDecimal getSaldoPendiente(Integer idProveedor) {
        TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT SUM(f.total) FROM Factura f WHERE f.idProveedor.idProveedor = :id AND f.estado = 'Pendiente'",
                BigDecimal.class
        );
        query.setParameter("id", idProveedor);
        BigDecimal saldo = query.getSingleResult();
        return (saldo != null) ? saldo : BigDecimal.ZERO;
    }

}
