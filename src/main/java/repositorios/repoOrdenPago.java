package repositorios;

import entidades.OrdenPago;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
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

    public List<OrdenPago> findByFechaPagoBetween(Date fechaInicio, Date fechaFin) {
        try {
            String jpql = "SELECT o FROM OrdenPago o "
                    + "WHERE o.fechaPago BETWEEN :inicio AND :fin "
                    + "ORDER BY o.idProveedor.razonSocial, o.formaPago, o.fechaPago";
            return em.createQuery(jpql, OrdenPago.class)
                    .setParameter("inicio", fechaInicio)
                    .setParameter("fin", fechaFin)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("Error al listar pagos por período: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<OrdenPago> buscarConFiltros(
            Integer proveedorId,
            String formaPago,
            Date fechaInicio,
            Date fechaFin) {

        String jpql = "SELECT o FROM OrdenPago o WHERE 1=1";

        if (proveedorId != null) {
            jpql += " AND o.idProveedor.idProveedor = :proveedorId";
        }
        if (formaPago != null && !formaPago.isEmpty()) {
            jpql += " AND o.formaPago = :formaPago";
        }
        if (fechaInicio != null) {
            jpql += " AND o.fechaPago >= :fechaInicio";
        }
        if (fechaFin != null) {
            jpql += " AND o.fechaPago <= :fechaFin";
        }

        TypedQuery<OrdenPago> query = em.createQuery(jpql, OrdenPago.class);

        if (proveedorId != null) {
            query.setParameter("proveedorId", proveedorId);
        }
        if (formaPago != null && !formaPago.isEmpty()) {
            query.setParameter("formaPago", formaPago);
        }
        if (fechaInicio != null) {
            query.setParameter("fechaInicio", fechaInicio);
        }
        if (fechaFin != null) {
            query.setParameter("fechaFin", fechaFin);
        }

        return query.getResultList();
    }

}
