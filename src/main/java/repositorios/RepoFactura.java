package repositorios;

import entidades.Factura;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoFactura {

    @Inject
    private EntityManager em;

    public void guardar(Factura factura) {
        if (factura.getIdFactura() != null && factura.getIdFactura() > 0) {
            em.merge(factura);
        } else {
            em.persist(factura);
        }
    }

    public void eliminar(Integer id) {
        porId(id).ifPresent(em::remove);
    }

    public Optional<Factura> porId(Integer id) {
        return Optional.ofNullable(em.find(Factura.class, id));
    }

    public List<Factura> listarTodas() {
        return em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();
    }

    // --- Métodos de búsqueda específicos ---

    public List<Factura> porProveedor(Integer idProveedor) {
        TypedQuery<Factura> query = em.createQuery("SELECT f FROM Factura f WHERE f.proveedor.idProveedor = :idProveedor", Factura.class);
        query.setParameter("idProveedor", idProveedor);
        return query.getResultList();
    }

    public List<Factura> porEstado(String estado) {
        TypedQuery<Factura> query = em.createQuery("SELECT f FROM Factura f WHERE f.estado = :estado", Factura.class);
        query.setParameter("estado", estado);
        return query.getResultList();
    }
}