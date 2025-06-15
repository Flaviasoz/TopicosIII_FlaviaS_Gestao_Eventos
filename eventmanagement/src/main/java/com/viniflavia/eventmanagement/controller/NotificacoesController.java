package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.NotificacoesEntity;
import com.viniflavia.eventmanagement.entity.UsuarioEntity;
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

@Named(value = "notificacoesController")
@SessionScoped
public class NotificacoesController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private NotificacoesEntity notificacao = new NotificacoesEntity();
    private NotificacoesEntity notificacaoSelecionada = new NotificacoesEntity();
    private boolean modoEdicao = false;

    public NotificacoesController() {
    }

    public NotificacoesEntity getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(NotificacoesEntity notificacao) {
        this.notificacao = notificacao;
    }

    public NotificacoesEntity getNotificacaoSelecionada() {
        return notificacaoSelecionada;
    }

    public void setNotificacaoSelecionada(NotificacoesEntity notificacaoSelecionada) {
        this.notificacaoSelecionada = notificacaoSelecionada;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<NotificacoesEntity> listarTodos() {
        return em.createQuery("SELECT n FROM NotificacoesEntity n ORDER BY n.enviadaEm DESC", NotificacoesEntity.class).getResultList();
    }

    public List<NotificacoesEntity> listarTodasNotificacoes() {
        return listarTodos();
    }

    public List<NotificacoesEntity> listarPorUsuario(Integer usuarioId) {
        return em.createQuery("SELECT n FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId ORDER BY n.enviadaEm DESC", NotificacoesEntity.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public List<NotificacoesEntity> listarNaoLidas(Integer usuarioId) {
        return em.createQuery("SELECT n FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId AND n.lida = false ORDER BY n.enviadaEm DESC", NotificacoesEntity.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public void prepararNovaNotificacao() {
        notificacao = new NotificacoesEntity();
        notificacao.setLida(false); // Por padrão, não lida
        modoEdicao = false;
    }

    public void limparFormulario() {
        notificacao = new NotificacoesEntity();
        notificacao.setLida(false); // Por padrão, não lida
        modoEdicao = false;
    }

    public void prepararEditarNotificacao(NotificacoesEntity notificacao) {
        this.notificacao = notificacao;
        modoEdicao = true;
    }

    @Transactional
    public void enviarNotificacao(Integer usuarioId, String titulo, String mensagem) {
        try {
            // Validações
            if (usuarioId == null || usuarioId <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do usuário é obrigatório!"));
                return;
            }
            
            if (titulo == null || titulo.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Título é obrigatório!"));
                return;
            }
            
            if (mensagem == null || mensagem.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Mensagem é obrigatória!"));
                return;
            }
            
            // Verificar se o usuário existe
            if (!usuarioExiste(usuarioId)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário com ID " + usuarioId + " não encontrado!"));
                return;
            }
            
            notificacao.setUsuarioId(usuarioId);
            notificacao.setTitulo(titulo.trim());
            notificacao.setMensagem(mensagem.trim());
            notificacao.setEnviadaEm(LocalDateTime.now());
            notificacao.setLida(false);
            em.persist(notificacao);
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Notificação enviada com sucesso!"));
            notificacao = new NotificacoesEntity();
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao enviar notificação: " + e.getMessage()));
        }
    }

    @Transactional
    public void salvarNotificacao() {
        try {
            // Validações
            if (notificacao.getUsuarioId() == null || notificacao.getUsuarioId() <= 0) {
                FacesContext.getCurrentInstance().addMessage("mainForm:usuarioId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID do usuário é obrigatório e deve ser maior que zero!"));
                return;
            }
            
            if (notificacao.getTitulo() == null || notificacao.getTitulo().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("mainForm:titulo",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Título é obrigatório!"));
                return;
            }
            
            if (notificacao.getMensagem() == null || notificacao.getMensagem().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("mainForm:mensagem",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Mensagem é obrigatória!"));
                return;
            }
            
            // Verificar se o usuário existe
            if (!usuarioExiste(notificacao.getUsuarioId())) {
                FacesContext.getCurrentInstance().addMessage("mainForm:usuarioId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário com ID " + notificacao.getUsuarioId() + " não encontrado!"));
                return;
            }
            
            // Definir valores padrão se não estiverem definidos
            if (notificacao.getLida() == null) {
                notificacao.setLida(false); // Por padrão, não lida
            }
            
            if (!modoEdicao) {
                notificacao.setEnviadaEm(LocalDateTime.now());
                em.persist(notificacao);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Notificação criada com sucesso!"));
            } else {
                em.merge(notificacao);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Notificação atualizada com sucesso!"));
            }
            
            // Limpar formulário
            notificacao = new NotificacoesEntity();
            modoEdicao = false;
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar notificação: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar notificação: " + e.getMessage()));
        }
    }

    @Transactional
    public void marcarComoLida(NotificacoesEntity notificacao) {
        try {
            notificacao.setLida(true);
            em.merge(notificacao);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Notificação marcada como lida!"));
        } catch (Exception e) {
            System.err.println("Erro ao marcar notificação como lida: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao marcar notificação: " + e.getMessage()));
        }
    }

    @Transactional
    public void excluirNotificacao(NotificacoesEntity notificacao) {
        try {
            em.remove(em.merge(notificacao));
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Notificação excluída com sucesso!"));
        } catch (Exception e) {
            System.err.println("Erro ao excluir notificação: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir notificação: " + e.getMessage()));
        }
    }

    public NotificacoesEntity buscarNotificacaoPorId(Integer id) {
        return em.find(NotificacoesEntity.class, id);
    }

    public void detalhesNotificacao(NotificacoesEntity notificacao) {
        this.notificacaoSelecionada = notificacao;
        if (!notificacao.getLida()) {
            marcarComoLida(notificacao);
        }
    }

    public int contarNotificacoesNaoLidas(Integer usuarioId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(n) FROM NotificacoesEntity n WHERE n.usuarioId = :usuarioId AND n.lida = false", 
                Long.class);
            query.setParameter("usuarioId", usuarioId);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            System.err.println("Erro ao contar notificações não lidas: " + e.getMessage());
            return 0;
        }
    }
    
    // Método para formatar data de envio
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
    
    // Método alternativo para formatar data usando String
    public String formatarDataEnvioString(Object dataEnvio) {
        if (dataEnvio == null) {
            return "Data não definida";
        }
        
        try {
            if (dataEnvio instanceof LocalDateTime) {
                return ((LocalDateTime) dataEnvio).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else if (dataEnvio instanceof String) {
                // Se for string, tentar converter
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
    
    // Método para obter lista de usuários para o select
    public List<UsuarioEntity> getListaUsuarios() {
        try {
            return em.createQuery("SELECT u FROM UsuarioEntity u ORDER BY u.nome", UsuarioEntity.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
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