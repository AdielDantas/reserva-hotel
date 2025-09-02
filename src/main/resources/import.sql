-- Inserção de usuários
INSERT INTO tb_usuario (nome, email, senha) VALUES ('Admin Teste', 'admin@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');
INSERT INTO tb_usuario (nome, email, senha) VALUES ('Cliente Teste', 'cliente@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');
INSERT INTO tb_usuario (nome, email, senha) VALUES ('João Silva', 'joao@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');
INSERT INTO tb_usuario (nome, email, senha) VALUES ('Maria Souza', 'maria@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');
INSERT INTO tb_usuario (nome, email, senha) VALUES ('Carlos Lima', 'carlos@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');
INSERT INTO tb_usuario (nome, email, senha) VALUES ('Usuario Sem Reserva', 'semreserva@gmail.com', '$2a$10$N7SkKCa3r17ga.i.dF9iy.BFUBL2n3b6Z1CWSZWi/qy7ABq/E6VpO');

INSERT INTO tb_role(authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role(authority) VALUES ('ROLE_CLIENTE');

INSERT INTO tb_usuario_role(usuario_id, role_id) VALUES (1, 1);
INSERT INTO tb_usuario_role(usuario_id, role_id) VALUES (2, 2);
INSERT INTO tb_usuario_role(usuario_id, role_id) VALUES (3, 2);
INSERT INTO tb_usuario_role(usuario_id, role_id) VALUES (4, 2);
INSERT INTO tb_usuario_role(usuario_id, role_id) VALUES (5, 2);
INSERT INTO tb_usuario_role(usuario_id, role_id) VALUES (6, 2);

-- Inserção de quartos
INSERT INTO tb_quarto (valor_diaria, disponivel, tipo) VALUES (350.00, FALSE, 'LUXO');     -- ocupado
INSERT INTO tb_quarto (valor_diaria, disponivel, tipo) VALUES (150.00, TRUE, 'ECONOMICO');
INSERT INTO tb_quarto (valor_diaria, disponivel, tipo) VALUES (220.00, TRUE, 'STANDARD');
INSERT INTO tb_quarto (valor_diaria, disponivel, tipo) VALUES (180.00, TRUE, 'ECONOMICO');
INSERT INTO tb_quarto (valor_diaria, disponivel, tipo) VALUES (500.00, TRUE, 'LUXO');
INSERT INTO tb_quarto (valor_diaria, disponivel, tipo) VALUES (400.00, FALSE, 'LUXO');  -- ocupado

-- Inserção de reservas
-- Reserva anterior (Cliente Teste)
INSERT INTO tb_reserva (checkin, checkout, valor_total, status, usuario_id, quarto_id) VALUES (TIMESTAMP WITH TIME ZONE '2022-07-25T13:00:00Z', TIMESTAMP WITH TIME ZONE '2022-07-28T13:00:00Z', 700.00, 'CONFIRMADA', 2, 1);

-- Reserva atual (Maria Souza)
INSERT INTO tb_reserva (checkin, checkout, valor_total, status, usuario_id, quarto_id) VALUES (TIMESTAMP WITH TIME ZONE '2025-08-07T14:00:00Z', TIMESTAMP WITH TIME ZONE '2025-08-10T10:00:00Z', 1200.00, 'CONFIRMADA', 4, 6);

-- Reserva futura (João Silva)
INSERT INTO tb_reserva (checkin, checkout, valor_total, status, usuario_id, quarto_id) VALUES (TIMESTAMP WITH TIME ZONE '2025-09-01T12:00:00Z', TIMESTAMP WITH TIME ZONE '2025-09-05T11:00:00Z', 880.00, 'PENDENTE', 3, 5);

-- Reserva cancelada (Carlos Lima)
INSERT INTO tb_reserva (checkin, checkout, valor_total, status, usuario_id, quarto_id) VALUES (TIMESTAMP WITH TIME ZONE '2025-07-15T15:00:00Z', TIMESTAMP WITH TIME ZONE '2025-07-18T10:00:00Z', 660.00, 'CANCELADA', 5, 4);

-- Atualiza disponibilidade dos quartos com reservas confirmadas
UPDATE tb_quarto SET disponivel = FALSE WHERE id IN (1, 6);

-- Garante que quartos com reservas futuras/canceladas estejam disponíveis
UPDATE tb_quarto SET disponivel = TRUE WHERE id IN (4, 5);
