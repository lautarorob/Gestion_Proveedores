/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Usuario;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import repositorios.repoUsuario;

/**
 *
 * @author roble
 */
@Named(value = "controladorUsuario")
@SessionScoped
public class controladorUsuario implements Serializable {

    @Inject
    private repoUsuario repoUsuario;

    private Usuario usuario;

    private Integer id;

    private String confirmPassword;// Propiedad para guardar el valor de "Repetir Contraseña"

    // permite "capturar" el componente de la primera contraseña
    // para poder leer su valor desde el validador.
    private UIInput passwordComponent;

    public controladorUsuario() {
    }

    @Model
    @Produces
    public Usuario usuario() {
        if (usuario != null) {
            return usuario; // Si ya hay un usuario en sesión, retornarlo
        }

        if (id != null && id > 0) {
            repoUsuario.porId(id).ifPresent(u -> {
                usuario = u;
            });
        } else {
            usuario = new Usuario();
        }
        return usuario;
    }

    /**
     * Este método es llamado por el atributo 'validator' del campo
     * confirmPassword. Compara los dos campos de contraseña.
     */
    public void validatePasswordConfirm(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return; // Valor vacío
        }

        String confirmPasswordValue = (String) value;

        //valor del *primer* campo de contraseña
        String passwordValue = (String) passwordComponent.getValue();

        if (passwordValue == null) {
            passwordValue = ""; // Evitar error si está vacío
        }

        // La validación
        if (!passwordValue.equals(confirmPasswordValue)) {
            // Si no coinciden, lanza una excepción. JSF la mostrará en el h:message
            throw new ValidatorException(new FacesMessage("Las contraseñas no coinciden."));
        }
    }

    public List<Usuario> listar() {
        return repoUsuario.Listar();
    }

    
    public String guardar() {

        // --- 1: Validar USERNAME (Login único) ---
        Optional<Usuario> userByUsername = repoUsuario.findByUsername(usuario.getUsername());
        if (userByUsername.isPresent()) {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            boolean esOtroUsuario = !userByUsername.get().getIdUsuario().equals(usuario.getIdUsuario());

            if (esNuevo || esOtroUsuario) {
                // Mensaje de error para el campo 'username'
                FacesContext.getCurrentInstance().addMessage("formulario:username",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "El nombre de usuario (login) '" + usuario.getUsername() + "' ya está en uso.",
                                "Login duplicado")
                );
                return null; // Nos quedamos en la página
            }
        }

        // --- 2: Validar NOMBRE + ROL ---
        Optional<Usuario> userByNombreRol = repoUsuario.findByNombreAndRol(
                usuario.getNombreCompleto(),
                usuario.getRol()
        );

        if (userByNombreRol.isPresent()) {
            boolean esNuevo = (usuario.getIdUsuario() == null);
            boolean esOtroUsuario = !userByNombreRol.get().getIdUsuario().equals(usuario.getIdUsuario());

            if (esNuevo || esOtroUsuario) {
                // Mensaje de error para el campo 'nombreCompleto'
                FacesContext.getCurrentInstance().addMessage("formulario:nombreCompleto", 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Ya existe un usuario con el nombre '" + usuario.getNombreCompleto() + "' y el rol '" + usuario.getRol() + "'.",
                                "Duplicado por Nombre y Rol")
                );
                return null;
            }
        }

        
        repoUsuario.Guardar(usuario);
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoUsuario.Eliminar(id);
        return "/usuarios/index.xhtml?faces-redirect=true";
    }

    public repoUsuario getRepoUsuario() {
        return repoUsuario;
    }

    public void setRepoUsuario(repoUsuario repoUsuario) {
        this.repoUsuario = repoUsuario;
    }

    public Usuario getUsuario() {
        if (usuario == null) {
            usuario();
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String login() {
    FacesContext context = FacesContext.getCurrentInstance();
    Usuario encontrado = repoUsuario.login(usuario.getUsername(), usuario.getPassword());

    if (encontrado != null) {
        this.usuario = encontrado;

        // REGISTRAR EL USUARIO ACTUAL EN MySQL PARA LOS TRIGGERS
        repoUsuario.setCurrentUserId(encontrado.getIdUsuario());

        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Bienvenido",
                "Bienvenido " + encontrado.getUsername()
        ));

        return "/index.xhtml?faces-redirect=true";
    } else {
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Usuario o contraseña incorrectos",
                "Error"
        ));
        return null;
    }
}


    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Getter y Setter para el bindeo
    public UIInput getPasswordComponent() {
        return passwordComponent;
    }

    public void setPasswordComponent(UIInput passwordComponent) {
        this.passwordComponent = passwordComponent;
    }

}
