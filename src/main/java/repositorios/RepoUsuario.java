package repositorios;

import entidades.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class RepoUsuario {

    @Inject
    private EntityManager em;

    public void guardar(Usuario usuario) {
        if (usuario.getIdUsuario() != null && usuario.getIdUsuario() > 0) {
            em.merge(usuario);
        } else {
            em.persist(usuario);
        }
    }

    public void eliminar(Integer id) {
        porId(id).ifPresent(em::remove);
    }

    public Optional<Usuario> porId(Integer id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public List<Usuario> listarTodos() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    // --- Métodos de búsqueda específicos ---
    public Optional<Usuario> porEmail(String email) {
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Usuario> porRol(String rol) {
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.rol = :rol", Usuario.class);
        query.setParameter("rol", rol);
        return query.getResultList();
    }
}
