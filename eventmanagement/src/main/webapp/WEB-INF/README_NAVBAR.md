# Componente Navbar - EventHub

## Descrição
O componente navbar foi criado para centralizar a navegação do sistema EventHub, permitindo que mudanças sejam feitas em apenas um local.

## Localização
O componente está localizado em: `/WEB-INF/navbar.xhtml`

## Como Usar

### 1. Incluir o componente em uma página XHTML:

```xml
<ui:include src="/WEB-INF/navbar.xhtml">
    <ui:param name="activePage" value="nome-da-pagina" />
</ui:include>
```

### 2. Valores possíveis para o parâmetro `activePage`:

- `home` - Para a página inicial
- `usuarios` - Para a página de usuários
- `eventos` - Para a página de eventos
- `inscricoes` - Para a página de inscrições
- `participantes` - Para a página de participantes
- `notificacoes` - Para a página de notificações
- `feedback` - Para a página de feedback

### 3. Exemplo de uso:

```xml
<h:form id="mainForm">
    <!-- Navbar Componente -->
    <ui:include src="/WEB-INF/navbar.xhtml">
        <ui:param name="activePage" value="usuarios" />
    </ui:include>
    
    <!-- Conteúdo da página -->
    <div class="main-content-container">
        <!-- ... resto do conteúdo ... -->
    </div>
</h:form>
```

## Funcionalidades

### Menu de Navegação Principal
- Home
- Usuários
- Eventos
- Inscrições
- Participantes
- Notificações
- Feedback

### Menu do Usuário
- Meu Perfil
- Configurações
- Sair

## Benefícios

1. **Manutenibilidade**: Mudanças na navbar são feitas em apenas um arquivo
2. **Consistência**: Todas as páginas usam a mesma navbar
3. **Reutilização**: O componente pode ser facilmente incluído em novas páginas
4. **Indicação de página ativa**: O parâmetro `activePage` permite destacar a página atual

## Estrutura do Componente

O componente utiliza:
- **PrimeFaces Menubar**: Para a estrutura principal da navbar
- **Parâmetros Facelets**: Para controlar qual item está ativo
- **CSS Classes**: Para estilização e indicação visual da página ativa

## Modificações

Para adicionar novos itens de menu ou modificar a navbar:

1. Edite o arquivo `/WEB-INF/navbar.xhtml`
2. Adicione o novo item de menu
3. Atualize a documentação com o novo valor para `activePage`
4. Teste em todas as páginas que usam o componente 