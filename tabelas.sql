-- ENUM para o nível de usuários
CREATE TYPE nivel_usuario AS ENUM ('adm', 'cliente');

-- Tabela de usuarios do sistema
CREATE TABLE usuarios (
    codigo 			INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cpf 			VARCHAR(14) NOT NULL UNIQUE,
    nome 			VARCHAR(100) NOT NULL,
    email 			VARCHAR(100) NOT NULL UNIQUE,
    senha 			TEXT NOT NULL,
    data_cadastro 	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nivel 			nivel_usuario NOT NULL DEFAULT 'cliente'
);

-- Tabela de cadastro do eventos
CREATE TABLE eventos (
    id 				INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo 			VARCHAR(200) NOT NULL,
    descricao 		TEXT,
    data_inicio 	TIMESTAMP NOT NULL,
    data_fim 		TIMESTAMP NOT NULL,
    local 			VARCHAR(200),
    criado_por 		INTEGER REFERENCES usuarios(codigo) ON DELETE SET NULL,
    data_criacao 	TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de cadastro de inscricoes
CREATE TABLE inscricoes (
    id 				INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id 		INTEGER REFERENCES usuarios(codigo) ON DELETE CASCADE,
    evento_id 		INTEGER REFERENCES eventos(id) ON DELETE CASCADE,
    data_inscricao 	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, evento_id)
);

-- Tabela de cadastro de participantes
CREATE TABLE participantes (
    id 				INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    inscricao_id 	INTEGER REFERENCES inscricoes(id) ON DELETE CASCADE,
    presente 		BOOLEAN DEFAULT FALSE,
    data_presenca 	TIMESTAMP
);

-- Tabela de cadastro de notificações
CREATE TABLE notificacoes (
    id 				INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id 		INTEGER REFERENCES usuarios(codigo) ON DELETE CASCADE,
    titulo 			VARCHAR(200),
    mensagem 		TEXT,
    enviada_em 		TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lida 			BOOLEAN DEFAULT FALSE
);

-- Tabela de cadastro de feedback
CREATE TABLE feedback (
    id 					INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    evento_id 			INTEGER REFERENCES eventos(id) ON DELETE CASCADE,
    usuario_id 	 	 	INTEGER REFERENCES usuarios(codigo) ON DELETE SET NULL,
    comentario 		 	TEXT NOT NULL,
    nota 				INTEGER CHECK (nota >= 1 AND nota <= 5),
    data_comentario 	TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
