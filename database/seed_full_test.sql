-- ============================================================
--  Lumen Cinema — Datos de prueba completos v2.0
--
--  Ejecutar DESPUÉS del schema:
--    mysql -u root -p cinema < database/seed_full_test.sql
--
--  Credenciales de acceso:
--    Usuarios (clients):  email listado abajo / contraseña: lumen2024
--    Empleados (workers): email listado abajo / contraseña: lumen2024
--
--  BCrypt de "lumen2024":
--    $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W
-- ============================================================

USE cinema;

-- ============================================================
-- CLIENTS  (rol en BD: ADMIN | CLIENTE | SUPERVISOR …)
-- ============================================================
INSERT IGNORE INTO clients
    (id, name, last_name, email, password, birth_date, user_type,
     visits_current_year, discount_active, role, image_url, created_at, updated_at)
VALUES
    (1, 'Admin',    'Lumen',   'admin@lumen.com',           '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', '1985-03-15', 'ADULT',   0,  FALSE, 'ADMIN',      NULL, NOW(), NOW()),
    (2, 'Carlos',   'Ruiz',    'carlos.ruiz@lumen.com',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', '1994-07-20', 'ADULT',   4,  FALSE, 'CLIENTE',    NULL, NOW(), NOW()),
    (3, 'Laura',    'Martin',  'laura.martin@lumen.com',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', '2001-09-10', 'STUDENT', 2,  FALSE, 'CLIENTE',    NULL, NOW(), NOW()),
    (4, 'Roberto',  'Lopez',   'roberto.lopez@lumen.com',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', '1958-02-20', 'SENIOR',  11, TRUE,  'CLIENTE',    NULL, NOW(), NOW()),
    (5, 'Supervisor','Lumen',  'supervisor@lumen.com',      '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', '1980-06-01', 'ADULT',   0,  FALSE, 'SUPERVISOR', NULL, NOW(), NOW());

-- ============================================================
-- WORKERS  (rol en BD: CAJERO | GERENCIA | MANTENIMIENTO | LIMPIEZA)
-- ============================================================
INSERT IGNORE INTO workers
    (id, name, email, password, role, phone_number, created_at)
VALUES
    (1, 'Carlos Sánchez', 'cajero@lumen.com',         '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'CAJERO',        '600111001', NOW()),
    (2, 'María García',   'gerencia@lumen.com',        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'GERENCIA',      '600111002', NOW()),
    (3, 'José Fernández', 'mantenimiento@lumen.com',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'MANTENIMIENTO', '600111003', NOW()),
    (4, 'Ana Torres',     'limpieza@lumen.com',        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'LIMPIEZA',      '600111004', NOW());

-- ============================================================
-- MOVIES
-- ============================================================
INSERT IGNORE INTO movie
    (id, title, description, duration_min, genre, age_rating, image_url, active, language, schedule, created_at)
VALUES
    (1, 'Dune: Parte Dos',  'Paul Atreides se une a los Fremen para vengar a su familia.',    166, 'Ciencia Ficción', '12',  NULL, TRUE, 'VOSE',     'Tarde',    NOW()),
    (2, 'Inside Out 2',     'Riley entra en la adolescencia y nuevas emociones aparecen.',    100, 'Animación',       'ALL', NULL, TRUE, 'Doblada',  'Familiar', NOW()),
    (3, 'Oppenheimer',      'Historia del físico J. Robert Oppenheimer y la bomba atómica.',  180, 'Drama',           '16',  NULL, TRUE, 'VOSE',     'Noche',    NOW()),
    (4, 'Alien: Romulus',   'Terror y ciencia ficción en las profundidades del espacio.',     119, 'Terror',          '18',  NULL, TRUE, 'VOSE',     'Noche',    NOW()),
    (5, 'El Señor de los Anillos: La Comunidad del Anillo', 'Un hobbit emprende un viaje épico para destruir el Anillo Único.', 178, 'Fantasía', 'ALL', NULL, TRUE, 'Doblada', 'Tarde', NOW());

-- ============================================================
-- THEATERS
-- ============================================================
INSERT IGNORE INTO theater
    (id, name, total_capacity, num_rows, num_columns)
VALUES
    (1, 'Sala 1 IMAX',     6, 2, 3),
    (2, 'Sala 2 Standard', 6, 2, 3),
    (3, 'Sala 3 VIP',      4, 2, 2);

-- ============================================================
-- SEATS
-- ============================================================
INSERT IGNORE INTO seat
    (id, theater_id, seat_row, seat_number, seat_type)
VALUES
    -- Sala 1 (6 asientos)
    (1,  1, 'A', 1, 'VIP'),
    (2,  1, 'A', 2, 'VIP'),
    (3,  1, 'A', 3, 'VIP'),
    (4,  1, 'B', 1, 'STANDARD'),
    (5,  1, 'B', 2, 'STANDARD'),
    (6,  1, 'B', 3, 'STANDARD'),
    -- Sala 2 (6 asientos)
    (7,  2, 'A', 1, 'VIP'),
    (8,  2, 'A', 2, 'VIP'),
    (9,  2, 'A', 3, 'VIP'),
    (10, 2, 'B', 1, 'STANDARD'),
    (11, 2, 'B', 2, 'STANDARD'),
    (12, 2, 'B', 3, 'STANDARD'),
    -- Sala 3 (4 asientos)
    (13, 3, 'A', 1, 'VIP'),
    (14, 3, 'A', 2, 'VIP'),
    (15, 3, 'B', 1, 'VIP'),
    (16, 3, 'B', 2, 'VIP');

-- ============================================================
-- SCREENINGS
--   Pasadas (id 1-2): para historial de compras
--   Futuras (id 3-5): para probar nuevas compras
-- ============================================================
INSERT IGNORE INTO screening
    (id, movie_id, theater_id, start_datetime, end_datetime, occupied_seats, is_full, base_price)
VALUES
    (1, 3, 1, '2026-05-10 20:00:00', '2026-05-10 23:00:00', 2, FALSE, 11.00),
    (2, 4, 3, '2026-05-15 22:00:00', '2026-05-15 23:59:00', 1, FALSE, 15.00),
    (3, 1, 1, '2026-06-05 18:00:00', '2026-06-05 20:46:00', 0, FALSE, 12.50),
    (4, 2, 2, '2026-06-08 17:00:00', '2026-06-08 18:40:00', 0, FALSE,  8.00),
    (5, 5, 2, '2026-06-15 19:00:00', '2026-06-15 21:58:00', 0, FALSE, 10.00);

-- ============================================================
-- SCREENING_SEATS
-- ============================================================
INSERT IGNORE INTO screening_seat
    (id, screening_id, seat_id, occupied, reserved_until)
VALUES
    -- Screening 1 (pasado) - 2 ocupados
    (1,  1, 1, TRUE,  NULL),
    (2,  1, 2, TRUE,  NULL),
    (3,  1, 3, FALSE, NULL),
    (4,  1, 4, FALSE, NULL),
    (5,  1, 5, FALSE, NULL),
    (6,  1, 6, FALSE, NULL),
    -- Screening 2 (pasado) - 1 ocupado
    (7,  2, 13, TRUE,  NULL),
    (8,  2, 14, FALSE, NULL),
    (9,  2, 15, FALSE, NULL),
    (10, 2, 16, FALSE, NULL),
    -- Screening 3 (futuro) - todos libres
    (11, 3, 1,  FALSE, NULL),
    (12, 3, 2,  FALSE, NULL),
    (13, 3, 3,  FALSE, NULL),
    (14, 3, 4,  FALSE, NULL),
    (15, 3, 5,  FALSE, NULL),
    (16, 3, 6,  FALSE, NULL),
    -- Screening 4 (futuro) - todos libres
    (17, 4, 7,  FALSE, NULL),
    (18, 4, 8,  FALSE, NULL),
    (19, 4, 9,  FALSE, NULL),
    (20, 4, 10, FALSE, NULL),
    (21, 4, 11, FALSE, NULL),
    (22, 4, 12, FALSE, NULL),
    -- Screening 5 (futuro) - todos libres
    (23, 5, 7,  FALSE, NULL),
    (24, 5, 8,  FALSE, NULL),
    (25, 5, 9,  FALSE, NULL),
    (26, 5, 10, FALSE, NULL),
    (27, 5, 11, FALSE, NULL),
    (28, 5, 12, FALSE, NULL);

-- ============================================================
-- PURCHASES
-- ============================================================
INSERT IGNORE INTO purchase
    (id, user_id, screening_id, status, total_amount, discount_applied,
     discount_amount, email_sent, guest_email, purchase_date,
     payment_intent_id, payment_method, paid_at)
VALUES
    (1, 2, 1, 'PAID',      22.00, FALSE, 0.00, TRUE,  NULL,                   '2026-05-10 17:30:00', 'pi_test_001', 'CARD', '2026-05-10 17:32:00'),
    (2, 3, 2, 'REFUNDED',  15.00, FALSE, 0.00, TRUE,  NULL,                   '2026-05-15 21:00:00', 'pi_test_002', 'CARD', '2026-05-15 21:02:00'),
    (3, 4, 1, 'PAID',       9.00, TRUE,  2.00, TRUE,  NULL,                   '2026-05-10 17:45:00', 'pi_test_003', 'CARD', '2026-05-10 17:47:00'),
    (4, NULL, 3, 'PENDING', 350.00, FALSE, 0.00, FALSE, 'invitado@email.com', '2026-05-19 10:00:00', NULL,          NULL,   NULL);

-- ============================================================
-- TICKETS
-- ============================================================
INSERT IGNORE INTO ticket
    (id, purchase_id, seat_id, screening_id, ticket_type, price)
VALUES
    (1, 1, 1, 1, 'ADULT',   11.00),
    (2, 1, 2, 1, 'ADULT',   11.00),
    (3, 2, 13, 2, 'STUDENT', 15.00),
    (4, 3, 4, 1, 'SENIOR',   9.00);

-- ============================================================
-- INCIDENTS
--   status: OPEN | IN_PROGRESS | RESOLVED
--   assigned_to: FK a workers (nullable)
-- ============================================================
INSERT IGNORE INTO incident
    (id, title, description, severity, category, room, status, assigned_to, created_at, updated_at)
VALUES
    (1, 'Proyector Sala 1',        'Parpadeo ocasional durante la sesión.',              'ALTA',  'Equipamiento',  'Sala 1', 'IN_PROGRESS', 3, NOW(), NOW()),
    (2, 'Terminal de pago',        'Terminal de taquilla 2 sin conexión intermitente.',  'MEDIA', 'Taquilla',      NULL,     'OPEN',        NULL, NOW(), NOW()),
    (3, 'Climatización Sala 3',    'Temperatura elevada en sesiones nocturnas.',         'ALTA',  'Climatización', 'Sala 3', 'OPEN',        3, NOW(), NOW()),
    (4, 'Limpieza pasillos',       'Restos de palomitas en el pasillo central.',         'BAJA',  'Limpieza',      NULL,     'RESOLVED',    4, NOW(), NOW()),
    (5, 'Sonido Sala 2',           'Eco en los altavoces traseros de la sala.',          'MEDIA', 'Sonido',        'Sala 2', 'IN_PROGRESS', 3, NOW(), NOW());

-- ============================================================
-- ROOMS
-- ============================================================
INSERT IGNORE INTO room
    (id, name, capacity, room_type, description, price_per_hour, active, created_at)
VALUES
    (1, 'Sala Eventos A',   50, 'EVENT',   'Sala para eventos privados con equipo audiovisual.', 80.00,  TRUE, NOW()),
    (2, 'Sala Reuniones B', 12, 'MEETING', 'Sala para reuniones de empresa con proyector.',      45.00,  TRUE, NOW()),
    (3, 'Sala VIP Premium', 20, 'VIP',     'Sala privada con servicio premium y catering.',      150.00, TRUE, NOW());

-- ============================================================
-- ROOM BOOKINGS
-- ============================================================
INSERT IGNORE INTO room_booking
    (id, room_id, user_id, booking_date, start_time, end_time,
     total_price, status, notes, created_at)
VALUES
    (1, 1, 2, '2026-06-10', '18:00:00', '21:00:00', 240.00, 'CONFIRMED', 'Cumpleaños privado.',            NOW()),
    (2, 2, 3, '2026-06-12', '10:00:00', '12:00:00',  90.00, 'PENDING',   'Reunión de equipo.',             NOW()),
    (3, 3, 4, '2026-06-15', '19:00:00', '22:00:00', 450.00, 'CANCELLED', 'Cancelada por el cliente.',      NOW()),
    (4, 1, 5, '2026-06-20', '09:00:00', '13:00:00', 320.00, 'CONFIRMED', 'Presentación de producto.',      NOW());

-- ============================================================
-- MERCHANDISE
-- ============================================================
INSERT IGNORE INTO merchandise
    (id, name, description, category, price, stock, min_stock, supplier, image_url, active, created_at)
VALUES
    (1, 'Palomitas Grandes',   'Cubo grande de palomitas con mantequilla.',     'FOOD',        5.50,  80, 20, 'ConcesiónPlus', NULL, TRUE, NOW()),
    (2, 'Refresco 500ml',      'Bebida fría de 500ml, varios sabores.',         'DRINK',        3.00, 100, 30, 'BeverageCo',    NULL, TRUE, NOW()),
    (3, 'Combo Familiar',      'Palomitas grandes + 2 refrescos.',              'FOOD',        11.00,  40, 10, 'ConcesiónPlus', NULL, TRUE, NOW()),
    (4, 'Camiseta Lumen',      'Camiseta oficial Lumen Cinema, talla única.',   'CLOTHING',    22.99,  25,  5, 'MerchandisePro',NULL, TRUE, NOW()),
    (5, 'Póster Dune',         'Póster A3 oficial de Dune: Parte Dos.',         'POSTERS',      7.50,  15,  3, 'PosterWorld',   NULL, TRUE, NOW()),
    (6, 'Agua 330ml',          'Botella de agua mineral 330ml.',                'DRINK',        2.00, 150, 50, 'BeverageCo',    NULL, TRUE, NOW());

-- ============================================================
-- MERCHANDISE SALES
-- ============================================================
INSERT IGNORE INTO merchandise_sale
    (id, user_id, merchandise_id, purchase_id, quantity, total, sale_date)
VALUES
    (1, 2, 1, 1, 2, 11.00, '2026-05-10 17:35:00'),
    (2, 2, 2, 1, 2,  6.00, '2026-05-10 17:35:00'),
    (3, 3, 5, NULL, 1, 7.50, '2026-05-16 12:00:00'),
    (4, 4, 3, 3, 1, 11.00, '2026-05-10 17:50:00');

-- ============================================================
-- REFUNDS
-- ============================================================
INSERT IGNORE INTO refunds
    (id, purchase_id, stripe_refund_id, amount, reason, status, created_at)
VALUES
    (1, 2, 're_test_lumen_001', 15.00, 'Solicitud del cliente', 'succeeded', NOW());

-- ============================================================
-- SHIFTS
-- ============================================================
INSERT IGNORE INTO shift
    (id, employee_id, shift_date, start_time, end_time, notes, status, created_at)
VALUES
    (1, 1, '2026-05-26', '09:00:00', '17:00:00', 'Turno de mañana taquilla.',   'SCHEDULED', NOW()),
    (2, 2, '2026-05-26', '10:00:00', '18:00:00', 'Gestión de sala y eventos.',  'SCHEDULED', NOW()),
    (3, 3, '2026-05-27', '08:00:00', '16:00:00', 'Revisión equipos proyección.','SCHEDULED', NOW()),
    (4, 4, '2026-05-27', '07:00:00', '15:00:00', 'Limpieza general antes de apertura.','SCHEDULED', NOW()),
    (5, 1, '2026-05-19', '09:00:00', '17:00:00', 'Turno completado.',           'COMPLETED', NOW()),
    (6, 3, '2026-05-18', '16:00:00', '00:00:00', 'No presentó justificante.',   'ABSENT',    NOW());

-- ============================================================
-- VERIFICACIÓN  (ejecutar para comprobar que todo se insertó)
-- ============================================================
SELECT table_name, total FROM (
    SELECT 'clients'          AS table_name, COUNT(*) AS total FROM clients
    UNION ALL SELECT 'workers',         COUNT(*) FROM workers
    UNION ALL SELECT 'movie',           COUNT(*) FROM movie
    UNION ALL SELECT 'theater',         COUNT(*) FROM theater
    UNION ALL SELECT 'seat',            COUNT(*) FROM seat
    UNION ALL SELECT 'screening',       COUNT(*) FROM screening
    UNION ALL SELECT 'screening_seat',  COUNT(*) FROM screening_seat
    UNION ALL SELECT 'purchase',        COUNT(*) FROM purchase
    UNION ALL SELECT 'ticket',          COUNT(*) FROM ticket
    UNION ALL SELECT 'incident',        COUNT(*) FROM incident
    UNION ALL SELECT 'room',            COUNT(*) FROM room
    UNION ALL SELECT 'room_booking',    COUNT(*) FROM room_booking
    UNION ALL SELECT 'merchandise',     COUNT(*) FROM merchandise
    UNION ALL SELECT 'merchandise_sale',COUNT(*) FROM merchandise_sale
    UNION ALL SELECT 'refunds',         COUNT(*) FROM refunds
    UNION ALL SELECT 'shift',           COUNT(*) FROM shift
) AS counts;