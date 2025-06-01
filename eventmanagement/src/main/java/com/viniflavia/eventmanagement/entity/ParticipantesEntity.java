package com.viniflavia.eventmanagement.entity;

import java.time.LocalDateTime;

public class ParticipantesEntity {
    private Integer id;
    private Integer inscricaoId;
    private Boolean presente;
    private LocalDateTime dataPresenca;

    public ParticipantesEntity() {
    }

    public ParticipantesEntity(Integer id, Integer inscricaoId, Boolean presente, LocalDateTime dataPresenca) {
        this.id = id;
        this.inscricaoId = inscricaoId;
        this.presente = presente;
        this.dataPresenca = dataPresenca;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInscricaoId() {
        return inscricaoId;
    }

    public void setInscricaoId(Integer inscricaoId) {
        this.inscricaoId = inscricaoId;
    }

    public Boolean getPresente() {
        return presente;
    }

    public void setPresente(Boolean presente) {
        this.presente = presente;
    }

    public LocalDateTime getDataPresenca() {
        return dataPresenca;
    }

    public void setDataPresenca(LocalDateTime dataPresenca) {
        this.dataPresenca = dataPresenca;
    }
}
