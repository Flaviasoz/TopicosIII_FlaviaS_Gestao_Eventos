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

/*
 * funcoes e trigger no banco eventos
 */

-- Trigger associada à tabela usuarios
CREATE OR REPLACE FUNCTION trigger_boas_vindas()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.nivel = 'adm' THEN
    INSERT INTO notificacoes (usuario_id, titulo, mensagem)
    VALUES (
      NEW.codigo,
      'Bem-vindo, administrador!',
      'Olá ' || NEW.nome || ', você agora pode gerenciar eventos no sistema.'
    );
  ELSE
    INSERT INTO notificacoes (usuario_id, titulo, mensagem)
    VALUES (
      NEW.codigo,
      'Bem-vindo ao sistema!',
      'Olá ' || NEW.nome || ', seja bem-vindo! Agora você pode se inscrever em eventos.'
    );
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_boas_vindas
AFTER INSERT ON usuarios
FOR EACH ROW
EXECUTE FUNCTION trigger_boas_vindas();

-- Trigger associada à tabela eventos
-- novo evento
CREATE OR REPLACE FUNCTION notificar_evento_novo()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  SELECT 
    u.codigo,
    'Novo evento disponível: ' || NEW.titulo,
    'Foi adicionado um novo evento: "' || NEW.titulo || '". Acesse os detalhes e inscreva-se!'
  FROM usuarios u
  WHERE u.nivel = 'cliente';

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_evento_novo
AFTER INSERT ON eventos
FOR EACH ROW
EXECUTE FUNCTION notificar_evento_novo();


-- cancelamento evento
CREATE OR REPLACE FUNCTION notificar_cancelamento_evento()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  SELECT 
    i.usuario_id,
    'Evento cancelado: ' || OLD.titulo,
    'O evento "' || OLD.titulo || '" foi cancelado. Pedimos desculpas pelo transtorno.'
  FROM inscricoes i
  WHERE i.evento_id = OLD.id;

  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_cancelamento_evento
BEFORE DELETE ON eventos
FOR EACH ROW
EXECUTE FUNCTION notificar_cancelamento_evento();


-- Trigger associada à tabela inscricao
-- inscrição realizada
CREATE OR REPLACE FUNCTION notificar_inscricao_evento()
RETURNS TRIGGER AS $$
DECLARE
  titulo_evento VARCHAR;
BEGIN
  SELECT titulo INTO titulo_evento FROM eventos WHERE id = NEW.evento_id;

  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  VALUES (
    NEW.usuario_id,
    'Inscrição confirmada! 🎉',
    'Você se inscreveu no evento "' || titulo_evento || '" com sucesso! 💙 Mas atenção: falta só confirmar sua presença para garantir sua vaga. 😉'
  );

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_inscricao_evento
AFTER INSERT ON inscricoes
FOR EACH ROW
EXECUTE FUNCTION notificar_inscricao_evento();
