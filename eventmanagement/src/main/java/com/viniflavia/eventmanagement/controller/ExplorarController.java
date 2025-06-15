package com.viniflavia.eventmanagement.controller;

import com.viniflavia.eventmanagement.entity.EventosEntity;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named(value = "explorarController")
@SessionScoped
public class ExplorarController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private String termoBusca = "";
    private String filtroCategoria = "";
    private String filtroData = "";
    private String filtroLocal = "";
    private String layout = "grid";
    private EventosEntity eventoSelecionado = null;

    public ExplorarController() {
    }

    @PostConstruct
    public void inicializar() {
        try {
            System.out.println("=== INICIALIZANDO EXPLORARCONTROLLER ===");
            
            // Verificar se o usuário está logado
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) facesContext.getExternalContext().getSession(false);
            
            if (session == null) {
                System.err.println("Erro: Sessão não encontrada na inicialização");
                return;
            }
            
            com.viniflavia.eventmanagement.entity.UsuarioEntity usuarioLogado = 
                (com.viniflavia.eventmanagement.entity.UsuarioEntity) session.getAttribute("usuarioLogado");
            
            if (usuarioLogado == null) {
                System.err.println("Erro: Usuário não logado na inicialização");
                return;
            }
            
            System.out.println("Usuário logado na inicialização: " + usuarioLogado.getNome() + " (ID: " + usuarioLogado.getCodigo() + ")");
            
            // Inicializar filtros com valores padrão
            if (termoBusca == null) termoBusca = "";
            if (filtroLocal == null) filtroLocal = "";
            if (filtroData == null) filtroData = "";
            if (layout == null) layout = "grid";
            
            System.out.println("Filtros inicializados - Termo: '" + termoBusca + "', Local: '" + filtroLocal + "', Data: '" + filtroData + "'");
            
            // Testar se os eventos estão sendo carregados
            List<EventosEntity> eventos = getEventosFiltrados();
            System.out.println("Eventos carregados na inicialização: " + eventos.size());
            
            // Testar se os locais estão sendo carregados
            List<String> locais = getLocais();
            System.out.println("Locais disponíveis: " + locais.size());
            
            System.out.println("=== EXPLORARCONTROLLER INICIALIZADO COM SUCESSO ===");
            
        } catch (Exception e) {
            System.err.println("Erro na inicialização do ExplorarController: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
        }
    }

    public String getTermoBusca() {
        return termoBusca;
    }

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public String getFiltroCategoria() {
        return filtroCategoria;
    }

    public void setFiltroCategoria(String filtroCategoria) {
        this.filtroCategoria = filtroCategoria;
    }

    public String getFiltroData() {
        return filtroData;
    }

    public void setFiltroData(String filtroData) {
        this.filtroData = filtroData;
    }

    public String getFiltroLocal() {
        return filtroLocal;
    }

    public void setFiltroLocal(String filtroLocal) {
        this.filtroLocal = filtroLocal;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
        System.out.println("Layout alterado para: " + layout);
    }

    public EventosEntity getEventoSelecionado() {
        return eventoSelecionado;
    }

    public void setEventoSelecionado(EventosEntity eventoSelecionado) {
        this.eventoSelecionado = eventoSelecionado;
    }

    public List<EventosEntity> getEventosFiltrados() {
        try {
            System.out.println("=== INICIANDO BUSCA DE EVENTOS FILTRADOS ===");
            System.out.println("Termo de busca: '" + termoBusca + "'");
            System.out.println("Filtro local: '" + filtroLocal + "'");
            System.out.println("Filtro data: '" + filtroData + "'");
            
            // Se não há filtros, retornar todos os eventos
            if ((termoBusca == null || termoBusca.trim().isEmpty()) && 
                (filtroLocal == null || filtroLocal.trim().isEmpty()) && 
                (filtroData == null || filtroData.trim().isEmpty())) {
                
                System.out.println("Nenhum filtro aplicado - retornando todos os eventos");
                TypedQuery<EventosEntity> query = em.createQuery(
                    "SELECT e FROM EventosEntity e ORDER BY e.dataInicio ASC", EventosEntity.class);
                List<EventosEntity> eventos = query.getResultList();
                System.out.println("Total de eventos encontrados: " + eventos.size());
                return eventos;
            }
            
            StringBuilder jpql = new StringBuilder("SELECT e FROM EventosEntity e WHERE 1=1");
            List<Object> parameters = new ArrayList<>();
            int paramIndex = 1;
            
            // Filtro por termo de busca
            if (termoBusca != null && !termoBusca.trim().isEmpty()) {
                jpql.append(" AND (e.titulo LIKE ?").append(paramIndex).append(" OR e.descricao LIKE ?").append(paramIndex).append(" OR e.local LIKE ?").append(paramIndex).append(")");
                parameters.add("%" + termoBusca.trim() + "%");
                paramIndex++;
                System.out.println("Adicionado filtro de busca: '%" + termoBusca.trim() + "%'");
            }
            
            // Filtro por local
            if (filtroLocal != null && !filtroLocal.trim().isEmpty()) {
                jpql.append(" AND e.local = ?").append(paramIndex);
                parameters.add(filtroLocal.trim());
                paramIndex++;
                System.out.println("Adicionado filtro de local: '" + filtroLocal.trim() + "'");
            }
            
            // Filtro por data
            if (filtroData != null && !filtroData.trim().isEmpty()) {
                java.time.LocalDateTime dataAtual = java.time.LocalDateTime.now();
                if ("futuros".equals(filtroData)) {
                    jpql.append(" AND e.dataInicio >= ?").append(paramIndex);
                    parameters.add(dataAtual);
                    paramIndex++;
                    System.out.println("Adicionado filtro de eventos futuros: " + dataAtual);
                } else if ("passados".equals(filtroData)) {
                    jpql.append(" AND e.dataInicio < ?").append(paramIndex);
                    parameters.add(dataAtual);
                    paramIndex++;
                    System.out.println("Adicionado filtro de eventos passados: " + dataAtual);
                }
            }
            
            jpql.append(" ORDER BY e.dataInicio ASC");
            
            System.out.println("JPQL Query final: " + jpql.toString());
            System.out.println("Parâmetros: " + parameters);
            
            TypedQuery<EventosEntity> query = em.createQuery(jpql.toString(), EventosEntity.class);
            
            // Definir parâmetros
            for (int i = 0; i < parameters.size(); i++) {
                query.setParameter(i + 1, parameters.get(i));
            }
            
            List<EventosEntity> eventos = query.getResultList();
            System.out.println("Eventos encontrados: " + eventos.size());
            
            // Log dos eventos encontrados
            for (EventosEntity evento : eventos) {
                System.out.println("  - " + evento.getTitulo() + " (ID: " + evento.getId() + ", Local: " + evento.getLocal() + ")");
            }
            
            System.out.println("=== FIM DA BUSCA DE EVENTOS FILTRADOS ===");
            return eventos;
        } catch (Exception e) {
            System.err.println("Erro ao filtrar eventos: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getCategorias() {
        List<String> categorias = new ArrayList<>();
        categorias.add("Tecnologia");
        categorias.add("Educação");
        categorias.add("Negócios");
        categorias.add("Entretenimento");
        categorias.add("Esportes");
        return categorias;
    }

    public List<String> getLocais() {
        try {
            System.out.println("=== BUSCANDO LISTA DE LOCAIS DISPONÍVEIS ===");
            
            TypedQuery<String> query = em.createQuery(
                "SELECT DISTINCT e.local FROM EventosEntity e WHERE e.local IS NOT NULL AND e.local != '' ORDER BY e.local", String.class);
            
            List<String> locais = query.getResultList();
            System.out.println("Locais encontrados: " + locais.size());
            
            // Log dos locais encontrados
            for (String local : locais) {
                System.out.println("  - " + local);
            }
            
            System.out.println("=== FIM DA BUSCA DE LOCAIS ===");
            return locais;
        } catch (Exception e) {
            System.err.println("Erro ao buscar locais: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void detalhesEvento(EventosEntity evento) {
        try {
            System.out.println("=== EXIBINDO DETALHES DO EVENTO (ExplorarController) ===");
            System.out.println("Evento recebido: " + (evento != null ? evento.getTitulo() : "null"));
            System.out.println("ID do evento: " + (evento != null ? evento.getId() : "null"));
            
            if (evento == null) {
                this.eventoSelecionado = null;
                System.err.println("Evento recebido é nulo");
                return;
            }
            
            // Criar uma nova instância para evitar problemas de referência (mesma lógica do prepararEditarEvento)
            this.eventoSelecionado = new EventosEntity();
            this.eventoSelecionado.setId(evento.getId());
            this.eventoSelecionado.setTitulo(evento.getTitulo());
            this.eventoSelecionado.setDescricao(evento.getDescricao());
            this.eventoSelecionado.setDataInicio(evento.getDataInicio());
            this.eventoSelecionado.setDataFim(evento.getDataFim());
            this.eventoSelecionado.setLocal(evento.getLocal());
            this.eventoSelecionado.setCriadoPor(evento.getCriadoPor());
            this.eventoSelecionado.setDataCriacao(evento.getDataCriacao());
            
            System.out.println("Evento selecionado definido com sucesso - ID: " + eventoSelecionado.getId());
            System.out.println("Título: " + eventoSelecionado.getTitulo());
            System.out.println("Local: " + eventoSelecionado.getLocal());
            System.out.println("=== FIM DOS DETALHES DO EVENTO (ExplorarController) ===");
        } catch (Exception e) {
            System.err.println("Erro ao exibir detalhes do evento: " + e.getMessage());
            e.printStackTrace();
            this.eventoSelecionado = null;
        }
    }

    public boolean isInscrito(Integer eventoId) {
        try {
            System.out.println("Verificando se usuário está inscrito no evento ID: " + eventoId);
            
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) facesContext.getExternalContext().getSession(false);
            
            if (session == null) {
                System.err.println("Erro: Sessão não encontrada ao verificar inscrição");
                return false;
            }
            
            com.viniflavia.eventmanagement.entity.UsuarioEntity usuarioLogado = 
                (com.viniflavia.eventmanagement.entity.UsuarioEntity) session.getAttribute("usuarioLogado");
            
            if (usuarioLogado == null) {
                System.err.println("Erro: Usuário não logado ao verificar inscrição");
                return false;
            }
            
            if (eventoId == null) {
                System.err.println("Erro: ID do evento é nulo");
                return false;
            }
            
            System.out.println("Verificando inscrição - Usuário: " + usuarioLogado.getNome() + 
                             " (ID: " + usuarioLogado.getCodigo() + "), Evento ID: " + eventoId);
            
            // Usar a mesma lógica do InscricoesController
            jakarta.persistence.TypedQuery<com.viniflavia.eventmanagement.entity.InscricoesEntity> query = em.createQuery(
                "SELECT i FROM com.viniflavia.eventmanagement.entity.InscricoesEntity i WHERE i.eventoId = :eventoId AND i.usuarioId = :usuarioId", 
                com.viniflavia.eventmanagement.entity.InscricoesEntity.class);
            query.setParameter("eventoId", eventoId);
            query.setParameter("usuarioId", usuarioLogado.getCodigo());
            
            boolean inscrito = !query.getResultList().isEmpty();
            
            System.out.println("Resultado da verificação: " + (inscrito ? "INSCRITO" : "NÃO INSCRITO"));
            
            return inscrito;
        } catch (Exception e) {
            System.err.println("Erro ao verificar inscrição: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
            return false;
        }
    }

    public void limparFiltros() {
        try {
            System.out.println("=== LIMPANDO FILTROS ===");
            System.out.println("Filtros antes da limpeza:");
            System.out.println("  - Termo: '" + termoBusca + "'");
            System.out.println("  - Local: '" + filtroLocal + "'");
            System.out.println("  - Data: '" + filtroData + "'");
            
            termoBusca = "";
            filtroCategoria = "";
            filtroData = "";
            filtroLocal = "";
            
            System.out.println("Filtros após limpeza:");
            System.out.println("  - Termo: '" + termoBusca + "'");
            System.out.println("  - Local: '" + filtroLocal + "'");
            System.out.println("  - Data: '" + filtroData + "'");
            
            System.out.println("=== FILTROS LIMPOS COM SUCESSO ===");
        } catch (Exception e) {
            System.err.println("Erro ao limpar filtros: " + e.getMessage());
            System.err.println("Stack trace completo:");
            e.printStackTrace();
        }
    }

    // Método para navegar para a página explorar com tratamento de erros
    public String navegarParaExplorar() {
        try {
            System.out.println("Navegando para página explorar eventos");
            
            // Verificar se o usuário está logado
            jakarta.faces.context.FacesContext facesContext = jakarta.faces.context.FacesContext.getCurrentInstance();
            jakarta.servlet.http.HttpSession session = (jakarta.servlet.http.HttpSession) facesContext.getExternalContext().getSession(false);
            
            if (session == null) {
                System.err.println("Erro: Sessão não encontrada");
                return "login.xhtml?faces-redirect=true";
            }
            
            com.viniflavia.eventmanagement.entity.UsuarioEntity usuarioLogado = 
                (com.viniflavia.eventmanagement.entity.UsuarioEntity) session.getAttribute("usuarioLogado");
            
            if (usuarioLogado == null) {
                System.err.println("Erro: Usuário não logado");
                return "login.xhtml?faces-redirect=true";
            }
            
            System.out.println("Usuário logado: " + usuarioLogado.getNome() + " (ID: " + usuarioLogado.getCodigo() + ")");
            
            // Limpar filtros antes de navegar
            limparFiltros();
            
            System.out.println("Navegação para explorar.xhtml realizada com sucesso");
            return "explorar.xhtml?faces-redirect=true";
            
        } catch (Exception e) {
            System.err.println("Erro ao navegar para explorar eventos: " + e.getMessage());
            e.printStackTrace();
            
            // Adicionar mensagem de erro
            jakarta.faces.application.FacesMessage message = 
                new jakarta.faces.application.FacesMessage(
                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                    "Erro de Navegação",
                    "Erro ao acessar a página de eventos: " + e.getMessage()
                );
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null, message);
            
            return null; // Ficar na mesma página
        }
    }

    public void aplicarFiltros() {
        try {
            System.out.println("=== APLICANDO FILTROS ===");
            System.out.println("Filtros atuais:");
            System.out.println("  - Termo: '" + termoBusca + "'");
            System.out.println("  - Local: '" + filtroLocal + "'");
            System.out.println("  - Data: '" + filtroData + "'");
            
            // Forçar a atualização da lista chamando getEventosFiltrados
            List<EventosEntity> eventos = getEventosFiltrados();
            System.out.println("Lista atualizada com " + eventos.size() + " eventos");
            
            System.out.println("=== FILTROS APLICADOS COM SUCESSO ===");
        } catch (Exception e) {
            System.err.println("Erro ao aplicar filtros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para forçar a atualização da lista
    public void atualizarLista() {
        try {
            System.out.println("=== FORÇANDO ATUALIZAÇÃO DA LISTA ===");
            // Limpar cache se necessário
            // Forçar nova consulta
            List<EventosEntity> eventos = getEventosFiltrados();
            System.out.println("Lista atualizada com " + eventos.size() + " eventos");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar lista: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 