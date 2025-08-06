-- Inserção de usuários
INSERT INTO tb_usuario (nome, email, senha, perfil) VALUES ('Admin Teste', 'admin@email.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO', 'ADMIN');
INSERT INTO tb_usuario (nome, email, senha, perfil) VALUES ('Cliente Teste', 'cliente@email.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO', 'CLIENTE');

-- Inserção de quartos
INSERT INTO tb_quarto (valor_diaria, disponivel) VALUES (350.00, TRUE);
INSERT INTO tb_quarto (valor_diaria, disponivel) VALUES (150.00, TRUE);

-- Inserção de reserva (supondo que usuario_id = 2 e quarto_id = 1)
INSERT INTO tb_reserva (checkin, checkout, valor_total, status, usuario_id, quarto_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-28T13:00:00Z', 700.00, 'CONFIRMADA', 2, 1);

-- Atualiza disponibilidade do quarto
UPDATE tb_quarto SET disponivel = FALSE WHERE id = 1;

