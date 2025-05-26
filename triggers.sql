-- Trigger de boas-vindas ao se cadastrar
CREATE OR REPLACE FUNCTION trigger_boas_vindas()
RETURNS TRIGGER AS $$
BEGIN
  IF NEW.nivel = 'adm' THEN
    INSERT INTO notificacoes (usuario_id, titulo, mensagem)
    VALUES (
      NEW.codigo,
      'Seja bem-vindo, Admin! 👑',
      'Olá ' || NEW.nome || '! Sua conta de administrador foi criada com sucesso. Agora você pode criar e gerenciar eventos incríveis! 🚀'
    );
  ELSE
    INSERT INTO notificacoes (usuario_id, titulo, mensagem)
    VALUES (
      NEW.codigo,
      'Bem-vindo ao universo dos eventos! ✨',
      'Oi ' || NEW.nome || '! Que bom te ver por aqui 😄 Agora é só explorar os eventos e garantir sua vaga nos que mais te interessarem! 🎫'
    );
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_boas_vindas
AFTER INSERT ON usuarios
FOR EACH ROW
EXECUTE FUNCTION trigger_boas_vindas();


-- Trigger de novo evento publicado
CREATE OR REPLACE FUNCTION notificar_evento_novo()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  SELECT 
    u.codigo,
    'Tem evento novo na área! 📢',
    'Um novo evento foi publicado: "' || NEW.titulo || '"! 👀 Corre lá pra conferir os detalhes e garantir sua vaga antes que acabe! 🏃‍♀️🏃‍♂️'
  FROM usuarios u
  WHERE u.nivel = 'cliente';

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_evento_novo
AFTER INSERT ON eventos
FOR EACH ROW
EXECUTE FUNCTION notificar_evento_novo();


-- Trigger de cancelamento de evento
CREATE OR REPLACE FUNCTION notificar_cancelamento_evento()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  SELECT 
    i.usuario_id,
    '😢 Evento cancelado: ' || OLD.titulo,
    'Poxa... o evento "' || OLD.titulo || '" foi cancelado. A gente sente muito mesmo! Mas fica tranquilo(a), outros eventos incríveis vêm por aí! 💙'
  FROM inscricoes i
  WHERE i.evento_id = OLD.id;

  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_cancelamento_evento
BEFORE DELETE ON eventos
FOR EACH ROW
EXECUTE FUNCTION notificar_cancelamento_evento();


-- Trigger de inscrição realizada
CREATE OR REPLACE FUNCTION notificar_inscricao_evento()
RETURNS TRIGGER AS $$
DECLARE
  titulo_evento VARCHAR;
BEGIN
  SELECT titulo INTO titulo_evento FROM eventos WHERE id = NEW.evento_id;

  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  VALUES (
    NEW.usuario_id,
    'Inscrição feita com sucesso! 🎉',
    'Você se inscreveu no evento "' || titulo_evento || '"! 💙 Agora falta só uma coisinha: confirmar sua presença e garantir sua participação de verdade! 😉'
  );

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_inscricao_evento
AFTER INSERT ON inscricoes
FOR EACH ROW
EXECUTE FUNCTION notificar_inscricao_evento();

-- Trigger de cancelamento de inscrição
CREATE OR REPLACE FUNCTION notificar_cancelamento_inscricao()
RETURNS TRIGGER AS $$
DECLARE
  titulo_evento VARCHAR;
BEGIN
  SELECT titulo INTO titulo_evento FROM eventos WHERE id = OLD.evento_id;

  INSERT INTO notificacoes (usuario_id, titulo, mensagem)
  VALUES (
    OLD.usuario_id,
    'Inscrição cancelada 😢',
    'Você cancelou sua inscrição no evento "' || titulo_evento || '". Sentiremos sua falta! Mas se mudar de ideia, estamos te esperando de volta! 💫'
  );

  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_cancelamento_inscricao
AFTER DELETE ON inscricoes
FOR EACH ROW
EXECUTE FUNCTION notificar_cancelamento_inscricao();


-- Trigger para notificar participação (presente ou ausente)
CREATE OR REPLACE FUNCTION notificar_participacao_evento()
RETURNS TRIGGER AS $$
DECLARE
  titulo_evento VARCHAR;
  usuario_id INTEGER;
BEGIN
  SELECT i.usuario_id, e.titulo
  INTO usuario_id, titulo_evento
  FROM inscricoes i
  JOIN eventos e ON e.id = i.evento_id
  WHERE i.id = NEW.inscricao_id;

  IF NEW.presente AND NOT OLD.presente THEN
    -- Participação confirmada
    INSERT INTO notificacoes (usuario_id, titulo, mensagem)
    VALUES (
      usuario_id,
      'Presença confirmada! 🙌',
      'Sua presença no evento "' || titulo_evento || '" foi registrada com sucesso! Obrigado por participar com a gente! 💙'
    );
  ELSIF NOT NEW.presente AND OLD.presente THEN
    -- Presença desmarcada
    INSERT INTO notificacoes (usuario_id, titulo, mensagem)
    VALUES (
      usuario_id,
      'Ausência registrada 😔',
      'Registramos que você não pôde comparecer ao evento "' || titulo_evento || '". Esperamos te ver na próxima! 🌟'
    );
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_notificar_participacao_evento
AFTER UPDATE ON participantes
FOR EACH ROW
WHEN (OLD.presente IS DISTINCT FROM NEW.presente)
EXECUTE FUNCTION notificar_participacao_evento();
