/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package controladores;

import entidades.Usuario;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Model;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import java.util.List;
import repositorios.repoUsuario;

/**
 *
 * @author roble
 */
@Named(value = "controladorUsuario")
@RequestScoped
public class controladorUsuario {

    @Inject
    private repoUsuario repoUsuario;

    private Usuario usuario;

    private Integer id;

    public controladorUsuario() {
    }

    @Model
    @Produces
    public Usuario usuario() {
        System.out.println("id" + id);
        if (id != null && id > 0) {
            repoUsuario.porId(id).ifPresent(u -> {
                usuario = u;
            });
        } else {
            usuario = new Usuario();
        }
        return usuario;
    }

    public List<Usuario> listar() {
        return repoUsuario.Listar();
    }

    public String guardar() {
        repoUsuario.Guardar(usuario);
        return "/index.xhtml?faces-redirect=true";
    }

    public String eliminar(Integer id) {
        repoUsuario.Eliminar(id);
        return "/index.xhtml?faces-redirect=true";
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
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Bienvenido",
                    "Bienvenido " + encontrado.getUsername()));
            return "/index.xhtml?faces-redirect=true";
        } else {
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Usuario o contraseña incorrectos ",
                    "Error"));
            return null;
        }
    }

}
