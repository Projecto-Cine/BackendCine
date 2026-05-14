-- ============================================================
--  Lumen Cinema — Datos de prueba
--  Ejecutar DESPUÉS de schema.sql:
--    mysql -u root -p cinema < database/seed.sql
-- ============================================================

USE cinema;

-- ============================================================
-- CLIENTS
-- Contraseñas en texto plano; se cifran automáticamente con BCrypt
-- en el primer login (ver AuthServiceImpl.isPasswordValid)
-- ============================================================
INSERT IGNORE INTO clients (name, last_name, email, password, role, user_type, visits_current_year, discount_active)
VALUES
  ('Admin',   'Sistema',   'admin@cine.com',   'admin123',  'ADMIN',   NULL,      0, FALSE),
  ('Carlos',  'García',    'carlos@cine.com',  'user123',   'CLIENTE', 'ADULT',   5, FALSE),
  ('Laura',   'Martínez',  'laura@cine.com',   'user123',   'CLIENTE', 'STUDENT', 2, FALSE),
  ('Roberto', 'López',     'roberto@cine.com', 'user123',   'CLIENTE', 'SENIOR',  8, TRUE);

-- ============================================================
-- MOVIES
-- age_rating usa AgeRatingConverter: valores ALL | 7 | 12 | 16 | 18
-- ============================================================
INSERT IGNORE INTO movie (title, description, duration_min, genre, age_rating, active, language)
VALUES
  ('Dune: Parte Dos',
   'Paul Atreides se une a los Fremen en su búsqueda de venganza contra los conspiradores que destruyeron a su familia.',
   166, 'Ciencia Ficción', '12', TRUE, 'Versión Original'),

  ('El León Rey',
   'Un joven príncipe destronado debe reclamar su lugar en el reino del Pridelands.',
   88, 'Animación', 'ALL', TRUE, 'Doblada'),

  ('Oppenheimer',
   'La historia del físico J. Robert Oppenheimer y su papel en el desarrollo de la bomba atómica.',
   180, 'Drama Histórico', '16', TRUE, 'Versión Original'),

  ('Alien: Romulus',
   'Un grupo de jóvenes colonos se enfrenta a la forma de vida más aterradora del universo.',
   119, 'Ciencia Ficción / Terror', '18', TRUE, 'Versión Original'),

  ('Inside Out 2',
   'Riley entra en la adolescencia y nuevas emociones llegan para complicarlo todo.',
   100, 'Animación', 'ALL', TRUE, 'Doblada');

-- ============================================================
-- THEATERS
-- total_capacity debe coincidir con el nº de filas × columnas
-- ============================================================
INSERT IGNORE INTO theater (id, name, total_capacity, num_rows, num_columns)
VALUES
  (1, 'Sala 1 - IMAX',     60,  5, 12),
  (2, 'Sala 2 - Standard', 40,  4, 10),
  (3, 'Sala 3 - VIP',      24,  3,  8);

-- ============================================================
-- SEATS — Sala 1 (5 filas × 12 asientos; filas A-B VIP, C-E STANDARD)
-- ============================================================
INSERT IGNORE INTO seat (theater_id, seat_row, seat_number, seat_type) VALUES
  (1,'A', 1,'VIP'),(1,'A', 2,'VIP'),(1,'A', 3,'VIP'),(1,'A', 4,'VIP'),(1,'A', 5,'VIP'),
  (1,'A', 6,'VIP'),(1,'A', 7,'VIP'),(1,'A', 8,'VIP'),(1,'A', 9,'VIP'),(1,'A',10,'VIP'),
  (1,'A',11,'VIP'),(1,'A',12,'VIP'),
  (1,'B', 1,'VIP'),(1,'B', 2,'VIP'),(1,'B', 3,'VIP'),(1,'B', 4,'VIP'),(1,'B', 5,'VIP'),
  (1,'B', 6,'VIP'),(1,'B', 7,'VIP'),(1,'B', 8,'VIP'),(1,'B', 9,'VIP'),(1,'B',10,'VIP'),
  (1,'B',11,'VIP'),(1,'B',12,'VIP'),
  (1,'C', 1,'STANDARD'),(1,'C', 2,'STANDARD'),(1,'C', 3,'STANDARD'),(1,'C', 4,'STANDARD'),
  (1,'C', 5,'STANDARD'),(1,'C', 6,'STANDARD'),(1,'C', 7,'STANDARD'),(1,'C', 8,'STANDARD'),
  (1,'C', 9,'STANDARD'),(1,'C',10,'STANDARD'),(1,'C',11,'STANDARD'),(1,'C',12,'STANDARD'),
  (1,'D', 1,'STANDARD'),(1,'D', 2,'STANDARD'),(1,'D', 3,'STANDARD'),(1,'D', 4,'STANDARD'),
  (1,'D', 5,'STANDARD'),(1,'D', 6,'STANDARD'),(1,'D', 7,'STANDARD'),(1,'D', 8,'STANDARD'),
  (1,'D', 9,'STANDARD'),(1,'D',10,'STANDARD'),(1,'D',11,'STANDARD'),(1,'D',12,'STANDARD'),
  (1,'E', 1,'STANDARD'),(1,'E', 2,'STANDARD'),(1,'E', 3,'STANDARD'),(1,'E', 4,'STANDARD'),
  (1,'E', 5,'STANDARD'),(1,'E', 6,'STANDARD'),(1,'E', 7,'STANDARD'),(1,'E', 8,'STANDARD'),
  (1,'E', 9,'STANDARD'),(1,'E',10,'STANDARD'),(1,'E',11,'STANDARD'),(1,'E',12,'STANDARD');

-- SEATS — Sala 2 (4 filas × 10 asientos; fila A VIP, B-D STANDARD)
INSERT IGNORE INTO seat (theater_id, seat_row, seat_number, seat_type) VALUES
  (2,'A', 1,'VIP'),(2,'A', 2,'VIP'),(2,'A', 3,'VIP'),(2,'A', 4,'VIP'),(2,'A', 5,'VIP'),
  (2,'A', 6,'VIP'),(2,'A', 7,'VIP'),(2,'A', 8,'VIP'),(2,'A', 9,'VIP'),(2,'A',10,'VIP'),
  (2,'B', 1,'STANDARD'),(2,'B', 2,'STANDARD'),(2,'B', 3,'STANDARD'),(2,'B', 4,'STANDARD'),
  (2,'B', 5,'STANDARD'),(2,'B', 6,'STANDARD'),(2,'B', 7,'STANDARD'),(2,'B', 8,'STANDARD'),
  (2,'B', 9,'STANDARD'),(2,'B',10,'STANDARD'),
  (2,'C', 1,'STANDARD'),(2,'C', 2,'STANDARD'),(2,'C', 3,'STANDARD'),(2,'C', 4,'STANDARD'),
  (2,'C', 5,'STANDARD'),(2,'C', 6,'STANDARD'),(2,'C', 7,'STANDARD'),(2,'C', 8,'STANDARD'),
  (2,'C', 9,'STANDARD'),(2,'C',10,'STANDARD'),
  (2,'D', 1,'STANDARD'),(2,'D', 2,'STANDARD'),(2,'D', 3,'STANDARD'),(2,'D', 4,'STANDARD'),
  (2,'D', 5,'STANDARD'),(2,'D', 6,'STANDARD'),(2,'D', 7,'STANDARD'),(2,'D', 8,'STANDARD'),
  (2,'D', 9,'STANDARD'),(2,'D',10,'STANDARD');

-- SEATS — Sala 3 VIP (3 filas × 8 asientos; todas VIP)
INSERT IGNORE INTO seat (theater_id, seat_row, seat_number, seat_type) VALUES
  (3,'A',1,'VIP'),(3,'A',2,'VIP'),(3,'A',3,'VIP'),(3,'A',4,'VIP'),
  (3,'A',5,'VIP'),(3,'A',6,'VIP'),(3,'A',7,'VIP'),(3,'A',8,'VIP'),
  (3,'B',1,'VIP'),(3,'B',2,'VIP'),(3,'B',3,'VIP'),(3,'B',4,'VIP'),
  (3,'B',5,'VIP'),(3,'B',6,'VIP'),(3,'B',7,'VIP'),(3,'B',8,'VIP'),
  (3,'C',1,'VIP'),(3,'C',2,'VIP'),(3,'C',3,'VIP'),(3,'C',4,'VIP'),
  (3,'C',5,'VIP'),(3,'C',6,'VIP'),(3,'C',7,'VIP'),(3,'C',8,'VIP');

-- ============================================================
-- SCREENINGS (fechas futuras a partir de 2026-05-08)
-- ============================================================
INSERT IGNORE INTO screening (movie_id, theater_id, start_datetime, end_datetime, occupied_seats, is_full, base_price)
VALUES
  (1, 1, '2026-05-10 17:00:00', '2026-05-10 19:46:00', 12, FALSE, 12.50),
  (1, 2, '2026-05-10 20:30:00', '2026-05-10 23:16:00',  0, FALSE, 10.00),
  (2, 2, '2026-05-11 11:00:00', '2026-05-11 12:28:00',  5, FALSE,  8.00),
  (3, 1, '2026-05-11 19:00:00', '2026-05-11 22:00:00',  0, FALSE, 11.00),
  (4, 3, '2026-05-12 22:00:00', '2026-05-12 23:59:00',  0, FALSE, 15.00),
  (5, 2, '2026-05-13 16:00:00', '2026-05-13 17:40:00',  8, FALSE,  9.00);

-- ============================================================
-- INCIDENTS
-- ============================================================
INSERT IGNORE INTO incident (title, description, severity, resolved, created_at, updated_at)
VALUES
  ('Proyector Sala 1 con parpadeo', 'El proyector de Sala 1 presenta parpadeo intermitente durante proyecciones largas.', 'MEDIA',  FALSE, NOW(), NOW()),
  ('Climatización Sala 2 defectuosa', 'El aire acondicionado de Sala 2 no enfría correctamente. Se ha solicitado técnico.', 'ALTA',   FALSE, NOW(), NOW()),
  ('Taquilla 3 fuera de servicio',   'Terminal de pago de taquilla 3 no acepta tarjeta de crédito.',                       'BAJA',   TRUE,  NOW(), NOW());

-- ============================================================
-- ROOMS
-- ============================================================
INSERT IGNORE INTO room (name, capacity, room_type, description, price_per_hour, active)
VALUES
  ('Sala de Eventos A', 50, 'STANDARD', 'Sala multiusos ideal para cumpleaños y eventos privados.',       80.00, TRUE),
  ('Sala VIP Premium',  20, 'VIP',      'Sala exclusiva con sillones reclinables y servicio de catering.', 150.00, TRUE),
  ('Sala Junior',       30, 'STANDARD', 'Sala para pases privados con capacidad reducida.',                60.00, TRUE);

-- ============================================================
-- MERCHANDISE
-- ============================================================
INSERT IGNORE INTO merchandise (name, description, category, price, stock, active)
VALUES
  ('Camiseta Dune',         'Camiseta 100% algodón con el logo de Dune: Parte Dos.',     'CLOTHING',     22.99, 50, TRUE),
  ('Taza Lumen Cinema',     'Taza de cerámica con el logo del cine.',                    'ACCESSORIES',   9.99, 80, TRUE),
  ('Póster Oppenheimer A3', 'Póster oficial de la película en formato A3.',              'POSTERS',        7.50, 30, TRUE),
  ('Figura Alien Romulus',  'Figura coleccionable de 15 cm del alien clásico.',          'COLLECTIBLES',  29.99, 15, TRUE),
  ('Palomitas Grandes',     'Cubo grande de palomitas con mantequilla.',                 'FOOD',           5.50, 999, TRUE),
  ('Refresco 500ml',        'Refresco a elegir entre Coca-Cola, Fanta o agua.',          'DRINK',          3.00, 999, TRUE),
  ('Set Inside Out 2',      'Set de 5 pins metálicos de las emociones de Inside Out 2.', 'COLLECTIBLES',  12.99, 40, TRUE));

-- ============================================================
-- WORKERS
-- ============================================================
INSERT IGNORE INTO workers (name, email, role, created_at)
VALUES
  ('María Fernández', 'maria@cine.com',   'CAJERO',    NOW()),
  ('Javier Ruiz',     'javier@cine.com',  'GERENCIA',  NOW()),
  ('Ana Sánchez',     'ana@cine.com',     'SEGURIDAD', NOW()),
  ('Pedro Gómez',     'pedro@cine.com',   'LIMPIEZA',  NOW());

-- ============================================================
-- SHIFTS (semana actual)
-- ============================================================
INSERT IGNORE INTO shift (employee_id, shift_date, start_time, end_time, status, created_at)
VALUES
  (1, '2026-05-08', '09:00:00', '17:00:00', 'SCHEDULED', NOW()),
  (2, '2026-05-08', '10:00:00', '18:00:00', 'SCHEDULED', NOW()),
  (3, '2026-05-08', '16:00:00', '00:00:00', 'SCHEDULED', NOW()),
  (4, '2026-05-09', '08:00:00', '16:00:00', 'SCHEDULED', NOW()),
  (1, '2026-05-10', '09:00:00', '17:00:00', 'SCHEDULED', NOW());
