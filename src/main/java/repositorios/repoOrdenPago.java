package repositorios;

import entidades.OrdenPago;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class repoOrdenPago {

    @Inject
    private EntityManager em;

    public void Guardar(OrdenPago ordenPago) {
        if (ordenPago.getIdOrdenPago() == null) {
            em.persist(ordenPago);
        } else {
            em.merge(ordenPago);
        }
    }

    public Optional<OrdenPago> porId(Integer id) {
        return Optional.ofNullable(em.find(OrdenPago.class, id));
    }

    public List<OrdenPago> Listar() {
        return em.createQuery("SELECT o FROM OrdenPago o ORDER BY o.fechaPago DESC", OrdenPago.class)
                .getResultList();
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