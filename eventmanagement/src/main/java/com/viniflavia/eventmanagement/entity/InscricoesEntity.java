package com.viniflavia.eventmanagement.entity;

import java.time.LocalDateTime;

public class InscricoesEntity {
    private Integer id;
    private Integer usuarioId;
    private Integer eventoId;
    private LocalDateTime dataInscricao;

    public InscricoesEntity() {
    }

    public InscricoesEntity(Integer id, Integer usuarioId, Integer eventoId, LocalDateTime dataInscricao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.dataInscricao = dataInscricao;
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

    public Integer getEventoId() {
        return eventoId;
    }

    public void setEventoId(Integer eventoId) {
        this.eventoId = eventoId;
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }
}
