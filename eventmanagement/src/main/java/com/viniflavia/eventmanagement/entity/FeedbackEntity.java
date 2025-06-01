package com.viniflavia.eventmanagement.entity;

import java.time.LocalDateTime;

public class FeedbackEntity {
    private Integer id;
    private Integer eventoId;
    private Integer usuarioId;
    private String comentario;
    private Integer nota;
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
