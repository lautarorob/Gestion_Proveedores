/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auditoriaContext;

/**
 *
 * @author roble
 */
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@Stateless
public class auditoriaContext {

    @Inject
    private EntityManager em;

    public void setUsuarioActual(Integer idUsuario) {
        if (idUsuario == null) {
            return;
        }

        // Construir SQL válido para MySQL
        String sql = "SET @app_user_id = " + idUsuario;

        em.createNativeQuery(sql).executeUpdate();
    }
}
