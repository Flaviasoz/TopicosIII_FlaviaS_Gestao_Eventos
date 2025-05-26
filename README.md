Claro! Aqui está o seu README revisado, com uma estrutura mais clara, ortografia corrigida e formatação padronizada para facilitar a leitura e o entendimento:

---

# 📄 Sistema de Gestão de Eventos

## 👥 Autores

* **Flávia Roseane Alves de Souza**
  Matrícula: 198726
* **Vinicius do Carmo Loeblein**
  Matrícula: 198779

## 🎯 Objetivo

Desenvolver um sistema web para que empresas possam organizar eventos internos, como treinamentos, workshops, reuniões e confraternizações.
O sistema permitirá a **criação, gestão e participação em eventos**, promovendo a organização e o engajamento dos colaboradores.

---

## 📌 Escopo

### Funcionalidades Principais

* Autenticação de usuários (login/logout)
* Cadastro de eventos
* Listagem e visualização de detalhes dos eventos
* Inscrição em eventos
* Gerenciamento de participantes

### Funcionalidades Adicionais

* Envio de notificações e lembretes
* Estatísticas de participação
* Filtros e busca de eventos
* Comentários e feedback pós-evento

---

## 👤 Tipos de Usuário

| Tipo de Usuário               | Permissões                                                                                   |
| ----------------------------- | -------------------------------------------------------------------------------------------- |
| **Administrador/Organizador** | Criar, editar e cancelar eventos; visualizar e gerenciar participantes; acessar estatísticas |
| **Usuário Comum**             | Visualizar eventos, confirmar participação, enviar feedback                                  |

---

## 🧩 Casos de Uso

### 1. Autenticar Usuário

* **Ator:** Todos os usuários
* **Descrição:** Permite que o usuário entre no sistema por meio de login e senha. A sessão é mantida com autenticação (ex: JWT).
* **Fluxo:** Login → Verificação de credenciais → Redirecionamento para a dashboard

### 2. Cadastrar Evento

* **Ator:** Organizador
* **Descrição:** Permite criar um novo evento com título, descrição, local, data, horário e número máximo de participantes.
* **Campos:** Título, Descrição, Data, Horário, Local, Capacidade
* **Pré-condição:** Usuário autenticado e com perfil de organizador

### 3. Editar Evento

* **Ator:** Organizador
* **Descrição:** Permite atualizar informações do evento, como data, local ou descrição.

### 4. Cancelar Evento

* **Ator:** Organizador
* **Descrição:** Permite cancelar um evento. Os usuários inscritos são notificados.

### 5. Filtrar e Pesquisar Eventos

* **Ator:** Todos os usuários
* **Descrição:** Permite filtrar eventos por nome, local, data ou tipo.

### 6. Listar e Visualizar Eventos

* **Ator:** Todos os usuários
* **Descrição:** Permite visualizar a lista de eventos disponíveis e acessar seus detalhes (descrição, local, data, participantes confirmados).
* **Filtros disponíveis:** Data, nome do evento, local

### 7. Confirmar Participação

* **Ator:** Usuário comum
* **Descrição:** Permite que o usuário confirme presença em um evento.
* **Regra de negócio:** A quantidade de participantes não pode ultrapassar o número máximo definido.

### 8. Gerenciar Participantes

* **Ator:** Organizador
* **Descrição:** Permite visualizar e remover usuários inscritos no evento.

### 9. Enviar Notificações

* **Ator:** Sistema / Organizador
* **Descrição:** Envia automaticamente lembretes ou atualizações sobre o evento via e-mail ou sistema interno.

### 10. Visualizar Estatísticas

* **Ator:** Organizador
* **Descrição:** Exibe dados sobre os eventos, como total de inscritos e eventos com maior participação.

### 11. Enviar Feedback Pós-Evento

* **Ator:** Participante
* **Descrição:** Após o evento, o usuário pode deixar uma nota ou comentário sobre sua experiência.


![image](https://github.com/user-attachments/assets/1fa0c72a-4679-490f-86c3-a0151500c51f)

