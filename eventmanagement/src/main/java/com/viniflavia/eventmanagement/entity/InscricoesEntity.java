package com.viniflavia.eventmanagement.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscricoes")
public class InscricoesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "evento_id", nullable = false)
    private Integer eventoId;

    @Column(name = "data_inscricao")
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
