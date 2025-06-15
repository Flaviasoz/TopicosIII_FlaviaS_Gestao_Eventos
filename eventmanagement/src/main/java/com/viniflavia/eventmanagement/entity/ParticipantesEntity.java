package com.viniflavia.eventmanagement.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "participantes")
public class ParticipantesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "inscricao_id", nullable = false)
    private Integer inscricaoId;

    @Column(name = "presente")
    private Boolean presente;

    @Column(name = "data_presenca")
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
