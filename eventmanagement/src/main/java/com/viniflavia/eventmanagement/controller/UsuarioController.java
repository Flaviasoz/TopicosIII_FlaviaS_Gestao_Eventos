package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.UsuarioEntity;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author vinic
 */
@Named(value = "usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    private UsuarioEntity usuario = new UsuarioEntity();

    public UsuarioController() {
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }
}
