package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.EventosEntity;
import com.viniflavia.eventmanagement.entity.UsuarioEntity;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Named(value = "eventoController")
@SessionScoped
public class EventosController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private EventosEntity evento = new EventosEntity();
    private EventosEntity eventoSelecionado = null;
    private boolean modoEdicao = false;

    public EventosController() {
    }

    public EventosEntity getEvento() {
        return evento;
    }

    public void setEvento(EventosEntity evento) {
        this.evento = evento;
    }

    public EventosEntity getEventoSelecionado() {
        return eventoSelecionado;
    }

    public void setEventoSelecionado(EventosEntity eventoSelecionado) {
        this.eventoSelecionado = eventoSelecionado;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<EventosEntity> listarTodosEventos() {
        try {
            List<EventosEntity> eventos = em.createQuery("SELECT e FROM EventosEntity e ORDER BY e.dataInicio DESC", EventosEntity.class).getResultList();
            System.out.println("Total de eventos encontrados: " + eventos.size());
            return eventos;
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<EventosEntity> listarEventosPorUsuario(Long usuarioId) {
        return em.createQuery("SELECT e FROM EventosEntity e WHERE e.criadoPor = :usuarioId ORDER BY e.dataInicio DESC", EventosEntity.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    public List<EventosEntity> listarProximosEventos(int limite) {
        return em.createQuery("SELECT e FROM EventosEntity e WHERE e.dataInicio >= :dataAtual ORDER BY e.dataInicio ASC", EventosEntity.class)
                .setParameter("dataAtual", LocalDateTime.now())
                .setMaxResults(limite)
                .getResultList();
    }

    @Transactional
    public void prepararNovoEvento() {
        try {
            System.out.println("Preparando novo evento - limpando campos");
            
            evento = new EventosEntity();
            evento.setId(null);
            evento.setTitulo("");
            evento.setDescricao("");
            evento.setDataInicio(null);
            evento.setDataFim(null);
            evento.setLocal("");
            // criadoPor será definido automaticamente no salvar com o ID do usuário logado
            evento.setCriadoPor(null);
            evento.setDataCriacao(null);
            modoEdicao = false;
            
            System.out.println("Novo evento - campos limpos com sucesso");
            System.out.println("Título: '" + evento.getTitulo() + "'");
            System.out.println("Local: '" + evento.getLocal() + "'");
            
        } catch (Exception e) {
            System.err.println("Erro ao preparar novo evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    public void prepararEditarEvento(EventosEntity evento) {
        try {
            if (evento != null && evento.getId() != null) {
                // Buscar o evento diretamente do banco de dados para garantir dados atualizados
                this.evento = em.find(EventosEntity.class, evento.getId());
                if (this.evento == null) {
                    // Se não encontrar no banco, usar o evento passado como parâmetro
                    this.evento = evento;
                }
            } else {
                this.evento = new EventosEntity();
            }
            modoEdicao = true;
        } catch (Exception e) {
            System.err.println("Erro ao preparar edição do evento: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o evento original
            this.evento = evento != null ? evento : new EventosEntity();
            modoEdicao = true;
        }
    }

    @Transactional
    public void salvarEvento() {
        try {
            System.out.println("Tentando salvar evento - Título: '" + evento.getTitulo() + "'" + 
                              ", Local: '" + evento.getLocal() + "'" + 
                              ", Modo Edição: " + modoEdicao);
            
            // Validação manual dos campos obrigatórios
            if (evento.getTitulo() == null || evento.getTitulo().trim().isEmpty()) {
                System.err.println("Erro de validação: Título é obrigatório");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Título é obrigatório."));
                return;
            }
            
            if (evento.getLocal() == null || evento.getLocal().trim().isEmpty()) {
                System.err.println("Erro de validação: Local é obrigatório");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Local é obrigatório."));
                return;
            }
            
            if (evento.getDataInicio() == null) {
                System.err.println("Erro de validação: Data de início é obrigatória");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Data de início é obrigatória."));
                return;
            }
            
            if (evento.getDataFim() == null) {
                System.err.println("Erro de validação: Data de fim é obrigatória");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Data de fim é obrigatória."));
                return;
            }
            
            if (evento.getDataFim().isBefore(evento.getDataInicio())) {
                System.err.println("Erro de validação: Data de fim deve ser posterior à data de início");
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Data de fim deve ser posterior à data de início."));
                return;
            }
            
            if (!modoEdicao) {
                evento.setDataCriacao(LocalDateTime.now());
                // Definir automaticamente o usuário logado como criador
                Integer usuarioLogadoId = getUsuarioLogadoId();
                if (usuarioLogadoId != null) {
                    evento.setCriadoPor(usuarioLogadoId);
                    System.out.println("Usuário logado definido como criador: " + usuarioLogadoId);
                } else {
                    System.err.println("Erro: Usuário não está logado");
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Usuário não está logado."));
                    return;
                }
                em.persist(evento);
                em.flush();
                System.out.println("Evento criado com sucesso - ID: " + evento.getId());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento criado com sucesso!"));
            } else {
                // Na edição, manter o criador original ou atualizar para o usuário logado
                Integer usuarioLogadoId = getUsuarioLogadoId();
                if (usuarioLogadoId != null) {
                    evento.setCriadoPor(usuarioLogadoId);
                    System.out.println("Usuário logado definido como criador na edição: " + usuarioLogadoId);
                }
                em.merge(evento);
                em.flush();
                System.out.println("Evento atualizado com sucesso - ID: " + evento.getId());
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento atualizado com sucesso!"));
            }
            evento = new EventosEntity();
            modoEdicao = false;
        } catch (Exception e) {
            System.err.println("Erro ao salvar evento: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar evento: " + e.getMessage()));
        }
    }

    @Transactional
    public void excluirEvento(EventosEntity evento) {
        try {
            em.remove(em.merge(evento));
            em.flush();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento excluído com sucesso!"));
        } catch (Exception e) {
            System.err.println("Erro ao excluir evento: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir evento: " + e.getMessage()));
        }
    }

    public EventosEntity buscarEventoPorId(Long id) {
        return em.find(EventosEntity.class, id);
    }

    public void detalhesEvento(EventosEntity evento) {
        try {
            if (evento != null && evento.getId() != null) {
                // Buscar o evento diretamente do banco de dados para garantir dados atualizados
                this.eventoSelecionado = em.find(EventosEntity.class, evento.getId());
                if (this.eventoSelecionado == null) {
                    // Se não encontrar no banco, usar o evento passado como parâmetro
                    this.eventoSelecionado = evento;
                }
            } else {
                this.eventoSelecionado = null;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar detalhes do evento: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o evento original
            this.eventoSelecionado = evento;
        }
    }

    public void detalhesEventoPorId(Integer id) {
        try {
            if (id == null) {
                this.eventoSelecionado = null;
                return;
            }
            this.eventoSelecionado = em.find(EventosEntity.class, id);
            if (this.eventoSelecionado == null) {
                System.err.println("Evento não encontrado no banco pelo ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar detalhes do evento por ID: " + e.getMessage());
            this.eventoSelecionado = null;
        }
    }

    public void limparEventoSelecionado() {
        this.eventoSelecionado = null;
    }

    // Método para formatar data de início
    public String formatarDataInicio(LocalDateTime dataInicio) {
        System.out.println("Formatando data de início: " + dataInicio);
        if (dataInicio == null) {
            System.err.println("Data de início é nula");
            return "Data não informada";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataFormatada = dataInicio.format(formatter);
            System.out.println("Data de início formatada: " + dataFormatada);
            return dataFormatada;
        } catch (Exception e) {
            System.err.println("Erro ao formatar data de início: " + e.getMessage());
            System.err.println("Data original: " + dataInicio);
            e.printStackTrace();
            return dataInicio.toString();
        }
    }
    
    // Método para formatar data de fim
    public String formatarDataFim(LocalDateTime dataFim) {
        System.out.println("Formatando data de fim: " + dataFim);
        if (dataFim == null) {
            System.err.println("Data de fim é nula");
            return "Data não informada";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataFormatada = dataFim.format(formatter);
            System.out.println("Data de fim formatada: " + dataFormatada);
            return dataFormatada;
        } catch (Exception e) {
            System.err.println("Erro ao formatar data de fim: " + e.getMessage());
            System.err.println("Data original: " + dataFim);
            e.printStackTrace();
            return dataFim.toString();
        }
    }
    
    // Método para formatar data de criação
    public String formatarDataCriacao(LocalDateTime dataCriacao) {
        if (dataCriacao == null) {
            return "Data não informada";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dataCriacao.format(formatter);
        } catch (Exception e) {
            System.err.println("Erro ao formatar data de criação: " + e.getMessage());
            return dataCriacao.toString();
        }
    }
    
    // Método para formatar apenas o dia da data
    public String formatarDiaData(LocalDateTime data) {
        if (data == null) {
            return "--";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd");
            return data.format(formatter);
        } catch (Exception e) {
            System.err.println("Erro ao formatar dia: " + e.getMessage());
            return "--";
        }
    }
    
    // Método para formatar apenas o mês da data
    public String formatarMesData(LocalDateTime data) {
        if (data == null) {
            return "---";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM", java.util.Locale.forLanguageTag("pt-BR"));
            return data.format(formatter);
        } catch (Exception e) {
            System.err.println("Erro ao formatar mês: " + e.getMessage());
            return "---";
        }
    }
    
    // Método para formatar apenas o horário
    public String formatarHorario(LocalDateTime data) {
        if (data == null) {
            return "--:--";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            return data.format(formatter);
        } catch (Exception e) {
            System.err.println("Erro ao formatar horário: " + e.getMessage());
            return "--:--";
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
                return usuario.getNome() + " (" + usuarioId + ")";
            } else {
                return "Usuário ID: " + usuarioId + " (não encontrado)";
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            return "Usuário ID: " + usuarioId + " (erro)";
        }
    }
    
    // Método para obter o ID do usuário logado
    public Integer getUsuarioLogadoId() {
        try {
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) facesContext.getExternalContext().getSession(false);
            if (session != null) {
                UsuarioEntity usuarioLogado = (UsuarioEntity) session.getAttribute("usuarioLogado");
                if (usuarioLogado != null) {
                    return usuarioLogado.getCodigo();
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao obter usuário logado: " + e.getMessage());
            return null;
        }
    }
}
