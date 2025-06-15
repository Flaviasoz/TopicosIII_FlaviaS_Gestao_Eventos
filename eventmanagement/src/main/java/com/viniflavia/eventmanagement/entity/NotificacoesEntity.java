package com.viniflavia.eventmanagement.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class NotificacoesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "titulo", length = 200)
    private String titulo;

    @Column(name = "mensagem", columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "enviada_em")
    private LocalDateTime enviadaEm;

    @Column(name = "lida")
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
