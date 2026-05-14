-- ============================================================
-- Lumen Cinema - datos completos de prueba
-- Ejecutar despues de crear el esquema:
--   mysql -u root -p cinema < database/seed_full_test.sql
-- ============================================================

USE cinema;

-- CLIENTS
INSERT IGNORE INTO clients
    (id, name, last_name, email, password, birth_date, user_type, visits_current_year, discount_active, role, image_url, created_at, updated_at)
VALUES
    (1, 'Admin', 'Lumen', 'admin@lumen.com', 'lumen2024', '1990-01-01', 'ADULT', 0, FALSE, 'ADMIN', NULL, NOW(), NOW()),
    (2, 'Cliente', 'Lumen', 'cliente@lumen.com', 'lumen2024', '1994-04-15', 'ADULT', 4, FALSE, 'CLIENTE', NULL, NOW(), NOW()),
    (3, 'Laura', 'Martin', 'laura.test@lumen.com', 'lumen2024', '2001-09-10', 'STUDENT', 2, FALSE, 'CLIENTE', NULL, NOW(), NOW()),
    (4, 'Roberto', 'Lopez', 'roberto.test@lumen.com', 'lumen2024', '1958-02-20', 'SENIOR', 9, TRUE, 'CLIENTE', NULL, NOW(), NOW());

-- MOVIES
INSERT IGNORE INTO movie
    (id, title, description, duration_min, genre, age_rating, image_url, active, language, schedule, created_at)
VALUES
    (1, 'Dune: Parte Dos', 'Paul Atreides se une a los Fremen.', 166, 'Ciencia Ficcion', '12', NULL, TRUE, 'VOSE', 'Tarde', NOW()),
    (2, 'Inside Out 2', 'Riley entra en la adolescencia.', 100, 'Animacion', 'ALL', NULL, TRUE, 'Doblada', 'Familiar', NOW()),
    (3, 'Oppenheimer', 'Historia del fisico J. Robert Oppenheimer.', 180, 'Drama', '16', NULL, TRUE, 'VOSE', 'Noche', NOW()),
    (4, 'Alien: Romulus', 'Terror y ciencia ficcion en el espacio.', 119, 'Terror', '18', NULL, TRUE, 'VOSE', 'Noche', NOW());

-- THEATERS
INSERT IGNORE INTO theater
    (id, name, total_capacity, num_rows, num_columns)
VALUES
    (1, 'Sala 1 IMAX', 6, 2, 3),
    (2, 'Sala 2 Standard', 6, 2, 3),
    (3, 'Sala 3 VIP', 4, 2, 2);

-- SEATS
INSERT IGNORE INTO seat
    (id, theater_id, seat_row, seat_number, seat_type)
VALUES
    (1, 1, 'A', 1, 'VIP'),
    (2, 1, 'A', 2, 'VIP'),
    (3, 1, 'A', 3, 'VIP'),
    (4, 1, 'B', 1, 'STANDARD'),
    (5, 1, 'B', 2, 'STANDARD'),
    (6, 1, 'B', 3, 'STANDARD'),
    (7, 2, 'A', 1, 'VIP'),
    (8, 2, 'A', 2, 'VIP'),
    (9, 2, 'B', 1, 'STANDARD'),
    (10, 2, 'B', 2, 'STANDARD'),
    (11, 3, 'A', 1, 'VIP'),
    (12, 3, 'A', 2, 'VIP');

-- SCREENINGS
INSERT IGNORE INTO screening
    (id, movie_id, theater_id, start_datetime, end_datetime, occupied_seats, is_full, base_price)
VALUES
    (1, 1, 1, '2026-05-20 18:00:00', '2026-05-20 20:46:00', 2, FALSE, 12.50),
    (2, 2, 2, '2026-05-21 17:00:00', '2026-05-21 18:40:00', 1, FALSE, 8.00),
    (3, 3, 1, '2026-05-22 20:00:00', '2026-05-22 23:00:00', 0, FALSE, 11.00),
    (4, 4, 3, '2026-05-23 22:00:00', '2026-05-23 23:59:00', 1, FALSE, 15.00);

-- SCREENING SEATS
INSERT IGNORE INTO screening_seat
    (id, screening_id, seat_id, occupied)
VALUES
    (1, 1, 1, TRUE),
    (2, 1, 2, TRUE),
    (3, 1, 3, FALSE),
    (4, 1, 4, FALSE),
    (5, 2, 7, TRUE),
    (6, 2, 8, FALSE),
    (7, 2, 9, FALSE),
    (8, 3, 1, FALSE),
    (9, 3, 5, FALSE),
    (10, 4, 11, TRUE),
    (11, 4, 12, FALSE);

-- PURCHASES
INSERT IGNORE INTO purchase
    (id, user_id, screening_id, status, total_amount, discount_applied, discount_amount, email_sent, purchase_date, payment_intent_id, payment_method, paid_at)
VALUES
    (1, 2, 1, 'PAID', 25.00, FALSE, 0.00, TRUE, '2026-05-12 11:00:00', 'pi_test_lumen_001', 'CARD', '2026-05-12 11:02:00'),
    (2, 3, 2, 'PENDING', 8.00, FALSE, 0.00, FALSE, '2026-05-12 11:10:00', NULL, 'CASH', NULL),
    (3, 4, 4, 'REFUNDED', 12.00, TRUE, 3.00, TRUE, '2026-05-12 11:20:00', 'pi_test_lumen_003', 'CARD', '2026-05-12 11:22:00');

-- TICKETS
INSERT IGNORE INTO ticket
    (id, purchase_id, seat_id, screening_id, ticket_type, price)
VALUES
    (1, 1, 1, 1, 'ADULT', 12.50),
    (2, 1, 2, 1, 'ADULT', 12.50),
    (3, 2, 7, 2, 'STUDENT', 8.00),
    (4, 3, 11, 4, 'SENIOR', 12.00);

-- INCIDENTS
INSERT IGNORE INTO incident
    (id, title, description, severity, resolved, created_at, updated_at)
VALUES
    (1, 'Proyector Sala 1', 'Parpadeo ocasional durante la sesion.', 'MEDIA', FALSE, NOW(), NOW()),
    (2, 'Taquilla 2', 'Terminal de pago revisado y operativo.', 'BAJA', TRUE, NOW(), NOW()),
    (3, 'Climatizacion Sala 3', 'Temperatura alta en sesiones de noche.', 'ALTA', FALSE, NOW(), NOW());

-- ROOMS
INSERT IGNORE INTO room
    (id, name, capacity, room_type, description, price_per_hour, active, created_at)
VALUES
    (1, 'Sala Eventos A', 50, 'EVENT', 'Sala para eventos privados.', 80.00, TRUE, NOW()),
    (2, 'Sala Reuniones', 12, 'MEETING', 'Sala para reuniones de empresa.', 45.00, TRUE, NOW()),
    (3, 'Sala VIP Premium', 20, 'VIP', 'Sala privada con servicio premium.', 150.00, TRUE, NOW());

-- ROOM BOOKINGS
INSERT IGNORE INTO room_booking
    (id, room_id, user_id, booking_date, start_time, end_time, total_price, status, notes, created_at)
VALUES
    (1, 1, 2, '2026-05-25', '18:00:00', '21:00:00', 240.00, 'CONFIRMED', 'Cumpleanos privado.', NOW()),
    (2, 2, 3, '2026-05-26', '10:00:00', '12:00:00', 90.00, 'PENDING', 'Reunion de prueba.', NOW()),
    (3, 3, 4, '2026-05-27', '19:00:00', '22:00:00', 450.00, 'CANCELLED', 'Cancelada por el cliente.', NOW());

-- MERCHANDISE
INSERT IGNORE INTO merchandise
    (id, name, description, category, price, stock, image_url, active, created_at)
VALUES
    (1, 'Palomitas Grandes', 'Cubo grande de palomitas.', 'FOOD', 5.50, 100, NULL, TRUE, NOW()),
    (2, 'Refresco 500ml', 'Bebida fria de 500ml.', 'DRINK', 3.00, 120, NULL, TRUE, NOW()),
    (3, 'Camiseta Lumen', 'Camiseta oficial del cine.', 'CLOTHING', 22.99, 30, NULL, TRUE, NOW()),
    (4, 'Poster Dune', 'Poster A3 de pelicula.', 'POSTERS', 7.50, 20, NULL, TRUE, NOW());

-- MERCHANDISE SALES
INSERT IGNORE INTO merchandise_sale
    (id, user_id, merchandise_id, purchase_id, quantity, total, sale_date)
VALUES
    (1, 2, 1, 1, 2, 11.00, '2026-05-12 11:05:00'),
    (2, 2, 2, 1, 2, 6.00, '2026-05-12 11:05:00'),
    (3, 3, 4, NULL, 1, 7.50, '2026-05-12 11:15:00');

-- WORKERS
INSERT IGNORE INTO workers
    (id, name, email, password, role, created_at)
VALUES
    (1, 'Carlos', 'cajero@lumen.com',        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'CAJERO',        NOW()),
    (2, 'Maria',  'gerencia@lumen.com',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'GERENCIA',      NOW()),
    (3, 'Jose',   'mantenimiento@lumen.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'MANTENIMIENTO', NOW()),
    (4, 'Ana',    'limpieza@lumen.com',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumE04W', 'LIMPIEZA',      NOW());

-- SHIFTS
INSERT IGNORE INTO shift
    (id, employee_id, shift_date, start_time, end_time, notes, status, created_at)
VALUES
    (1, 1, '2026-05-20', '09:00:00', '17:00:00', 'Turno de taquilla.', 'SCHEDULED', NOW()),
    (2, 2, '2026-05-20', '10:00:00', '18:00:00', 'Gestion de sala.', 'COMPLETED', NOW()),
    (3, 3, '2026-05-21', '16:00:00', '23:59:00', 'Control accesos.', 'SCHEDULED', NOW()),
    (4, 4, '2026-05-21', '08:00:00', '16:00:00', 'Limpieza general.', 'ABSENT', NOW());

-- REFUNDS
INSERT IGNORE INTO refunds
    (id, purchase_id, stripe_refund_id, amount, reason, status, created_at)
VALUES
    (1, 3, 're_test_lumen_001', 12.00, 'Solicitud del cliente', 'succeeded', NOW());

-- CHECKS RAPIDOS
SELECT 'clients' AS table_name, COUNT(*) AS total FROM clients
UNION ALL SELECT 'movie', COUNT(*) FROM movie
UNION ALL SELECT 'theater', COUNT(*) FROM theater
UNION ALL SELECT 'seat', COUNT(*) FROM seat
UNION ALL SELECT 'screening', COUNT(*) FROM screening
UNION ALL SELECT 'screening_seat', COUNT(*) FROM screening_seat
UNION ALL SELECT 'purchase', COUNT(*) FROM purchase
UNION ALL SELECT 'ticket', COUNT(*) FROM ticket
UNION ALL SELECT 'incident', COUNT(*) FROM incident
UNION ALL SELECT 'room', COUNT(*) FROM room
UNION ALL SELECT 'room_booking', COUNT(*) FROM room_booking
UNION ALL SELECT 'merchandise', COUNT(*) FROM merchandise
UNION ALL SELECT 'merchandise_sale', COUNT(*) FROM merchandise_sale
UNION ALL SELECT 'workers', COUNT(*) FROM workers
UNION ALL SELECT 'shift', COUNT(*) FROM shift
UNION ALL SELECT 'refunds', COUNT(*) FROM refunds;
