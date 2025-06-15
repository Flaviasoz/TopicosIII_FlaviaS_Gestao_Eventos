package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.FeedbackEntity;
import com.viniflavia.eventmanagement.entity.UsuarioEntity;
import com.viniflavia.eventmanagement.entity.EventosEntity;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Named(value = "feedbackController")
@SessionScoped
public class FeedbackController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private FeedbackEntity feedback = new FeedbackEntity();
    private FeedbackEntity feedbackSelecionado = new FeedbackEntity();
    private boolean modoEdicao = false;

    public FeedbackController() {
    }

    public FeedbackEntity getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackEntity feedback) {
        this.feedback = feedback;
    }

    public FeedbackEntity getFeedbackSelecionado() {
        return feedbackSelecionado;
    }

    public void setFeedbackSelecionado(FeedbackEntity feedbackSelecionado) {
        this.feedbackSelecionado = feedbackSelecionado;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<FeedbackEntity> listarTodos() {
        return em.createQuery("SELECT f FROM FeedbackEntity f ORDER BY f.dataComentario DESC", FeedbackEntity.class).getResultList();
    }

    public List<FeedbackEntity> listarTodosFeedbacks() {
        return listarTodos();
    }

    public List<FeedbackEntity> listarPorEvento(Integer eventoId) {
        return em.createQuery("SELECT f FROM FeedbackEntity f WHERE f.eventoId = :eventoId ORDER BY f.dataComentario DESC", FeedbackEntity.class)
                .setParameter("eventoId", eventoId)
                .getResultList();
    }

    public List<FeedbackEntity> listarPorUsuario(Integer usuarioId) {
        return em.createQuery("SELECT f FROM FeedbackEntity f WHERE f.usuarioId = :usuarioId ORDER BY f.dataComentario DESC", FeedbackEntity.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public void prepararNovoFeedback() {
        feedback = new FeedbackEntity();
        modoEdicao = false;
    }

    public void limparFormulario() {
        feedback = new FeedbackEntity();
        modoEdicao = false;
    }

    public void prepararEditarFeedback(FeedbackEntity feedback) {
        this.feedback = feedback;
        modoEdicao = true;
    }

    @Transactional
    public void enviarFeedback(Integer eventoId, Integer usuarioId, String comentario, Integer nota) {
        try {
            // Validações
            if (eventoId == null || eventoId <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do evento é obrigatório!"));
                return;
            }
            
            if (usuarioId == null || usuarioId <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do usuário é obrigatório!"));
                return;
            }
            
            if (comentario == null || comentario.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Comentário é obrigatório!"));
                return;
            }
            
            if (nota == null || nota < 1 || nota > 5) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nota deve estar entre 1 e 5!"));
                return;
            }
            
            // Verificar se o evento existe
            if (!eventoExiste(eventoId)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Evento com ID " + eventoId + " não encontrado!"));
                return;
            }
            
            // Verificar se o usuário existe
            if (!usuarioExiste(usuarioId)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário com ID " + usuarioId + " não encontrado!"));
                return;
            }
            
            feedback.setEventoId(eventoId);
            feedback.setUsuarioId(usuarioId);
            feedback.setComentario(comentario.trim());
            feedback.setNota(nota);
            feedback.setDataComentario(LocalDateTime.now());
            em.persist(feedback);
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Feedback enviado com sucesso!"));
            feedback = new FeedbackEntity();
        } catch (Exception e) {
            System.err.println("Erro ao enviar feedback: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao enviar feedback: " + e.getMessage()));
        }
    }

    @Transactional
    public void salvarFeedback() {
        try {
            // Validações
            if (feedback.getEventoId() == null || feedback.getEventoId() <= 0) {
                FacesContext.getCurrentInstance().addMessage("mainForm:eventoId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do evento é obrigatório e deve ser maior que zero!"));
                return;
            }
            
            if (feedback.getUsuarioId() == null || feedback.getUsuarioId() <= 0) {
                FacesContext.getCurrentInstance().addMessage("mainForm:usuarioId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do usuário é obrigatório e deve ser maior que zero!"));
                return;
            }
            
            if (feedback.getComentario() == null || feedback.getComentario().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("mainForm:comentario",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Comentário é obrigatório!"));
                return;
            }
            
            if (feedback.getNota() == null || feedback.getNota() < 1 || feedback.getNota() > 5) {
                FacesContext.getCurrentInstance().addMessage("mainForm:nota",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nota deve estar entre 1 e 5!"));
                return;
            }
            
            // Verificar se o evento existe
            if (!eventoExiste(feedback.getEventoId())) {
                FacesContext.getCurrentInstance().addMessage("mainForm:eventoId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Evento com ID " + feedback.getEventoId() + " não encontrado!"));
                return;
            }
            
            // Verificar se o usuário existe
            if (!usuarioExiste(feedback.getUsuarioId())) {
                FacesContext.getCurrentInstance().addMessage("mainForm:usuarioId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário com ID " + feedback.getUsuarioId() + " não encontrado!"));
                return;
            }
            
            if (!modoEdicao) {
                feedback.setDataComentario(LocalDateTime.now());
                em.persist(feedback);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Feedback criado com sucesso!"));
            } else {
                em.merge(feedback);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Feedback atualizado com sucesso!"));
            }
            
            // Limpar formulário
            feedback = new FeedbackEntity();
            modoEdicao = false;
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar feedback: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar feedback: " + e.getMessage()));
        }
    }

    @Transactional
    public void excluirFeedback(FeedbackEntity feedback) {
        try {
            em.remove(em.merge(feedback));
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Feedback excluído com sucesso!"));
        } catch (Exception e) {
            System.err.println("Erro ao excluir feedback: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir feedback: " + e.getMessage()));
        }
    }

    public FeedbackEntity buscarFeedbackPorId(Integer id) {
        return em.find(FeedbackEntity.class, id);
    }

    public void detalhesFeedback(FeedbackEntity feedback) {
        this.feedbackSelecionado = feedback;
    }

    public double calcularMediaNotasPorEvento(Integer eventoId) {
        try {
            TypedQuery<Double> query = em.createQuery(
                "SELECT AVG(f.nota) FROM FeedbackEntity f WHERE f.eventoId = :eventoId", 
                Double.class);
            query.setParameter("eventoId", eventoId);
            Double media = query.getSingleResult();
            return media != null ? media : 0.0;
        } catch (Exception e) {
            System.err.println("Erro ao calcular média de notas: " + e.getMessage());
            return 0.0;
        }
    }

    public int contarFeedbacksPorEvento(Integer eventoId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(f) FROM FeedbackEntity f WHERE f.eventoId = :eventoId", 
                Long.class);
            query.setParameter("eventoId", eventoId);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            System.err.println("Erro ao contar feedbacks: " + e.getMessage());
            return 0;
        }
    }

    public boolean jaEnviouFeedback(Integer eventoId, Integer usuarioId) {
        try {
            TypedQuery<FeedbackEntity> query = em.createQuery(
                "SELECT f FROM FeedbackEntity f WHERE f.eventoId = :eventoId AND f.usuarioId = :usuarioId", 
                FeedbackEntity.class);
            query.setParameter("eventoId", eventoId);
            query.setParameter("usuarioId", usuarioId);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            System.err.println("Erro ao verificar feedback existente: " + e.getMessage());
            return false;
        }
    }
    
    // Método para formatar data de comentário
    public String formatarDataComentario(LocalDateTime dataComentario) {
        if (dataComentario == null) {
            return "Data não definida";
        }
        try {
            return dataComentario.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return "Data inválida";
        }
    }
    
    // Método alternativo para formatar data usando String
    public String formatarDataComentarioString(Object dataComentario) {
        if (dataComentario == null) {
            return "Data não definida";
        }
        
        try {
            if (dataComentario instanceof LocalDateTime) {
                return ((LocalDateTime) dataComentario).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else if (dataComentario instanceof String) {
                // Se for string, tentar converter
                LocalDateTime dateTime = LocalDateTime.parse((String) dataComentario);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else {
                return dataComentario.toString();
            }
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return "Data inválida";
        }
    }
    
    // Método para validar se o evento existe
    public boolean eventoExiste(Integer eventoId) {
        if (eventoId == null) {
            return false;
        }
        try {
            Long count = (Long) em.createQuery("SELECT COUNT(e) FROM EventosEntity e WHERE e.id = :id")
                    .setParameter("id", eventoId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Método para validar se o usuário existe
    public boolean usuarioExiste(Integer usuarioId) {
        if (usuarioId == null) {
            return false;
        }
        try {
            Long count = (Long) em.createQuery("SELECT COUNT(u) FROM UsuarioEntity u WHERE u.codigo = :codigo")
                    .setParameter("codigo", usuarioId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Método para obter lista de usuários para o select
    public List<UsuarioEntity> getListaUsuarios() {
        try {
            return em.createQuery("SELECT u FROM UsuarioEntity u ORDER BY u.nome", UsuarioEntity.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obter lista de eventos para o select
    public List<EventosEntity> getListaEventos() {
        try {
            return em.createQuery("SELECT e FROM EventosEntity e ORDER BY e.titulo", EventosEntity.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao carregar eventos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para obter informações do evento
    public String getInfoEvento(Integer eventoId) {
        if (eventoId == null) {
            return "Evento não informado";
        }
        try {
            // Buscar o evento
            Object evento = em.createQuery("SELECT e FROM EventosEntity e WHERE e.id = :id")
                    .setParameter("id", eventoId)
                    .getResultList().stream().findFirst().orElse(null);
            
            if (evento != null) {
                return "Evento #" + eventoId;
            } else {
                return "Evento #" + eventoId + " (não encontrado)";
            }
        } catch (Exception e) {
            return "Evento #" + eventoId + " (erro)";
        }
    }
    
    // Método para obter nome do evento por ID
    public String getNomeEvento(Integer eventoId) {
        if (eventoId == null) {
            return "Evento não informado";
        }
        try {
            EventosEntity evento = em.find(EventosEntity.class, eventoId);
            if (evento != null) {
                return evento.getTitulo() + "(" + evento.getId() + ")";
            } else {
                return "Evento ID: " + eventoId + " (não encontrado)";
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar evento: " + e.getMessage());
            return "Evento ID: " + eventoId + " (erro)";
        }
    }
    
    // Método para obter informações do usuário
    public String getInfoUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return "Usuário não informado";
        }
        try {
            // Buscar o usuário
            Object usuario = em.createQuery("SELECT u FROM UsuarioEntity u WHERE u.codigo = :codigo")
                    .setParameter("codigo", usuarioId)
                    .getResultList().stream().findFirst().orElse(null);
            
            if (usuario != null) {
                return "Usuário #" + usuarioId;
            } else {
                return "Usuário #" + usuarioId + " (não encontrado)";
            }
        } catch (Exception e) {
            return "Usuário #" + usuarioId + " (erro)";
        }
    }
    
    // Método para obter nome do usuário por ID
    public String getNomeUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return "Usuário não informado";
        }
        try {
            UsuarioEntity usuario = em.find(UsuarioEntity.class, usuarioId);
            if (usuario != null) {
                return usuario.getNome() + "(" + usuario.getCodigo() + ")";
            } else {
                return "Usuário ID: " + usuarioId + " (não encontrado)";
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            return "Usuário ID: " + usuarioId + " (erro)";
        }
    }
} 