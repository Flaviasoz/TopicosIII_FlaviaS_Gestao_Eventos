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
        Integer usuarioId = getUsuarioLogadoId();
        if (usuarioId != null) {
            feedback.setUsuarioId(usuarioId);
        }
        modoEdicao = false;
    }

    public void limparFormulario() {
        feedback = new FeedbackEntity();
        modoEdicao = false;
    }

    public void prepararEditarFeedback(FeedbackEntity feedback) {
        try {
            if (feedback != null && feedback.getId() != null) {
                // Buscar o feedback diretamente do banco de dados para garantir dados atualizados
                this.feedback = em.find(FeedbackEntity.class, feedback.getId());
                if (this.feedback == null) {
                    // Se não encontrar no banco, usar o feedback passado como parâmetro
                    this.feedback = feedback;
                }
            } else {
                this.feedback = new FeedbackEntity();
            }
            modoEdicao = true;
        } catch (Exception e) {
            System.err.println("Erro ao preparar edição do feedback: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o feedback original
            this.feedback = feedback != null ? feedback : new FeedbackEntity();
            modoEdicao = true;
        }
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
            
            // Definir automaticamente o usuário logado
            Integer usuarioId = getUsuarioLogadoId();
            if (usuarioId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário não logado!"));
                return;
            }
            feedback.setUsuarioId(usuarioId);
            
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
            
            // Verificar se o usuário já enviou feedback para este evento
            if (!modoEdicao && jaEnviouFeedback(feedback.getEventoId(), usuarioId)) {
                FacesContext.getCurrentInstance().addMessage("mainForm:eventoId",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Você já enviou feedback para este evento!"));
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
        try {
            if (feedback != null && feedback.getId() != null) {
                // Buscar o feedback diretamente do banco de dados para garantir dados atualizados
                this.feedbackSelecionado = em.find(FeedbackEntity.class, feedback.getId());
                if (this.feedbackSelecionado == null) {
                    // Se não encontrar no banco, usar o feedback passado como parâmetro
                    this.feedbackSelecionado = feedback;
                }
            } else {
                this.feedbackSelecionado = new FeedbackEntity();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar detalhes do feedback: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o feedback original
            this.feedbackSelecionado = feedback != null ? feedback : new FeedbackEntity();
        }
    }

    public void limparFeedbackSelecionado() {
        this.feedbackSelecionado = new FeedbackEntity();
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

    /**
     * Lista apenas os feedbacks do usuário logado
     */
    public List<FeedbackEntity> listarFeedbacksUsuarioLogado() {
        Integer usuarioId = getUsuarioLogadoId();
        if (usuarioId != null) {
            return listarPorUsuario(usuarioId);
        }
        return new ArrayList<>();
    }

    /**
     * Lista eventos em que o usuário logado marcou presença
     */
    public List<EventosEntity> listarEventosComPresenca() {
        Integer usuarioId = getUsuarioLogadoId();
        if (usuarioId == null) {
            return new ArrayList<>();
        }
        
        try {
            TypedQuery<EventosEntity> query = em.createQuery(
                "SELECT DISTINCT e FROM EventosEntity e " +
                "INNER JOIN InscricoesEntity i ON e.id = i.eventoId " +
                "INNER JOIN ParticipantesEntity p ON i.id = p.inscricaoId " +
                "WHERE i.usuarioId = :usuarioId " +
                "AND p.presente = true " +
                "ORDER BY e.dataInicio DESC", 
                EventosEntity.class);
            
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar eventos com presença: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Obtém o ID do usuário logado
     */
    private Integer getUsuarioLogadoId() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null && facesContext.getExternalContext() != null) {
                jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) 
                    facesContext.getExternalContext().getSession(false);
                if (session != null) {
                    Object usuarioLogado = session.getAttribute("usuarioLogado");
                    if (usuarioLogado != null) {
                        return ((com.viniflavia.eventmanagement.entity.UsuarioEntity) usuarioLogado).getCodigo();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter usuário logado: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtém a lista de eventos em que o usuário marcou presença para o select
     */
    public List<EventosEntity> getListaEventosComPresenca() {
        return listarEventosComPresenca();
    }

    /**
     * Verifica se o usuário pode enviar feedback para um evento específico
     * (se participou do evento e ainda não enviou feedback)
     */
    public boolean podeEnviarFeedback(Integer eventoId) {
        Integer usuarioId = getUsuarioLogadoId();
        if (usuarioId == null || eventoId == null) {
            return false;
        }
        
        try {
            // Verificar se participou do evento
            TypedQuery<Long> queryPresenca = em.createQuery(
                "SELECT COUNT(p) FROM ParticipantesEntity p " +
                "INNER JOIN InscricoesEntity i ON p.inscricaoId = i.id " +
                "WHERE i.usuarioId = :usuarioId AND i.eventoId = :eventoId AND p.presente = true", 
                Long.class);
            queryPresenca.setParameter("usuarioId", usuarioId);
            queryPresenca.setParameter("eventoId", eventoId);
            
            boolean participou = queryPresenca.getSingleResult() > 0;
            
            // Verificar se já enviou feedback
            boolean jaEnviou = jaEnviouFeedback(eventoId, usuarioId);
            
            return participou && !jaEnviou;
        } catch (Exception e) {
            System.err.println("Erro ao verificar se pode enviar feedback: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista eventos em que o usuário pode enviar feedback
     * (participou do evento mas ainda não enviou feedback)
     */
    public List<EventosEntity> listarEventosParaFeedback() {
        Integer usuarioId = getUsuarioLogadoId();
        if (usuarioId == null) {
            return new ArrayList<>();
        }
        
        try {
            TypedQuery<EventosEntity> query = em.createQuery(
                "SELECT DISTINCT e FROM EventosEntity e " +
                "INNER JOIN InscricoesEntity i ON e.id = i.eventoId " +
                "INNER JOIN ParticipantesEntity p ON i.id = p.inscricaoId " +
                "WHERE i.usuarioId = :usuarioId " +
                "AND p.presente = true " +
                "AND e.id NOT IN (" +
                "    SELECT f.eventoId FROM FeedbackEntity f WHERE f.usuarioId = :usuarioId" +
                ") " +
                "ORDER BY e.dataInicio DESC", 
                EventosEntity.class);
            
            query.setParameter("usuarioId", usuarioId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar eventos para feedback: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Obtém a lista de eventos em que o usuário pode enviar feedback para o select
     */
    public List<EventosEntity> getListaEventosParaFeedback() {
        return listarEventosParaFeedback();
    }
    
    /**
     * Obtém a lista de eventos para edição (inclui o evento atual do feedback sendo editado)
     */
    public List<EventosEntity> getListaEventosParaEdicao() {
        List<EventosEntity> eventos = new ArrayList<>();
        
        try {
            // Se estamos editando, adicionar o evento atual do feedback
            if (modoEdicao && feedback != null && feedback.getEventoId() != null) {
                EventosEntity eventoAtual = em.find(EventosEntity.class, feedback.getEventoId());
                if (eventoAtual != null) {
                    eventos.add(eventoAtual);
                }
            }
            
            // Adicionar os eventos onde o usuário pode enviar feedback
            eventos.addAll(listarEventosParaFeedback());
            
            // Remover duplicatas baseado no ID do evento
            return eventos.stream()
                    .distinct()
                    .sorted((e1, e2) -> e1.getTitulo().compareToIgnoreCase(e2.getTitulo()))
                    .collect(java.util.stream.Collectors.toList());
                    
        } catch (Exception e) {
            System.err.println("Erro ao obter lista de eventos para edição: " + e.getMessage());
            return new ArrayList<>();
        }
    }
} 