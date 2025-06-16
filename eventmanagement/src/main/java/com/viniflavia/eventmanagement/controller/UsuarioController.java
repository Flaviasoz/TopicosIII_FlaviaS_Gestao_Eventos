package com.viniflavia.eventmanagement.controller;

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

@Named(value = "usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    @PersistenceContext(unitName = "eventmanagementPU")
    private EntityManager em;

    private UsuarioEntity usuario = new UsuarioEntity();
    private UsuarioEntity usuarioSelecionado = new UsuarioEntity();
    private boolean modoEdicao = false;

    public UsuarioController() {
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public UsuarioEntity getUsuarioSelecionado() {
        return usuarioSelecionado;
    }

    public void setUsuarioSelecionado(UsuarioEntity usuarioSelecionado) {
        this.usuarioSelecionado = usuarioSelecionado;
    }

    public boolean isModoEdicao() {
        return modoEdicao;
    }

    public void setModoEdicao(boolean modoEdicao) {
        this.modoEdicao = modoEdicao;
    }

    public List<UsuarioEntity> listarTodos() {
        try {
            List<UsuarioEntity> usuarios = em.createQuery("SELECT u FROM UsuarioEntity u ORDER BY u.nome", UsuarioEntity.class).getResultList();
            System.out.println("Total de usuários encontrados: " + usuarios.size());
            return usuarios;
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void prepararNovoUsuario() {
        usuario = new UsuarioEntity();
        usuario.setCodigo(null);
        usuario.setNome(null);
        usuario.setEmail(null);
        usuario.setCpf(null);
        usuario.setSenha(null);
        usuario.setNivel("cliente");
        usuario.setDataCadastro(null);
        modoEdicao = false;
        
        System.out.println("Novo usuário - campos limpos");
    }

    @Transactional
    public void prepararEditarUsuario(UsuarioEntity usuario) {
        // Criar uma nova instância para evitar problemas de referência
        this.usuario = new UsuarioEntity();
        this.usuario.setCodigo(usuario.getCodigo());
        this.usuario.setNome(usuario.getNome());
        this.usuario.setEmail(usuario.getEmail());
        this.usuario.setCpf(usuario.getCpf());
        this.usuario.setSenha(usuario.getSenha());
        this.usuario.setNivel(usuario.getNivel());
        this.usuario.setDataCadastro(usuario.getDataCadastro());
        modoEdicao = true;
        
        System.out.println("Editando usuário - ID: " + this.usuario.getCodigo() + 
                          ", Nome: " + this.usuario.getNome() + 
                          ", Email: " + this.usuario.getEmail());
    }

    @Transactional
    public void salvarUsuario() {
        try {
            System.out.println("Tentando salvar usuário - Nome: " + usuario.getNome() + 
                              ", Email: " + usuario.getEmail() + 
                              ", Modo Edição: " + modoEdicao);
            
            // Validação manual dos campos obrigatórios
            if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Nome é obrigatório."));
                return;
            }
            
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Email é obrigatório."));
                return;
            }
            
            if (usuario.getCpf() == null || usuario.getCpf().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "CPF é obrigatório."));
                return;
            }
            
            if (!modoEdicao && (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty())) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Senha é obrigatória para novos usuários."));
                return;
            }
            
            if (!modoEdicao) {
                usuario.setDataCadastro(LocalDateTime.now());
                if (usuario.getNivel() == null || usuario.getNivel().trim().isEmpty()) {
                    usuario.setNivel("cliente"); // Padrão para novos usuários
                }
                em.persist(usuario);
                em.flush();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário criado com sucesso!"));
            } else {
                em.merge(usuario);
                em.flush();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário atualizado com sucesso!"));
            }
            usuario = new UsuarioEntity();
            modoEdicao = false;
        } catch (Exception e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar usuário: " + e.getMessage()));
        }
    }

    @Transactional
    public void excluirUsuario(UsuarioEntity usuario) {
        try {
            em.remove(em.merge(usuario));
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário excluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir usuário: " + e.getMessage()));
        }
    }

    public UsuarioEntity buscarUsuarioPorId(Integer id) {
        return em.find(UsuarioEntity.class, id);
    }

    public void detalhesUsuario(UsuarioEntity usuario) {
        try {
            if (usuario != null && usuario.getCodigo() != null) {
                // Buscar o usuário diretamente do banco de dados para garantir dados atualizados
                this.usuarioSelecionado = em.find(UsuarioEntity.class, usuario.getCodigo());
                if (this.usuarioSelecionado == null) {
                    // Se não encontrar no banco, usar o usuário passado como parâmetro
                    this.usuarioSelecionado = usuario;
                }
            } else {
                this.usuarioSelecionado = new UsuarioEntity();
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar detalhes do usuário: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, usar o usuário original
            this.usuarioSelecionado = usuario != null ? usuario : new UsuarioEntity();
        }
    }
    
    public void limparUsuarioSelecionado() {
        this.usuarioSelecionado = new UsuarioEntity();
    }

    // Método para formatar data de cadastro
    public String formatarDataCadastro(LocalDateTime dataCadastro) {
        if (dataCadastro == null) {
            return "Data não informada";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return dataCadastro.format(formatter);
        } catch (Exception e) {
            System.err.println("Erro ao formatar data: " + e.getMessage());
            return dataCadastro.toString();
        }
    }
}
