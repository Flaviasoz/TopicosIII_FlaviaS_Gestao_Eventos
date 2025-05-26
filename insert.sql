-- Inserindo dois usuários na tabela 'usuarios'
INSERT INTO usuarios (cpf, nome, email, senha, nivel)
VALUES 
  ('123.456.789-00', 'Administrador do Sistema', '999999@upf.br', '123', 'adm'),
  ('987.654.321-04', 'João da Silva', '123@upf.br', '123', 'cliente'),
  ('987.654.321-14', 'João da Silva', '1234@upf.br', '123', 'cliente');

-- Inserindo eventos
INSERT INTO eventos (titulo, descricao, data_inicio, data_fim, local, criado_por)
VALUES (
  'Semana da Tecnologia',
  'Evento com palestras e workshops sobre desenvolvimento web, IA e segurança da informação.',
  '2025-06-10 09:00:00',
  '2025-06-14 18:00:00',
  'Auditório Central - UPF',
  1
);
