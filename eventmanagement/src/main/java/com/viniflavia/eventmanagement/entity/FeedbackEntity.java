package com.viniflavia.eventmanagement.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class FeedbackEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "evento_id", nullable = false)
    private Integer eventoId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "comentario", nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "nota")
    private Integer nota;

    @Column(name = "data_comentario")
    private LocalDateTime dataComentario;

    public FeedbackEntity() {
    }

    public FeedbackEntity(Integer id, Integer eventoId, Integer usuarioId, String comentario, Integer nota, LocalDateTime dataComentario) {
        this.id = id;
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.comentario = comentario;
        this.nota = nota;
        this.dataComentario = dataComentario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEventoId() {
        return eventoId;
    }

    public void setEventoId(Integer eventoId) {
        this.eventoId = eventoId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public LocalDateTime getDataComentario() {
        return dataComentario;
    }

    public void setDataComentario(LocalDateTime dataComentario) {
        this.dataComentario = dataComentario;
    }
}
