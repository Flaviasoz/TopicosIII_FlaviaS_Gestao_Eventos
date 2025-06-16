package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.ParticipantesEntity;
import com.viniflavia.eventmanagement.entity.InscricoesEntity;
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

@Named(value = "participantesController")
@SessionScoped
public class ParticipantesController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private ParticipantesEntity participante = new ParticipantesEntity();
    private ParticipantesEntity participanteSelecionado = new ParticipantesEntity();
    private boolean modoEdicao = false;

    public ParticipantesController() {
    }

    public ParticipantesEntity getParticipante() {
        return participante;
    }

    public void setParticipante(ParticipantesEntity participante) {
        this.participante = participante;
    }

    public ParticipantesEntity getParticipanteSelecionado() {
        return participanteSelecionado;
    }

    public void setParticipanteSelecionado(ParticipantesEntity participanteSelecionado) {
        this.participanteSelecionado = participanteSelecionado;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<ParticipantesEntity> listarTodos() {
        return em.createQuery("SELECT p FROM ParticipantesEntity p ORDER BY p.dataPresenca DESC", ParticipantesEntity.class).getResultList();
    }

    public List<ParticipantesEntity> listarTodosParticipantes() {
        return listarTodos();
    }

    public ParticipantesEntity buscarPorInscricao(Integer inscricaoId) {
        return em.createQuery("SELECT p FROM ParticipantesEntity p WHERE p.inscricaoId = :inscricaoId", ParticipantesEntity.class)
                .setParameter("inscricaoId", inscricaoId)
                .getResultList().stream().findFirst().orElse(null);
    }

    public List<ParticipantesEntity> listarPresentes() {
        return em.createQuery("SELECT p FROM ParticipantesEntity p WHERE p.presente = true ORDER BY p.dataPresenca DESC", ParticipantesEntity.class).getResultList();
    }

    public void prepararNovoParticipante() {
        participante = new ParticipantesEntity();
        participante.setPresente(true); // Por padrão, presente
        modoEdicao = false;
    }

    public void limparFormulario() {
        participante = new ParticipantesEntity();
        participante.setPresente(true); // Por padrão, presente
        modoEdicao = false;
    }

    public void prepararEditarParticipante(ParticipantesEntity participante) {
        try {
            if (participante != null && participante.getId() != null) {
                // Buscar o participante diretamente do banco de dados para garantir dados atualizados
                this.participante = em.find(ParticipantesEntity.class, participante.getId());
                if (this.participante == null) {
                    // Se não encontrar no banco, usar o participante passado como parâmetro
                    this.participante = participante;
                }
            } else {
                this.participante = new ParticipantesEntity();
            }
            modoEdicao = true;
        } catch (Exception e) {
            System.err.println("Erro ao preparar edição do participante: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o participante original
            this.participante = participante != null ? participante : new ParticipantesEntity();
            modoEdicao = true;
        }
    }

    @Transactional
    public void registrarPresenca(Integer inscricaoId) {
        try {
            // Verificar se já existe participante para esta inscrição
            TypedQuery<ParticipantesEntity> query = em.createQuery(
                "SELECT p FROM ParticipantesEntity p WHERE p.inscricaoId = :inscricaoId", 
                ParticipantesEntity.class);
            query.setParameter("inscricaoId", inscricaoId);
            
            List<ParticipantesEntity> participantes = query.getResultList();
            
            if (participantes.isEmpty()) {
                participante.setInscricaoId(inscricaoId);
                participante.setPresente(true);
                participante.setDataPresenca(LocalDateTime.now());
                em.persist(participante);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença registrada com sucesso!"));
            } else {
                ParticipantesEntity participanteExistente = participantes.get(0);
                participanteExistente.setPresente(true);
                participanteExistente.setDataPresenca(LocalDateTime.now());
                em.merge(participanteExistente);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença atualizada com sucesso!"));
            }
            participante = new ParticipantesEntity();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao registrar presença: " + e.getMessage()));
        }
    }

    @Transactional
    public void salvarParticipante() {
        try {
            // Validações
            if (participante.getInscricaoId() == null || participante.getInscricaoId() <= 0) {
                FacesContext.getCurrentInstance().addMessage("mainForm:inscricaoId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "ID da inscrição é obrigatório e deve ser maior que zero!"));
                return;
            }
            
            // Verificar se a inscrição existe
            if (!inscricaoExiste(participante.getInscricaoId())) {
                FacesContext.getCurrentInstance().addMessage("mainForm:inscricaoId",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Inscrição com ID " + participante.getInscricaoId() + " não encontrada!"));
                return;
            }
            
            // Definir valores padrão se não estiverem definidos
            if (participante.getPresente() == null) {
                participante.setPresente(true); // Por padrão, presente
            }
            
            if (participante.getDataPresenca() == null) {
                participante.setDataPresenca(LocalDateTime.now()); // Data atual
            }
            
            if (!modoEdicao) {
                // Verificar se já existe participante para esta inscrição
                ParticipantesEntity participanteExistente = buscarPorInscricao(participante.getInscricaoId());
                if (participanteExistente != null) {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Já existe um registro para esta inscrição. Atualizando..."));
                    participante.setId(participanteExistente.getId());
                    modoEdicao = true;
                }
                
                em.persist(participante);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença registrada com sucesso!"));
            } else {
                em.merge(participante);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença atualizada com sucesso!"));
            }
            
            // Limpar formulário
            participante = new ParticipantesEntity();
            modoEdicao = false;
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar participante: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar participante: " + e.getMessage()));
        }
    }

    @Transactional
    public void excluirParticipante(ParticipantesEntity participante) {
        try {
            em.remove(em.merge(participante));
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante excluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir participante: " + e.getMessage()));
        }
    }

    public ParticipantesEntity buscarParticipantePorId(Integer id) {
        return em.find(ParticipantesEntity.class, id);
    }

    public void detalhesParticipante(ParticipantesEntity participante) {
        try {
            if (participante != null && participante.getId() != null) {
                // Buscar o participante diretamente do banco de dados para garantir dados atualizados
                this.participanteSelecionado = em.find(ParticipantesEntity.class, participante.getId());
                if (this.participanteSelecionado == null) {
                    // Se não encontrar no banco, usar o participante passado como parâmetro
                    this.participanteSelecionado = participante;
                }
            } else {
                this.participanteSelecionado = new ParticipantesEntity();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar detalhes do participante: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o participante original
            this.participanteSelecionado = participante != null ? participante : new ParticipantesEntity();
        }
    }

    public void limparParticipanteSelecionado() {
        this.participanteSelecionado = new ParticipantesEntity();
    }

    public boolean isPresente(Integer inscricaoId) {
        TypedQuery<ParticipantesEntity> query = em.createQuery(
            "SELECT p FROM ParticipantesEntity p WHERE p.inscricaoId = :inscricaoId AND p.presente = true", 
            ParticipantesEntity.class);
        query.setParameter("inscricaoId", inscricaoId);
        return !query.getResultList().isEmpty();
    }

    /**
     * Verifica se o usuário está presente em um evento específico
     */
    public boolean isPresenteNoEvento(Integer eventoId, Integer usuarioId) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM ParticipantesEntity p " +
                "INNER JOIN InscricoesEntity i ON p.inscricaoId = i.id " +
                "WHERE i.eventoId = :eventoId AND i.usuarioId = :usuarioId AND p.presente = true", 
                Long.class);
            query.setParameter("eventoId", eventoId);
            query.setParameter("usuarioId", usuarioId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao verificar presença no evento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra presença do usuário em um evento específico
     */
    @Transactional
    public void registrarPresencaNoEvento(Integer eventoId, Integer usuarioId) {
        try {
            // Buscar a inscrição do usuário no evento
            TypedQuery<InscricoesEntity> queryInscricao = em.createQuery(
                "SELECT i FROM InscricoesEntity i WHERE i.eventoId = :eventoId AND i.usuarioId = :usuarioId", 
                InscricoesEntity.class);
            queryInscricao.setParameter("eventoId", eventoId);
            queryInscricao.setParameter("usuarioId", usuarioId);
            
            List<InscricoesEntity> inscricoes = queryInscricao.getResultList();
            
            if (inscricoes.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Você precisa estar inscrito no evento para registrar presença!"));
                return;
            }
            
            InscricoesEntity inscricao = inscricoes.get(0);
            
            // Verificar se já existe participante para esta inscrição
            TypedQuery<ParticipantesEntity> queryParticipante = em.createQuery(
                "SELECT p FROM ParticipantesEntity p WHERE p.inscricaoId = :inscricaoId", 
                ParticipantesEntity.class);
            queryParticipante.setParameter("inscricaoId", inscricao.getId());
            
            List<ParticipantesEntity> participantes = queryParticipante.getResultList();
            
            if (participantes.isEmpty()) {
                participante.setInscricaoId(inscricao.getId());
                participante.setPresente(true);
                participante.setDataPresenca(LocalDateTime.now());
                em.persist(participante);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença registrada com sucesso!"));
            } else {
                ParticipantesEntity participanteExistente = participantes.get(0);
                participanteExistente.setPresente(true);
                participanteExistente.setDataPresenca(LocalDateTime.now());
                em.merge(participanteExistente);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presença atualizada com sucesso!"));
            }
            participante = new ParticipantesEntity();
        } catch (Exception e) {
            System.err.println("Erro ao registrar presença no evento: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao registrar presença: " + e.getMessage()));
        }
    }

    // Método para obter informações da inscrição
    public String getInfoInscricao(Integer inscricaoId) {
        if (inscricaoId == null) {
            return "Inscrição não informada";
        }
        try {
            // Buscar a inscrição
            Object inscricao = em.createQuery("SELECT i FROM InscricoesEntity i WHERE i.id = :id")
                    .setParameter("id", inscricaoId)
                    .getResultList().stream().findFirst().orElse(null);
            
            if (inscricao != null) {
                return "Inscrição #" + inscricaoId;
            } else {
                return "Inscrição #" + inscricaoId + " (não encontrada)";
            }
        } catch (Exception e) {
            return "Inscrição #" + inscricaoId + " (erro)";
        }
    }
    
    // Método para formatar data de presença
    public String formatarDataPresenca(LocalDateTime dataPresenca) {
        if (dataPresenca == null) {
            return "Não registrada";
        }
        try {
            return dataPresenca.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return "Data inválida";
        }
    }
    
    // Método alternativo para formatar data usando String
    public String formatarDataPresencaString(Object dataPresenca) {
        if (dataPresenca == null) {
            return "Não registrada";
        }
        
        try {
            if (dataPresenca instanceof LocalDateTime) {
                return ((LocalDateTime) dataPresenca).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else if (dataPresenca instanceof String) {
                // Se for string, tentar converter
                LocalDateTime dateTime = LocalDateTime.parse((String) dataPresenca);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } else {
                return dataPresenca.toString();
            }
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return "Data inválida";
        }
    }
    
    // Método para validar se a inscrição existe
    public boolean inscricaoExiste(Integer inscricaoId) {
        if (inscricaoId == null) {
            return false;
        }
        try {
            Long count = (Long) em.createQuery("SELECT COUNT(i) FROM InscricoesEntity i WHERE i.id = :id")
                    .setParameter("id", inscricaoId)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Método para obter lista de inscrições para o select
    public List<InscricoesEntity> getListaInscricoes() {
        try {
            return em.createQuery("SELECT i FROM InscricoesEntity i ORDER BY i.dataInscricao DESC", InscricoesEntity.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao carregar inscrições: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Método para formatar a inscrição para exibição no select
    public String formatarInscricaoParaSelect(InscricoesEntity inscricao) {
        if (inscricao == null) {
            return "Inscrição inválida";
        }
        
        try {
            // Buscar informações do usuário
            String nomeUsuario = "Usuário não encontrado";
            try {
                Object usuario = em.createQuery("SELECT u FROM UsuarioEntity u WHERE u.codigo = :codigo")
                        .setParameter("codigo", inscricao.getUsuarioId())
                        .getResultList().stream().findFirst().orElse(null);
                if (usuario != null) {
                    nomeUsuario = ((com.viniflavia.eventmanagement.entity.UsuarioEntity) usuario).getNome();
                }
            } catch (Exception e) {
                System.err.println("Erro ao buscar usuário: " + e.getMessage());
            }
            
            // Buscar informações do evento
            String tituloEvento = "Evento não encontrado";
            try {
                Object evento = em.createQuery("SELECT e FROM EventosEntity e WHERE e.id = :id")
                        .setParameter("id", inscricao.getEventoId())
                        .getResultList().stream().findFirst().orElse(null);
                if (evento != null) {
                    tituloEvento = ((com.viniflavia.eventmanagement.entity.EventosEntity) evento).getTitulo();
                }
            } catch (Exception e) {
                System.err.println("Erro ao buscar evento: " + e.getMessage());
            }
            
            return "ID: " + inscricao.getId() + " - " + nomeUsuario + " em " + tituloEvento;
        } catch (Exception e) {
            return "Inscrição ID: " + inscricao.getId();
        }
    }
} 