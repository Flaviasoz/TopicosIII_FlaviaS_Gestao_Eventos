package com.viniflavia.eventmanagement.entity;

import java.time.LocalDateTime;

public class NotificacoesEntity {
    private Integer id;
    private Integer usuarioId;
    private String titulo;
    private String mensagem;
    private LocalDateTime enviadaEm;
    private Boolean lida;

    public NotificacoesEntity() {
    }

    public NotificacoesEntity(Integer id, Integer usuarioId, String titulo, String mensagem, LocalDateTime enviadaEm, Boolean lida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.enviadaEm = enviadaEm;
        this.lida = lida;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getEnviadaEm() {
        return enviadaEm;
    }

    public void setEnviadaEm(LocalDateTime enviadaEm) {
        this.enviadaEm = enviadaEm;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }
}
