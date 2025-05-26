-- Inserindo dois usuários na tabela 'usuarios'
INSERT INTO usuarios (cpf, nome, email, senha, nivel)
VALUES 
  ('123.456.789-00', 'Administrador do Sistema', '999999@upf.br', '123', 'adm'),
  ('987.654.321-04', 'João da Silva', '123@upf.br', '123', 'cliente'),
  ('987.654.321-14', 'João da Silva', '1234@upf.br', '123', 'cliente');

-- Inserindo eventos
INSERT INTO eventos (titulo, descricao, data_inicio, data_fim, local, criado_por) VALUES
('Semana da Tecnologia', 'Evento com palestras e workshops sobre desenvolvimento web, IA e segurança da informação.', '2025-06-10 09:00:00', '2025-06-14 18:00:00', 'Auditório Central - UPF', 1),

('Encontro de Startups', 'Networking e apresentações de projetos inovadores para investidores e aceleradoras.', '2025-06-20 10:00:00', '2025-06-20 17:00:00', 'Espaço InovaLab', 1),

('Hackathon Universitário', 'Maratona de programação com desafios práticos e premiações para as melhores soluções.', '2025-07-05 08:00:00', '2025-07-06 20:00:00', 'Laboratório de TI - Bloco B', 1),

('Palestra: Futuro da IA', 'Discussão sobre impactos sociais, éticos e profissionais da inteligência artificial.', '2025-07-10 19:00:00', '2025-07-10 21:00:00', 'Auditório 1 - Campus Sul', 1),

('Workshop: Design Thinking', 'Aprenda metodologias para criar soluções centradas no usuário.', '2025-07-15 13:00:00', '2025-07-15 17:00:00', 'Sala 203 - Prédio de Inovação', 1),

('Seminário de Cibersegurança', 'Tópicos avançados sobre proteção de dados e resposta a incidentes.', '2025-08-01 09:00:00', '2025-08-02 17:00:00', 'Auditório CyberSafe - UPF', 1),

('Feira de Empregabilidade Tech', 'Conecte-se com empresas de tecnologia que estão contratando.', '2025-08-10 09:00:00', '2025-08-10 16:00:00', 'Ginásio Multiuso - UPF', 1),

('Code Day', 'Dia inteiro de desafios de codificação, prêmios e muito networking.', '2025-08-22 08:00:00', '2025-08-22 18:00:00', 'Espaço Criativo - Sala 2', 1),

('Congresso de Engenharia de Software', 'Evento acadêmico com apresentações de artigos e mesas redondas.', '2025-09-05 09:00:00', '2025-09-07 18:00:00', 'Bloco Acadêmico 4 - UFRS', 1),

('Meetup: React & Node.js', 'Troca de experiências, cases e novidades no ecossistema JavaScript.', '2025-09-12 18:00:00', '2025-09-12 21:00:00', 'Coworking DigitalHub', 1),

('Bootcamp Fullstack', 'Treinamento intensivo em desenvolvimento front-end e back-end.', '2025-09-20 08:00:00', '2025-09-24 18:00:00', 'Sala Tech 5 - Bloco A', 1),

('Fórum de Tecnologia Educacional', 'Debates sobre inovação na educação e uso de tecnologias em sala de aula.', '2025-10-01 09:00:00', '2025-10-02 17:00:00', 'Auditório EducUP', 1),

('Oficina: UX/UI Design', 'Aprenda os fundamentos do design de experiência do usuário.', '2025-10-10 14:00:00', '2025-10-10 18:00:00', 'Sala de Criação - Bloco C', 1),

('Encontro de Comunidades Dev', 'Reunião das principais comunidades tech da região para bate-papo e integração.', '2025-10-15 18:00:00', '2025-10-15 21:00:00', 'Open Space - Pavilhão 1', 1),

('Palestra: Carreira em TI', 'Conselhos práticos para quem está começando na área de tecnologia.', '2025-10-20 19:00:00', '2025-10-20 20:30:00', 'Auditório Jovem Talento', 1),

('Mini Curso: Git e GitHub', 'Controle de versão na prática para desenvolvedores iniciantes.', '2025-10-25 09:00:00', '2025-10-25 12:00:00', 'Sala Dev 101', 1),

('Dev Talks', 'Talks técnicas com devs experientes do mercado.', '2025-11-01 14:00:00', '2025-11-01 18:00:00', 'Anfiteatro Coders', 1),

('Game Jam', 'Criação de jogos em equipe com temas surpresa e muita criatividade.', '2025-11-10 08:00:00', '2025-11-12 20:00:00', 'Lab de Jogos - UPF', 1),

('Intensivo: APIs REST com NestJS', 'Curso prático para dominar APIs modernas com NestJS.', '2025-11-20 09:00:00', '2025-11-22 18:00:00', 'Sala Backend Pro', 1),

('Feira Maker & Inovação', 'Exposição de projetos maker, protótipos e robótica educacional.', '2025-11-28 10:00:00', '2025-11-29 17:00:00', 'Pavilhão Criativo - ParqueTec', 1);
