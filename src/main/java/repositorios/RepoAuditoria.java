package repositorios;

import entidades.Auditoria;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoAuditoria {

    @Inject
    private EntityManager em;

    /**
     * Guarda un nuevo registro de auditoría.Generalmente solo se deberían
 insertar (persist) y no actualizar (merge).
     * @param auditoria
     */
    public void guardar(Auditoria auditoria) {
        if (auditoria.getIdAuditoria() != null && auditoria.getIdAuditoria() > 0) {
            em.merge(auditoria); // No es una práctica común actualizar logs
        } else {
            em.persist(auditoria);
        }
    }

    /**
     * Eliminar registros de auditoría no es una práctica recomendada por
     * motivos de seguridad y trazabilidad.Usar con precaución.
     * @param id
     */
    public void eliminar(Integer id) {
        porId(id).ifPresent(em::remove);
    }

    public Optional<Auditoria> porId(Integer id) {
        return Optional.ofNullable(em.find(Auditoria.class, id));
    }

    public List<Auditoria> listarTodos() {
        return em.createQuery("SELECT a FROM Auditoria a ORDER BY a.fechaMovimiento DESC", Auditoria.class)
                 .getResultList();
    }

    // --- Métodos de búsqueda específicos ---

    /**
     * Busca todos los registros de auditoría generados por un usuario específico.
     * @param idUsuario El ID del usuario a buscar.
     * @return Una lista de registros de auditoría.
     */
    public List<Auditoria> porUsuario(Integer idUsuario) {
        TypedQuery<Auditoria> query = em.createQuery("SELECT a FROM Auditoria a WHERE a.idusuarioUltimo.idUsuario = :idUsuario ORDER BY a.fechaMovimiento DESC", Auditoria.class);
        query.setParameter("idUsuario", idUsuario);
        return query.getResultList();
    }

    /**
     * Busca todos los registros de auditoría dentro de un rango de fechas.
     * @param fechaInicio La fecha inicial del rango.
     * @param fechaFin La fecha final del rango.
     * @return Una lista de registros de auditoría.
     */
    public List<Auditoria> porRangoFechas(Date fechaInicio, Date fechaFin) {
        TypedQuery<Auditoria> query = em.createQuery("SELECT a FROM Auditoria a WHERE a.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaMovimiento DESC", Auditoria.class);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }
}