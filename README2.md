# EventHub - Sistema de Gerenciamento de Eventos

## 📋 Descrição do Projeto

O **EventHub** é uma plataforma completa de gerenciamento de eventos desenvolvida em Java EE com JSF (Jakarta Server Faces) e PrimeFaces. O sistema permite que usuários criem, gerenciem e participem de eventos de forma intuitiva e eficiente.

## 🏗️ Arquitetura do Sistema

### Tecnologias Utilizadas
- **Backend**: Java EE 10, Jakarta Server Faces 4.0
- **Frontend**: PrimeFaces 13.0.6, PrimeFlex 3.3.1
- **Banco de Dados**: MySQL (via JPA/Hibernate)
- **Servidor**: GlassFish 7.0.12
- **IDE**: NetBeans (recomendado)

### Estrutura do Projeto
```
TopicosIII_FlaviaS_Gestao_Eventos/
└── eventmanagement/
    ├── src/main/
    │   ├── java/com/viniflavia/eventmanagement/
    │   │   ├── controller/     # Controllers JSF
    │   │   ├── entity/         # Entidades JPA
    │   │   └── util/           # Classes utilitárias
    │   ├── webapp/
    │   │   ├── WEB-INF/        # Configurações
    │   │   ├── css/            # Estilos CSS
    │   │   └── *.xhtml         # Páginas JSF
    │   └── resources/
    │       └── META-INF/       # Configurações JPA
    └── pom.xml                 # Dependências Maven
```

## 👥 Funcionalidades do Sistema

### Para Usuários Comuns
- ✅ **Cadastro e Login**: Sistema de autenticação completo
- ✅ **Explorar Eventos**: Buscar e filtrar eventos disponíveis
- ✅ **Inscrever-se**: Participar de eventos
- ✅ **Gerenciar Inscrições**: Ver e cancelar inscrições
- ✅ **Visualizar Detalhes**: Informações completas dos eventos

### Para Administradores
- ✅ **Criar Eventos**: Interface completa para criação
- ✅ **Editar Eventos**: Modificar informações existentes
- ✅ **Excluir Eventos**: Remover eventos do sistema
- ✅ **Gerenciar Usuários**: Controle de acesso administrativo

## 🖥️ Telas e Funcionalidades

### 1. **Página de Login** (`login.xhtml`)
**Acesso**: `/login.xhtml`

**Funcionalidades**:
- Autenticação de usuários
- Validação de credenciais
- Redirecionamento automático após login
- Mensagens de erro para credenciais inválidas

**Fluxo**:
1. Usuário insere email e senha
2. Sistema valida credenciais no banco
3. Se válido: redireciona para home
4. Se inválido: exibe mensagem de erro

---

### 2. **Página Inicial** (`home.xhtml`)
**Acesso**: `/home.xhtml` (após login)

**Seções Principais**:

#### 🎯 **Seção Hero**
- Boas-vindas personalizadas
- Botão "Explorar Eventos" (para todos)
- Botão "Criar Evento" (apenas para admins)

#### 📅 **Seus Próximos Eventos**
- Lista os próximos eventos em que o usuário está inscrito
- Botão "Ver Todos" → redireciona para `meusEventos.xhtml`
- Botão de detalhes em cada evento
- Mensagem quando não há inscrições

#### 🔍 **Descubra Eventos**
- Mostra 3 eventos futuros disponíveis
- Botão "Ver Todos" → redireciona para `explorar.xhtml`
- Botão de detalhes em cada evento

**Funcionalidades Especiais**:
- **Botões de Detalhes**: Abrem modal com informações completas
- **Status de Inscrição**: Mostra se está inscrito ou não
- **Ações Contextuais**: Participar/Cancelar inscrição

---

### 3. **Página de Explorar Eventos** (`explorar.xhtml`)
**Acesso**: `/explorar.xhtml`

**Funcionalidades Principais**:

#### 🔍 **Sistema de Filtros**
- **Busca por texto**: Pesquisa em título, descrição e local
- **Filtro por local**: Dropdown com locais disponíveis
- **Filtro por data**: Eventos futuros, passados ou todos
- **Botão "Limpar Filtros"**: Remove todos os filtros

#### 📱 **Layout Responsivo**
- **Modo Grid**: Cards visuais (padrão)
- **Modo Lista**: Tabela compacta
- **Toggle de Layout**: Botões para alternar visualização

#### 🎯 **Ações por Evento**
- **Botão Detalhes**: Modal com informações completas
- **Botão Inscrever-se**: Para eventos não inscritos
- **Botão Inscrito**: Status visual para eventos já inscritos

**Fluxo de Funcionamento**:
1. Página carrega todos os eventos
2. Filtros são aplicados em tempo real
3. Usuário pode inscrever-se diretamente
4. Detalhes são exibidos em modal

---

### 4. **Página Meus Eventos Inscritos** (`meusEventos.xhtml`)
**Acesso**: `/meusEventos.xhtml`

**Funcionalidades**:
- Lista **apenas** eventos em que o usuário está inscrito
- Botão de detalhes em cada evento
- Botão "Cancelar Inscrição" no modal de detalhes
- Mensagem quando não há inscrições
- Botão "Explorar Eventos" para encontrar novos eventos

**Diferença Importante**:
- **Antes**: Mostrava eventos criados pelo usuário
- **Agora**: Mostra eventos em que o usuário está inscrito

---

### 5. **Página de Gerenciamento de Eventos** (`eventos.xhtml`)
**Acesso**: `/eventos.xhtml` (apenas para admins)

**Funcionalidades**:
- **Tabela completa** de todos os eventos
- **Botões de ação**:
  - 👁️ **Detalhes**: Modal com informações
  - ✏️ **Editar**: Abre formulário de edição
  - 🗑️ **Excluir**: Remove evento (com confirmação)
- **Formulário de criação/edição**:
  - Campos: Título, Descrição, Data Início/Fim, Local
  - Validações automáticas
  - Mensagens de sucesso/erro

---

### 6. **Página de Gerenciamento de Inscrições** (`inscricoes.xhtml`)
**Acesso**: `/inscricoes.xhtml` (apenas para admins)

**Funcionalidades**:
- **Tabela de todas as inscrições** do sistema
- **Filtros por usuário e evento**
- **Ações**:
  - ✏️ **Editar**: Modificar inscrição
  - 🗑️ **Excluir**: Cancelar inscrição
- **Formulário de criação**:
  - Seleção de usuário e evento
  - Validação de duplicatas

---

### 7. **Página de Participantes** (`participantes.xhtml`)
**Acesso**: `/participantes.xhtml` (apenas para admins)

**Funcionalidades**:
- **Controle de presença** nos eventos
- **Registro de participantes** efetivos
- **Relatórios de presença**
- **Validação de inscrições**

### **Pré-requisitos**
- Java JDK 17 ou superior
- GlassFish 7.0.12
- PostgreSQL 15+
- NetBeans 21+ (recomendado)

### **Acesso**
- **URL**: `http://localhost:8080/eventmanagement/`
- **Admin**: admin@eventhub.com / admin123
- **Usuário**: user@eventhub.com / user123

## 🔍 Funcionalidades Técnicas Implementadas

### **Sistema de Filtros Avançado**
- Busca por texto em múltiplos campos
- Filtros por local e data
- Atualização em tempo real
- Cache de resultados

### **Modais de Detalhes**
- Implementação consistente em todas as páginas
- Dados sempre atualizados
- Ações contextuais (inscrever/cancelar)
- Formatação adequada de datas

### **Sistema de Cache**
- Cache de eventos inscritos
- Invalidação automática
- Performance otimizada

### **Validações**
- Campos obrigatórios
- Validação de datas
- Prevenção de duplicatas
- Mensagens de erro claras

## 🔒 Segurança

- **Autenticação obrigatória** em todas as páginas
- **Controle de acesso** por perfil (admin/user)
- **Validação de sessão** em todas as operações
- **Proteção contra SQL Injection** via JPA
- **Sanitização de inputs** no frontend

## 📊 Performance

- **Cache inteligente** de eventos inscritos
- **Lazy loading** de dados
- **Otimização de queries** JPA
- **Compressão de assets** CSS/JS
- **Minificação** de recursos

**Desenvolvido para o curso de Tópicos Especiais III** 