package repositorios;

import entidades.Producto;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoProducto {

    @Inject
    private EntityManager em;

    public void guardar(Producto producto) {
        if (producto.getIdProducto() != null && producto.getIdProducto() > 0) {
            em.merge(producto);
        } else {
            em.persist(producto);
        }
    }

    public void eliminar(Integer id) {
        porId(id).ifPresent(em::remove);
    }

    public Optional<Producto> porId(Integer id) {
        return Optional.ofNullable(em.find(Producto.class, id));
    }

    public List<Producto> listarTodos() {
        return em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
    }

    // --- Métodos de búsqueda específicos ---
    public Optional<Producto> porCodigo(String codProd) {
        try {
            TypedQuery<Producto> query = em.createQuery("SELECT p FROM Producto p WHERE p.codProd = :codProd", Producto.class);
            query.setParameter("codProd", codProd);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Producto> porProveedor(Integer idProveedor) {
        TypedQuery<Producto> query = em.createQuery("SELECT p FROM Producto p WHERE p.proveedor.idProveedor = :idProveedor", Producto.class);
        query.setParameter("idProveedor", idProveedor);
        return query.getResultList();
    }
}
