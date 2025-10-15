package repositorios;

import entidades.Proveedor;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoProveedores {

    @Inject
    private EntityManager em;

    public void guardar(Proveedor proveedor) {
        if (proveedor.getIdProveedor() != null && proveedor.getIdProveedor() > 0) {
            em.merge(proveedor);
        } else {
            em.persist(proveedor);
        }
    }

    public void eliminar(Integer id) {
        porId(id).ifPresent(em::remove);
    }

    public Optional<Proveedor> porId(Integer id) {
        return Optional.ofNullable(em.find(Proveedor.class, id));
    }

    public List<Proveedor> listarTodos() {
        return em.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();
    }

    // --- Métodos de búsqueda específicos ---
    public Optional<Proveedor> porCuit(String cuit) {
        try {
            TypedQuery<Proveedor> query = em.createQuery("SELECT p FROM Proveedor p WHERE p.cuit = :cuit", Proveedor.class);
            query.setParameter("cuit", cuit);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Proveedor> porRazonSocial(String razonSocial) {
        TypedQuery<Proveedor> query = em.createQuery("SELECT p FROM Proveedor p WHERE p.razonSocial LIKE :razonSocial", Proveedor.class);
        query.setParameter("razonSocial", "%" + razonSocial + "%");
        return query.getResultList();
    }

    public List<Proveedor> porEstado(boolean activo) {
        TypedQuery<Proveedor> query = em.createQuery("SELECT p FROM Proveedor p WHERE p.estado = :estado", Proveedor.class);
        query.setParameter("estado", activo);
        return query.getResultList();
    }
}
