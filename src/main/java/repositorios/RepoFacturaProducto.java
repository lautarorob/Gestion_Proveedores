package repositorios;

import entidades.FacturaProducto;
import entidades.FacturaProductoPK;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoFacturaProducto {

    @Inject
    private EntityManager em;

    public void guardar(FacturaProducto facturaProducto) {
        // Para entidades con clave compuesta, merge es a menudo más seguro
        em.merge(facturaProducto);
    }

    public void eliminar(FacturaProductoPK pk) {
        porId(pk).ifPresent(em::remove);
    }

    public Optional<FacturaProducto> porId(FacturaProductoPK pk) {
        return Optional.ofNullable(em.find(FacturaProducto.class, pk));
    }

    // --- Métodos de búsqueda específicos ---

    public List<FacturaProducto> porFactura(Integer idFactura) {
        TypedQuery<FacturaProducto> query = em.createQuery("SELECT fp FROM FacturaProducto fp WHERE fp.facturaProductoPK.idFactura = :idFactura", FacturaProducto.class);
        query.setParameter("idFactura", idFactura);
        return query.getResultList();
    }
}