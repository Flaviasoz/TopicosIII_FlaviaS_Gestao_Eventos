package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.UsuarioEntity;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    private UsuarioEntity usuario = new UsuarioEntity();
    private boolean logado = false;

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public boolean isLogado() {
        return logado;
    }

    public String login() {
        try {
            TypedQuery<UsuarioEntity> query = em.createQuery(
                "SELECT u FROM UsuarioEntity u WHERE u.email = :email AND u.senha = :senha", UsuarioEntity.class);
            query.setParameter("email", usuario.getEmail());
            query.setParameter("senha", usuario.getSenha());

            List<UsuarioEntity> usuarios = query.getResultList();

            if (!usuarios.isEmpty()) {
                usuario = usuarios.get(0);
                logado = true;
                return "home?faces-redirect=true";
            } else {
                logado = false;
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login falhou", "Email ou senha inválidos."));
                return null;
            }
        } catch (Exception e) {
            logado = false;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao tentar logar."));
            return null;
        }
    }

    public String logout() {
        usuario = new UsuarioEntity();
        logado = false;
        return "login?faces-redirect=true";
    }
}
