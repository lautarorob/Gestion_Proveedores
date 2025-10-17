package repositorios;

import entidades.OrdenPago;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoOrdenPago {

    @Inject
    private EntityManager em;

    public void guardar(OrdenPago ordenPago) {
        if (ordenPago.getIdOrdenPago() != null && ordenPago.getIdOrdenPago() > 0) {
            em.merge(ordenPago);
        } else {
            em.persist(ordenPago);
        }
    }

    public void eliminar(Integer id) {
        porId(id).ifPresent(em::remove);
    }

    public Optional<OrdenPago> porId(Integer id) {
        return Optional.ofNullable(em.find(OrdenPago.class, id));
    }

    public List<OrdenPago> listarTodas() {
        return em.createQuery("SELECT o FROM OrdenPago o", OrdenPago.class).getResultList();
    }
    
    // --- Métodos de búsqueda específicos ---

    /**
     * Busca una orden de pago por su número de orden, que debería ser único.
     * @param nroOrden El número de orden a buscar.
     * @return Un Optional que contiene la orden de pago si se encuentra.
     */
    public Optional<OrdenPago> porNumeroOrden(String nroOrden) {
        try {
            TypedQuery<OrdenPago> query = em.createQuery("SELECT o FROM OrdenPago o WHERE o.nroOrden = :nroOrden", OrdenPago.class);
            query.setParameter("nroOrden", nroOrden);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Busca todas las órdenes de pago asociadas a un proveedor.
     * @param idProveedor El ID del proveedor.
     * @return Una lista de órdenes de pago.
     */
    public List<OrdenPago> porProveedor(Integer idProveedor) {
        TypedQuery<OrdenPago> query = em.createQuery("SELECT o FROM OrdenPago o WHERE o.idProveedor.idProveedor = :idProveedor", OrdenPago.class);
        query.setParameter("idProveedor", idProveedor);
        return query.getResultList();
    }
    
    /**
     * Busca órdenes de pago dentro de un rango de fechas de pago.
     * @param fechaInicio La fecha inicial del rango.
     * @param fechaFin La fecha final del rango.
     * @return Una lista de órdenes de pago.
     */
    public List<OrdenPago> porRangoFechasPago(Date fechaInicio, Date fechaFin) {
        TypedQuery<OrdenPago> query = em.createQuery("SELECT o FROM OrdenPago o WHERE o.fechaPago BETWEEN :fechaInicio AND :fechaFin", OrdenPago.class);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }
}