/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorios;

import entidades.Usuario;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author roble
 */
public class repoUsuario {

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
    
    
    public Usuario login(String userName, String password){
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password", 
                Usuario.class);
            query.setParameter("username", userName);
            query.setParameter("password", password);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        
    }

}
