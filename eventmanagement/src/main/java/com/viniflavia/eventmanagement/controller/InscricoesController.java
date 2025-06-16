package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.InscricoesEntity;
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

@Named(value = "inscricoesController")
@SessionScoped
public class InscricoesController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private InscricoesEntity inscricao = new InscricoesEntity();
    private InscricoesEntity inscricaoSelecionada = new InscricoesEntity();
    private boolean modoEdicao = false;
    private List<EventosEntity> eventosInscritosCache = null;
    private Integer usuarioCacheId = null;

    public InscricoesController() {
    }

    // Método de inicialização
    public void inicializar() {
        try {
            System.out.println("=== INICIALIZANDO INSCRICOES CONTROLLER ===");
            
            // Limpar cache para garantir dados frescos
            limparCacheEventosInscritos();
            
            // Verificar se o usuário está logado
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) facesContext.getExternalContext().getSession(false);
            
            if (session == null) {
                System.err.println("Erro: Sessão não encontrada na inicialização do InscricoesController");
                return;
            }
            
            com.viniflavia.eventmanagement.entity.UsuarioEntity usuarioLogado = 
                (com.viniflavia.eventmanagement.entity.UsuarioEntity) session.getAttribute("usuarioLogado");
            
            if (usuarioLogado == null) {
                System.err.println("Erro: Usuário não logado na inicialização do InscricoesController");
                return;
            }
            
            System.out.println("Usuário logado na inicialização: " + usuarioLogado.getNome() + " (ID: " + usuarioLogado.getCodigo() + ")");
            
            // Listar eventos inscritos para debug
            List<EventosEntity> eventos = listarEventosInscritos(usuarioLogado.getCodigo());
            System.out.println("Total de eventos inscritos carregados: " + eventos.size());
            
            System.out.println("=== FIM DA INICIALIZAÇÃO DO INSCRICOES CONTROLLER ===");
            
        } catch (Exception e) {
            System.err.println("Erro na inicialização do InscricoesController: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
        }
    }

    public InscricoesEntity getInscricao() {
        return inscricao;
    }

    public void setInscricao(InscricoesEntity inscricao) {
        this.inscricao = inscricao;
    }

    public InscricoesEntity getInscricaoSelecionada() {
        return inscricaoSelecionada;
    }

    public void setInscricaoSelecionada(InscricoesEntity inscricaoSelecionada) {
        this.inscricaoSelecionada = inscricaoSelecionada;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<InscricoesEntity> listarTodos() {
        return em.createQuery("SELECT i FROM InscricoesEntity i ORDER BY i.dataInscricao DESC", InscricoesEntity.class).getResultList();
    }

    public List<InscricoesEntity> listarTodasInscricoes() {
        try {
            List<InscricoesEntity> inscricoes = em.createQuery("SELECT i FROM InscricoesEntity i ORDER BY i.dataInscricao DESC", InscricoesEntity.class).getResultList();
            System.out.println("Total de inscrições encontradas: " + inscricoes.size());
            for (InscricoesEntity inscricao : inscricoes) {
                System.out.println("Inscrição ID: " + inscricao.getId() + 
                                 ", Usuário: " + inscricao.getUsuarioId() + 
                                 ", Evento: " + inscricao.getEventoId() + 
                                 ", Data: " + inscricao.getDataInscricao());
            }
            return inscricoes;
        } catch (Exception e) {
            System.err.println("Erro ao listar inscrições: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<InscricoesEntity> listarPorUsuario(Integer usuarioId) {
        return em.createQuery("SELECT i FROM InscricoesEntity i WHERE i.usuarioId = :usuarioId ORDER BY i.dataInscricao DESC", InscricoesEntity.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public List<InscricoesEntity> listarPorEvento(Integer eventoId) {
        return em.createQuery("SELECT i FROM InscricoesEntity i WHERE i.eventoId = :eventoId ORDER BY i.dataInscricao ASC", InscricoesEntity.class)
                .setParameter("eventoId", eventoId)
                .getResultList();
    }

    public void prepararNovaInscricao() {
        inscricao = new InscricoesEntity();
        inscricao.setId(null);
        inscricao.setUsuarioId(null);
        inscricao.setEventoId(null);
        inscricao.setDataInscricao(null);
        modoEdicao = false;
        
        System.out.println("Nova inscrição - campos limpos");
    }

    @Transactional
    public void prepararEditarInscricao(InscricoesEntity inscricao) {
        // Criar uma nova instância para evitar problemas de referência
        this.inscricao = new InscricoesEntity();
        this.inscricao.setId(inscricao.getId());
        this.inscricao.setUsuarioId(inscricao.getUsuarioId());
        this.inscricao.setEventoId(inscricao.getEventoId());
        this.inscricao.setDataInscricao(inscricao.getDataInscricao());
        modoEdicao = true;
        
        System.out.println("Editando inscrição - ID: " + this.inscricao.getId() + 
                          ", Usuário: " + this.inscricao.getUsuarioId() + 
                          ", Evento: " + this.inscricao.getEventoId());
    }

    @Transactional
    public void inscreverEmEvento(Integer eventoId, Integer usuarioId) {
        try {
            // Verificar se já está inscrito
            TypedQuery<InscricoesEntity> query = em.createQuery(
                "SELECT i FROM InscricoesEntity i WHERE i.eventoId = :eventoId AND i.usuarioId = :usuarioId", 
                InscricoesEntity.class);
            query.setParameter("eventoId", eventoId);
            query.setParameter("usuarioId", usuarioId);
            
            if (query.getResultList().isEmpty()) {
                inscricao.setEventoId(eventoId);
                inscricao.setUsuarioId(usuarioId);
                inscricao.setDataInscricao(LocalDateTime.now());
                em.persist(inscricao);
                em.flush();
                
                // Atualizar notificações do usuário
                atualizarNotificacoesUsuario();
                
                // Limpar cache após nova inscrição
                limparCacheEventosInscritos();
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Inscrição realizada com sucesso!"));
            } else {                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", 
                        "Você já está inscrito neste evento!"));
            }
            inscricao = new InscricoesEntity();
        } catch (Exception e) {
            System.err.println("Erro ao inscrever em evento: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao realizar inscrição: " + e.getMessage()));
        }
    }

    @Transactional
    public void salvarInscricao() {
        try {
            System.out.println("Tentando salvar inscrição - Usuário: " + inscricao.getUsuarioId() + 
                              ", Evento: " + inscricao.getEventoId() + 
                              ", Modo Edição: " + modoEdicao);
            
            if (inscricao.getUsuarioId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Por favor, selecione um usuário."));
                return;
            }
            
            if (inscricao.getEventoId() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Por favor, selecione um evento."));
                return;
            }
            
            if (!modoEdicao) {
                TypedQuery<InscricoesEntity> query = em.createQuery(
                    "SELECT i FROM InscricoesEntity i WHERE i.usuarioId = :usuarioId AND i.eventoId = :eventoId", 
                    InscricoesEntity.class);
                query.setParameter("usuarioId", inscricao.getUsuarioId());
                query.setParameter("eventoId", inscricao.getEventoId());
                
                List<InscricoesEntity> inscricoesExistentes = query.getResultList();
                
                if (!inscricoesExistentes.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", 
                            "Este usuário já está inscrito neste evento!"));
                    return;
                }
                
                inscricao.setDataInscricao(LocalDateTime.now());
                em.persist(inscricao);
                em.flush();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Inscrição criada com sucesso!"));
            } else {
                TypedQuery<InscricoesEntity> query = em.createQuery(
                    "SELECT i FROM InscricoesEntity i WHERE i.usuarioId = :usuarioId AND i.eventoId = :eventoId AND i.id != :inscricaoId", 
                    InscricoesEntity.class);
                query.setParameter("usuarioId", inscricao.getUsuarioId());
                query.setParameter("eventoId", inscricao.getEventoId());
                query.setParameter("inscricaoId", inscricao.getId());
                
                List<InscricoesEntity> inscricoesExistentes = query.getResultList();
                
                if (!inscricoesExistentes.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", 
                            "Já existe outra inscrição para este usuário e evento!"));
                    return;
                }
                
                em.merge(inscricao);
                em.flush();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Inscrição atualizada com sucesso!"));
            }
            inscricao = new InscricoesEntity();
            modoEdicao = false;
        } catch (Exception e) {
            System.err.println("Erro detalhado ao salvar inscrição:");
            e.printStackTrace();
            
            String errorMessage = "Erro ao salvar inscrição: ";
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                errorMessage += e.getMessage();
            } else if (e.getCause() != null && e.getCause().getMessage() != null) {
                errorMessage += e.getCause().getMessage();
            } else {
                errorMessage += "Erro desconhecido - verifique os logs do servidor";
            }
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", errorMessage));
        }
    }

    @Transactional
    public void cancelarInscricao(InscricoesEntity inscricao) {
        try {
            em.remove(em.merge(inscricao));
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Inscrição cancelada com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao cancelar inscrição: " + e.getMessage()));
        }
    }

    @Transactional
    public void cancelarInscricao(Integer eventoId) {
        try {
            System.out.println("Cancelando inscrição para evento ID: " + eventoId);
            
            // Buscar o usuário logado
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) facesContext.getExternalContext().getSession(false);
            
            if (session == null) {
                System.err.println("Erro: Sessão não encontrada ao cancelar inscrição");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Sessão não encontrada"));
                return;
            }
            
            com.viniflavia.eventmanagement.entity.UsuarioEntity usuarioLogado = 
                (com.viniflavia.eventmanagement.entity.UsuarioEntity) session.getAttribute("usuarioLogado");
            
            if (usuarioLogado == null) {
                System.err.println("Erro: Usuário não logado ao cancelar inscrição");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário não logado"));
                return;
            }
            
            // Buscar a inscrição
            TypedQuery<InscricoesEntity> query = em.createQuery(
                "SELECT i FROM InscricoesEntity i WHERE i.eventoId = :eventoId AND i.usuarioId = :usuarioId", 
                InscricoesEntity.class);
            query.setParameter("eventoId", eventoId);
            query.setParameter("usuarioId", usuarioLogado.getCodigo());
            
            List<InscricoesEntity> inscricoes = query.getResultList();
            
            if (inscricoes.isEmpty()) {
                System.err.println("Inscrição não encontrada para evento ID: " + eventoId + " e usuário ID: " + usuarioLogado.getCodigo());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Inscrição não encontrada"));
                return;
            }
            
            InscricoesEntity inscricao = inscricoes.get(0);
            em.remove(em.merge(inscricao));
            em.flush();
            
            // Atualizar notificações do usuário
            atualizarNotificacoesUsuario();
            
            // Limpar cache após cancelar inscrição
            limparCacheEventosInscritos();
            
            System.out.println("Inscrição cancelada com sucesso - ID: " + inscricao.getId());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Inscrição cancelada com sucesso!"));
                
        } catch (Exception e) {
            System.err.println("Erro ao cancelar inscrição: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao cancelar inscrição: " + e.getMessage()));
        }
    }

    public InscricoesEntity buscarInscricaoPorId(Integer id) {
        return em.find(InscricoesEntity.class, id);
    }

    public void detalhesInscricao(InscricoesEntity inscricao) {
        this.inscricaoSelecionada = inscricao;
    }

    public boolean isInscrito(Integer eventoId, Integer usuarioId) {
        TypedQuery<InscricoesEntity> query = em.createQuery(
            "SELECT i FROM InscricoesEntity i WHERE i.eventoId = :eventoId AND i.usuarioId = :usuarioId", 
            InscricoesEntity.class);
        query.setParameter("eventoId", eventoId);
        query.setParameter("usuarioId", usuarioId);
        return !query.getResultList().isEmpty();
    }

    // Método de teste para verificar se o EntityManager está funcionando
    public String testarEntityManager() {
        try {
            // Teste simples para verificar se o EntityManager está funcionando
            em.createQuery("SELECT COUNT(i) FROM InscricoesEntity i", Long.class).getSingleResult();
            return "EntityManager funcionando corretamente";
        } catch (Exception e) {
            System.err.println("Erro no EntityManager: " + e.getMessage());
            e.printStackTrace();
            return "Erro no EntityManager: " + e.getMessage();
        }
    }
    
    // Método para formatar data de inscrição
    public String formatarDataInscricao(LocalDateTime dataInscricao) {
        if (dataInscricao == null) {
            return "Data não informada";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return dataInscricao.format(formatter);
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return dataInscricao.toString();
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
    
    // Método para obter nome do usuário por ID
    public String getNomeUsuario(Integer usuarioId) {
        if (usuarioId == null) {
            return "Usuário não informado";
        }
        try {
            UsuarioEntity usuario = em.find(UsuarioEntity.class, usuarioId);
            if (usuario != null) {
                return usuario.getNome() + " (" + usuario.getEmail() + ")";
            } else {
                return "Usuário ID: " + usuarioId + " (não encontrado)";
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            return "Usuário ID: " + usuarioId + " (erro)";
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
                return evento.getTitulo() + " - " + (evento.getLocal() != null ? evento.getLocal() : "Local não informado");
            } else {
                return "Evento ID: " + eventoId + " (não encontrado)";
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar evento: " + e.getMessage());
            return "Evento ID: " + eventoId + " (erro)";
        }
    }
    
    // Método auxiliar para verificar se uma inscrição já existe
    public boolean inscricaoExiste(Integer usuarioId, Integer eventoId, Integer inscricaoIdExcluir) {
        try {
            String jpql = "SELECT i FROM InscricoesEntity i WHERE i.usuarioId = :usuarioId AND i.eventoId = :eventoId";
            TypedQuery<InscricoesEntity> query = em.createQuery(jpql, InscricoesEntity.class);
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("eventoId", eventoId);
            
            if (inscricaoIdExcluir != null) {
                jpql += " AND i.id != :inscricaoId";
                query = em.createQuery(jpql, InscricoesEntity.class);
                query.setParameter("usuarioId", usuarioId);
                query.setParameter("eventoId", eventoId);
                query.setParameter("inscricaoId", inscricaoIdExcluir);
            }
            
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            System.err.println("Erro ao verificar inscrição existente: " + e.getMessage());
            return false;
        }
    }

    // Método para listar eventos em que o usuário está inscrito
    public List<EventosEntity> listarEventosInscritos(Integer usuarioId) {
        try {
            System.out.println("=== BUSCANDO EVENTOS INSCRITOS ===");
            System.out.println("Usuário ID: " + usuarioId);
            
            if (usuarioId == null) {
                System.err.println("Erro: ID do usuário é nulo");
                return new ArrayList<>();
            }
            
            // Verificar se já temos cache para este usuário
            if (eventosInscritosCache != null && usuarioCacheId != null && usuarioCacheId.equals(usuarioId)) {
                System.out.println("Usando cache - Eventos encontrados: " + eventosInscritosCache.size());
                return eventosInscritosCache;
            }
            
            // Buscar os eventos futuros em que está inscrito
            TypedQuery<EventosEntity> query = em.createQuery(
                "SELECT e FROM EventosEntity e " +
                "INNER JOIN InscricoesEntity i ON e.id = i.eventoId " +
                "WHERE i.usuarioId = :usuarioId " +
                "AND e.dataInicio >= :dataAtual " +
                "ORDER BY e.dataInicio ASC", 
                EventosEntity.class);
            
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("dataAtual", java.time.LocalDateTime.now());
            
            List<EventosEntity> eventos = query.getResultList();
            System.out.println("Eventos futuros inscritos encontrados: " + eventos.size());
            
            // Log detalhado dos eventos encontrados
            for (EventosEntity evento : eventos) {
                System.out.println("  - Evento: " + evento.getTitulo() + 
                                 " (ID: " + evento.getId() + 
                                 ", Data: " + evento.getDataInicio() + 
                                 ", Local: " + evento.getLocal() + ")");
            }
            
            // Atualizar cache
            eventosInscritosCache = eventos;
            usuarioCacheId = usuarioId;
            
            System.out.println("=== FIM DA BUSCA DE EVENTOS INSCRITOS ===");
            return eventos;
            
        } catch (Exception e) {
            System.err.println("Erro ao buscar eventos inscritos: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Método para listar todos os eventos em que o usuário está inscrito (incluindo passados)
    public List<EventosEntity> listarTodosEventosInscritos(Integer usuarioId) {
        try {
            System.out.println("Buscando todos os eventos inscritos para usuário ID: " + usuarioId);
            
            TypedQuery<EventosEntity> query = em.createQuery(
                "SELECT e FROM EventosEntity e " +
                "INNER JOIN InscricoesEntity i ON e.id = i.eventoId " +
                "WHERE i.usuarioId = :usuarioId " +
                "ORDER BY e.dataInicio DESC", 
                EventosEntity.class);
            
            query.setParameter("usuarioId", usuarioId);
            
            List<EventosEntity> eventos = query.getResultList();
            System.out.println("Encontrados " + eventos.size() + " eventos inscritos (todos) para o usuário");
            
            return eventos;
        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os eventos inscritos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Método auxiliar para verificar se o usuário tem eventos inscritos
    public boolean temEventosInscritos(Integer usuarioId) {
        try {
            if (usuarioId == null) {
                return false;
            }
            
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(e) FROM EventosEntity e " +
                "INNER JOIN InscricoesEntity i ON e.id = i.eventoId " +
                "WHERE i.usuarioId = :usuarioId " +
                "AND e.dataInicio >= :dataAtual", 
                Long.class);
            
            query.setParameter("usuarioId", usuarioId);
            query.setParameter("dataAtual", java.time.LocalDateTime.now());
            
            Long count = query.getSingleResult();
            System.out.println("Verificação de eventos inscritos - Usuário ID: " + usuarioId + ", Count: " + count);
            
            return count > 0;
        } catch (Exception e) {
            System.err.println("Erro ao verificar eventos inscritos: " + e.getMessage());
            return false;
        }
    }

    // Método para limpar o cache de eventos inscritos
    public void limparCacheEventosInscritos() {
        eventosInscritosCache = null;
        usuarioCacheId = null;
        System.out.println("Cache de eventos inscritos limpo");
    }
    
    /**
     * Método utilitário para atualizar as notificações do usuário logado
     * após ações que geram notificações (inscrição/cancelamento)
     */
    private void atualizarNotificacoesUsuario() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                jakarta.el.ELContext elContext = facesContext.getELContext();
                Object bean = facesContext.getApplication().getELResolver()
                    .getValue(elContext, null, "notificacoesUsuarioController");
                if (bean != null && bean instanceof com.viniflavia.eventmanagement.controller.NotificacoesUsuarioController) {
                    ((com.viniflavia.eventmanagement.controller.NotificacoesUsuarioController) bean).forcarAtualizacaoNotificacoes();
                    System.out.println("Notificações do usuário atualizadas com sucesso");
                } else {
                    System.err.println("Bean notificacoesUsuarioController não encontrado");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar notificações do usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 