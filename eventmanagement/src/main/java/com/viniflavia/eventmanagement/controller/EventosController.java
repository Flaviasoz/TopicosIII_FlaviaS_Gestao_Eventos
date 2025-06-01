package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.EventosEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Stateless
public class EventosController {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    public List<EventosEntity> listarTodosEventos() {
        return em.createNamedQuery("Evento.findAll", EventosEntity.class).getResultList();
    }

    public List<EventosEntity> listarEventosPorUsuario(Long usuarioId) {
        return em.createNamedQuery("Evento.findByUsuario", EventosEntity.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public void criarEvento(EventosEntity evento) {
        evento.setDataCriacao(new Date());
        em.persist(evento);
    }
}
