package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.NotificacoesEntity;
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

@Named(value = "notificacoesUsuarioController")
@SessionScoped
public class NotificacoesUsuarioController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private List<NotificacoesEntity> notificacoesUsuario;
    private Integer usuarioLogadoId;

    public NotificacoesUsuarioController() {
    }

    public void inicializar() {
        // Obter o ID do usuário logado
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null && facesContext.getExternalContext() != null) {
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) 
                facesContext.getExternalContext().getSession(false);
            if (session != null) {
                Object usuarioLogado = session.getAttribute("usuarioLogado");
                if (usuarioLogado != null) {
                    this.usuarioLogadoId = ((com.viniflavia.eventmanagement.entity.UsuarioEntity) usuarioLogado).getCodigo();
                    carregarNotificacoes();
                }
            }
        }
    }

    public void carregarNotificacoes() {
        if (usuarioLogadoId != null) {
            try {
                TypedQuery<NotificacoesEntity> query = em.createQuery(
                    "SELECT n FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId ORDER BY n.enviadaEm DESC", 
                    NotificacoesEntity.class);
                query.setParameter("usuarioId", usuarioLogadoId);
                this.notificacoesUsuario = query.getResultList();
            } catch (Exception e) {
                System.err.println("Erro ao carregar notificações: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Método público para forçar a atualização das notificações
     * Pode ser chamado por outros controllers após ações que geram notificações
     */
    public void forcarAtualizacaoNotificacoes() {
        // Limpar a lista atual para forçar recarregamento
        this.notificacoesUsuario = null;
        // Recarregar as notificações
        carregarNotificacoes();
        System.out.println("Forçada atualização das notificações do usuário ID: " + usuarioLogadoId);
    }

    public List<NotificacoesEntity> getNotificacoesUsuario() {
        if (notificacoesUsuario == null) {
            inicializar();
        }
        return notificacoesUsuario;
    }

    public List<NotificacoesEntity> getNotificacoesNaoLidas() {
        if (usuarioLogadoId == null) {
            inicializar();
        }
        
        if (usuarioLogadoId != null) {
            try {
                TypedQuery<NotificacoesEntity> query = em.createQuery(
                    "SELECT n FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId AND n.lida = false ORDER BY n.enviadaEm DESC", 
                    NotificacoesEntity.class);
                query.setParameter("usuarioId", usuarioLogadoId);
                return query.getResultList();
            } catch (Exception e) {
                System.err.println("Erro ao carregar notificações não lidas: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public int getQuantidadeNotificacoesNaoLidas() {
        if (usuarioLogadoId == null) {
            inicializar();
        }
        
        if (usuarioLogadoId != null) {
            try {
                TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(n) FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId AND n.lida = false", 
                    Long.class);
                query.setParameter("usuarioId", usuarioLogadoId);
                return query.getSingleResult().intValue();
            } catch (Exception e) {
                System.err.println("Erro ao contar notificações não lidas: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Transactional
    public void marcarComoLida(NotificacoesEntity notificacao) {
        try {
            notificacao.setLida(true);
            em.merge(notificacao);
            carregarNotificacoes(); // Recarregar a lista
        } catch (Exception e) {
            System.err.println("Erro ao marcar notificação como lida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void marcarTodasComoLidas() {
        if (usuarioLogadoId != null) {
            try {
                TypedQuery<NotificacoesEntity> query = em.createQuery(
                    "SELECT n FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId AND n.lida = false", 
                    NotificacoesEntity.class);
                query.setParameter("usuarioId", usuarioLogadoId);
                List<NotificacoesEntity> naoLidas = query.getResultList();
                
                for (NotificacoesEntity notificacao : naoLidas) {
                    notificacao.setLida(true);
                    em.merge(notificacao);
                }
                
                carregarNotificacoes(); // Recarregar a lista
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Todas as notificações foram marcadas como lidas!"));
            } catch (Exception e) {
                System.err.println("Erro ao marcar todas as notificações como lidas: " + e.getMessage());
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao marcar notificações como lidas!"));
            }
        }
    }

    public String formatarDataEnvio(LocalDateTime dataEnvio) {
        if (dataEnvio == null) {
            return "Data não definida";
        }
        try {
            return dataEnvio.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return "Data inválida";
        }
    }

    public String formatarDataEnvioString(Object dataEnvio) {
        if (dataEnvio == null) {
            return "Data não definida";
        }
        
        try {
            if (dataEnvio instanceof LocalDateTime) {
                return ((LocalDateTime) dataEnvio).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else if (dataEnvio instanceof String) {
                LocalDateTime dateTime = LocalDateTime.parse((String) dataEnvio);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else {
                return dataEnvio.toString();
            }
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return "Data inválida";
        }
    }

    public String getTempoRelativo(LocalDateTime dataEnvio) {
        if (dataEnvio == null) {
            return "Data não definida";
        }
        
        try {
            LocalDateTime agora = LocalDateTime.now();
            java.time.Duration duracao = java.time.Duration.between(dataEnvio, agora);
            
            long minutos = duracao.toMinutes();
            long horas = duracao.toHours();
            long dias = duracao.toDays();
            
            if (minutos < 1) {
                return "Agora mesmo";
            } else if (minutos < 60) {
                return minutos + " min atrás";
            } else if (horas < 24) {
                return horas + "h atrás";
            } else if (dias < 7) {
                return dias + " dias atrás";
            } else {
                return formatarDataEnvio(dataEnvio);
            }
        } catch (Exception e) {
            return formatarDataEnvio(dataEnvio);
        }
    }
} 