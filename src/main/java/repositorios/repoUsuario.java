/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Producto;
import entidades.Proveedor;
import entidades.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author roble
 */
@Stateless
public class repoUsuario implements Serializable {

    @Inject
    EntityManager em;

    public void Guardar(Usuario u) {
        if (u.getIdUsuario() != null && u.getIdUsuario() > 0) {
            em.merge(u);
        } else {
            em.persist(u);
        }
    }

    public void Eliminar(Integer id) {
        porId(id).ifPresent(u -> {
            em.remove(u);
        });
    }

    public Optional<Usuario> porId(Integer id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public List<Usuario> Listar() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    public Usuario login(String username, String password) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password AND u.estado = true",
                    Usuario.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<Usuario> findByUsername(String username) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :username",
                    Usuario.class);

            query.setParameter("username", username);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario",
                    Usuario.class);

            query.setParameter("idUsuario", id);

            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Usuario> findByNombreAndRol(String nombreCompleto, String rol) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombreCompleto = :nombreCompleto AND u.rol = :rol",
                    Usuario.class);
            query.setParameter("nombreCompleto", nombreCompleto);
            query.setParameter("rol", rol);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void setCurrentUserId(Integer userId) {
        em.createNativeQuery("SET @app_user_id = " + userId)
                .executeUpdate();
    }

    public List<Usuario> listarActivos() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.estado = true", Usuario.class)
                .getResultList();
    }

    @Transactional
    public void BajaLogica(Integer id) {
        em.createQuery("UPDATE Usuario u SET u.estado = false WHERE u.idUsuario = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Transactional
    public void ActivarLogica(Integer id) {
        em.createQuery("UPDATE Usuario u SET u.estado = true WHERE u.idUsuario = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

}
