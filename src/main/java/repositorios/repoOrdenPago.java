package repositorios;

import entidades.OrdenPago;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class repoOrdenPago {

    @Inject
    private EntityManager em;

    public void Guardar(OrdenPago op) {
        if (op.getIdOrdenPago() != null && op.getIdOrdenPago() > 0) {
            em.merge(op);
        } else {
            em.persist(op);
        }
    }

    public Optional<OrdenPago> porId(Integer id) {
        return Optional.ofNullable(em.find(OrdenPago.class, id));
    }

    public List<OrdenPago> Listar() {
        return em.createQuery("SELECT o FROM OrdenPago o ORDER BY o.fechaPago DESC", OrdenPago.class)
                .getResultList();
    }

    public String obtenerUltimoNumeroOrden() {
        try {
            String jpql = "SELECT o.nroOrden FROM OrdenPago o ORDER BY o.idOrdenPago DESC";
            List<String> resultados = em.createQuery(jpql, String.class)
                    .setMaxResults(1)
                    .getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<OrdenPago> listarPorProveedor(Integer idProveedor) {
        try {
            return em.createQuery(
               "SELECT o FROM OrdenPago o WHERE o.idProveedor.idProveedor = :idProv ORDER BY o.fechaPago", 
                OrdenPago.class)
                .setParameter("idProv", idProveedor)
                .getResultList();
        } catch (Exception e) {
            System.out.println("Error al listar órdenes de pago por proveedor: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}