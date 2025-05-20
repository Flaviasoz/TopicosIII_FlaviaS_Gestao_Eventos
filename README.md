 
Documento do projeto: Sistema de 
Gestão de Eventos 
Nome: Flávia Roseane Alves de Souza.            Matrícula: 198726 
Nome: Vinicius do Carmo Loeblein.               Matrícula: 198779  
Objetivo 
Desenvolver  um  sistema  web  para  que  empresas  possam  organizar  eventos internos 
como  treinamentos,  workshops,  reuniões  e  confraternizações.  O  sistema  permitirá  a 
criação, gestão e participação em eventos, promovendo a organização e engajamento 
dos colaboradores. 
Escopo 
Funcionalidades Principais 
●  Autenticação de usuários (login/logout) 
●  Cadastro de eventos 
●  Listagem e visualização de detalhes dos eventos 
●  Inscrição em eventos 
●  Gerenciamento de participantes 
Funcionalidades Adicionais 
●  Envio de notiﬁcações e lembretes 
●  Estatísticas de participação 
●  Filtros e busca de eventos 
●  Comentários e feedback pós-evento 

Tipos de Usuário 
 
Tipo de Usuário  Permissões 
Administrador/Organizador  Criar, editar, cancelar eventos; visualizar e gerenciar 
participantes; acessar estatísticas 
Usuário Comum  Visualizar eventos, conﬁrmar participação, enviar 
feedback 
Casos de Uso 
1.  Autenticar Usuário 
Ator: Todos os usuários 
Descrição: Permite que o usuário entre no sistema por meio de login e senha. Sessão é 
mantida com autenticação (ex: JWT). 
Fluxo: Login → Veriﬁcação de credenciais → Redirecionamento à dashboard 
2.  Cadastrar Evento 
Ator: Organizador 
Descrição: Permite criar um novo evento com título, descrição, local, data, horário e 
número máximo de participantes. 
Campos: Título, Descrição, Data, Horário, Local, Capacidade 
Pré-condição: Usuário autenticado e com perﬁl de organizador 
 
3.  Editar Evento 
●  Ator: Organizador 
●  Descrição: Permite atualizar informações do evento como data, local ou 
descrição. 
4.  Cancelar Evento 
●  Ator: Organizador 
●  Descrição: Permite cancelar um evento. Os usuários inscritos são notiﬁcados. 
5.  Filtrar e Pesquisar Eventos 
●  Ator: Todos os usuários 
●  Descrição: Permite ﬁltrar eventos por nome, local, data ou tipo. 
6.  Listar e Visualizar Eventos 
Ator: Todos os usuários 
Descrição: Permite visualizar a lista de eventos disponíveis e acessar seus detalhes 
(descrição, local, data, participantes conﬁrmados). 
Filtros: Data, nome do evento, local 
7.  Conﬁrmar Participação 
Ator: Usuário comum 
Descrição: Permite que o usuário conﬁrme presença em um evento. 
Regra de negócio: Não pode ultrapassar o número máximo de participantes. 
8.  Gerenciar Participantes 
Ator: Organizador 
Descrição: Permite ao organizador visualizar e remover usuários inscritos no evento. 
9.  Enviar Notiﬁcações 
Ator: Sistema / Organizador 
 
 
Descrição: Envia automaticamente lembretes ou atualizações sobre o evento via e-mail 
ou sistema interno. 
10.  Visualizar Estatísticas 
Ator: Organizador 
Descrição: Exibe dados sobre eventos, como total de inscritos, eventos com mais 
participação etc. 
11.  Filtrar e Pesquisar Eventos 
Ator: Todos os usuários 
Descrição: Permite ﬁltrar eventos por nome, local, data ou tipo. 
12.  Enviar Feedback Pós-Evento 
Ator: Participante 
 
Descrição: Após o evento, o usuário pode deixar uma nota ou comentário sobre a 
experiência.
