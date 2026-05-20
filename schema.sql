-- ============================================================
--  Lumen Cinema — Schema Completo v2.1
--  Compatible con los modelos JPA actuales del proyecto.
--
--  Uso:
--    mysql -u root -p < schema.sql
--    mysql -u root -p cinema < database/seed_full_test.sql
--
--  Tablas (16):
--    clients · movie · workers · theater · seat · screening ·
--    screening_seat · purchase · ticket · incident · room ·
--    room_booking · merchandise · merchandise_sale · refunds · shift
-- ============================================================

CREATE DATABASE IF NOT EXISTS cinema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cinema;

-- ============================================================
-- 1. CLIENTS  (entidad User)
--    role en BD: ADMIN | CLIENTE | SUPERVISOR | OPERATOR |
--                TICKET | MAINTENANCE | READONLY
--    visits_current_year: contador 0-10 para descuento fidelidad
-- ============================================================
CREATE TABLE IF NOT EXISTS clients (
    id                  BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255)  NOT NULL,
    last_name           VARCHAR(255),
    email               VARCHAR(255)  NOT NULL UNIQUE,
    password            VARCHAR(255),
    birth_date          DATE,
    user_type           VARCHAR(20),              -- ADULT | STUDENT | SENIOR
    visits_current_year INT           DEFAULT 0,  -- 0-10; en 10 → próxima compra con 10% dto
    discount_active     BOOLEAN       DEFAULT FALSE,
    role                VARCHAR(20)   DEFAULT 'CLIENTE',
    image_url           VARCHAR(512),
    created_at          DATETIME,
    updated_at          DATETIME,
    INDEX idx_clients_email (email),
    INDEX idx_clients_role  (role)
);

-- ============================================================
-- 2. MOVIE
--    age_rating en BD: ALL | 7 | 12 | 16 | 18
-- ============================================================
CREATE TABLE IF NOT EXISTS movie (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255)  NOT NULL,
    description  TEXT,
    duration_min INT           NOT NULL,
    genre        VARCHAR(100)  NOT NULL,
    age_rating   VARCHAR(10),
    image_url    VARCHAR(512),
    active       BOOLEAN       DEFAULT TRUE,
    language     VARCHAR(50),
    schedule     VARCHAR(255),
    format       VARCHAR(20)   NOT NULL DEFAULT '2D',
    created_at   DATETIME,
    INDEX idx_movie_active (active)
);

-- ============================================================
-- 3. WORKERS  (entidad Employee)
--    role en BD: CAJERO | GERENCIA | MANTENIMIENTO | LIMPIEZA
--    Se crea antes que incident y shift por dependencia FK.
-- ============================================================
CREATE TABLE IF NOT EXISTS workers (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255)  NOT NULL,
    email        VARCHAR(255)  NOT NULL UNIQUE,
    password     VARCHAR(255)  NOT NULL,
    role         VARCHAR(50)   NOT NULL,
    phone_number VARCHAR(20),
    created_at   DATETIME
);

-- ============================================================
-- 4. THEATER
-- ============================================================
CREATE TABLE IF NOT EXISTS theater (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255)  NOT NULL,
    total_capacity INT           NOT NULL,
    num_rows       INT,
    num_columns    INT
);

-- ============================================================
-- 5. SEAT
-- ============================================================
CREATE TABLE IF NOT EXISTS seat (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    theater_id  BIGINT        NOT NULL,
    seat_row    VARCHAR(10)   NOT NULL,
    seat_number INT           NOT NULL,
    seat_type   VARCHAR(20),                      -- STANDARD | VIP
    UNIQUE KEY uq_seat (theater_id, seat_row, seat_number),
    CONSTRAINT fk_seat_theater FOREIGN KEY (theater_id) REFERENCES theater(id) ON DELETE CASCADE
);

-- ============================================================
-- 6. SCREENING
-- ============================================================
CREATE TABLE IF NOT EXISTS screening (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    movie_id       BIGINT        NOT NULL,
    theater_id     BIGINT        NOT NULL,
    start_datetime DATETIME      NOT NULL,
    end_datetime   DATETIME,
    occupied_seats INT           DEFAULT 0,
    is_full        BOOLEAN       DEFAULT FALSE,
    base_price     DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_screening_movie   FOREIGN KEY (movie_id)   REFERENCES movie(id),
    CONSTRAINT fk_screening_theater FOREIGN KEY (theater_id) REFERENCES theater(id),
    INDEX idx_screening_start (start_datetime),
    INDEX idx_screening_movie (movie_id)
);

-- ============================================================
-- 7. SCREENING_SEAT
-- ============================================================
CREATE TABLE IF NOT EXISTS screening_seat (
    id             BIGINT    AUTO_INCREMENT PRIMARY KEY,
    screening_id   BIGINT    NOT NULL,
    seat_id        BIGINT    NOT NULL,
    occupied       BOOLEAN   DEFAULT FALSE,
    reserved_until DATETIME  DEFAULT NULL,
    UNIQUE KEY uq_screening_seat (screening_id, seat_id),
    CONSTRAINT fk_ss_screening FOREIGN KEY (screening_id) REFERENCES screening(id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_seat      FOREIGN KEY (seat_id)      REFERENCES seat(id)      ON DELETE CASCADE
);

-- ============================================================
-- 8. PURCHASE
--    user_id es nullable para compras de invitados (guest_email).
--    status: PENDING | PAID | CONFIRMED | CANCELLED | REFUNDED
--    payment_method: CASH | CARD | ONLINE
-- ============================================================
CREATE TABLE IF NOT EXISTS purchase (
    id                BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT,
    screening_id      BIGINT        NOT NULL,
    status            VARCHAR(20)   DEFAULT 'PENDING',
    total_amount      DECIMAL(10,2) NOT NULL,
    discount_applied  BOOLEAN       DEFAULT FALSE,
    discount_amount   DECIMAL(10,2) DEFAULT 0.00,
    email_sent        BOOLEAN       DEFAULT FALSE,
    guest_email       VARCHAR(255),
    purchase_date     DATETIME,
    payment_intent_id VARCHAR(255),
    payment_method    VARCHAR(20),
    paid_at           DATETIME,
    CONSTRAINT fk_purchase_user      FOREIGN KEY (user_id)      REFERENCES clients(id)  ON DELETE SET NULL,
    CONSTRAINT fk_purchase_screening FOREIGN KEY (screening_id) REFERENCES screening(id),
    INDEX idx_purchase_status     (status),
    INDEX idx_purchase_user       (user_id),
    INDEX idx_purchase_screening  (screening_id)
);

-- ============================================================
-- 9. TICKET
-- ============================================================
CREATE TABLE IF NOT EXISTS ticket (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    purchase_id  BIGINT        NOT NULL,
    seat_id      BIGINT        NOT NULL,
    screening_id BIGINT        NOT NULL,
    ticket_type  VARCHAR(20)   NOT NULL,          -- CHILD | ADULT | STUDENT | SENIOR
    price        DECIMAL(10,2) NOT NULL,
    UNIQUE KEY uq_ticket (screening_id, seat_id),
    CONSTRAINT fk_ticket_purchase  FOREIGN KEY (purchase_id)  REFERENCES purchase(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ticket_seat      FOREIGN KEY (seat_id)      REFERENCES seat(id),
    CONSTRAINT fk_ticket_screening FOREIGN KEY (screening_id) REFERENCES screening(id)
);

-- ============================================================
-- 10. INCIDENT
--     status: OPEN | IN_PROGRESS | RESOLVED
--     assigned_to → workers(id), nullable
-- ============================================================
CREATE TABLE IF NOT EXISTS incident (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255)  NOT NULL,
    description TEXT,
    severity    VARCHAR(50)   NOT NULL,           -- BAJA | MEDIA | ALTA
    category    VARCHAR(100),
    room        VARCHAR(100),
    status      VARCHAR(20)   NOT NULL DEFAULT 'OPEN',
    assigned_to BIGINT        NULL,
    created_at  DATETIME,
    updated_at  DATETIME,
    CONSTRAINT fk_incident_employee FOREIGN KEY (assigned_to) REFERENCES workers(id) ON DELETE SET NULL,
    INDEX idx_incident_status (status)
);

-- ============================================================
-- 11. ROOM
--     room_type: STANDARD | VIP | EVENT | MEETING
-- ============================================================
CREATE TABLE IF NOT EXISTS room (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255)  NOT NULL,
    capacity       INT           NOT NULL,
    room_type      VARCHAR(50)   DEFAULT 'STANDARD',
    description    TEXT,
    price_per_hour DECIMAL(10,2),
    active         BOOLEAN       DEFAULT TRUE,
    created_at     DATETIME
);

-- ============================================================
-- 12. ROOM_BOOKING
--     status: PENDING | CONFIRMED | CANCELLED
-- ============================================================
CREATE TABLE IF NOT EXISTS room_booking (
    id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
    room_id      BIGINT        NOT NULL,
    user_id      BIGINT        NOT NULL,
    booking_date DATE          NOT NULL,
    start_time   TIME          NOT NULL,
    end_time     TIME          NOT NULL,
    total_price  DECIMAL(10,2),
    status       VARCHAR(20)   DEFAULT 'PENDING',
    notes        TEXT,
    created_at   DATETIME,
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES clients(id),
    INDEX idx_booking_status (status),
    INDEX idx_booking_date   (booking_date)
);

-- ============================================================
-- 13. MERCHANDISE
--     category: FOOD | DRINK | CLOTHING | ACCESSORIES |
--               POSTERS | COLLECTIBLES | MERCHANDISE | OTHER
-- ============================================================
CREATE TABLE IF NOT EXISTS merchandise (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    category    VARCHAR(50),
    price       DECIMAL(10,2),
    stock       INT           DEFAULT 0,
    min_stock   INT           DEFAULT 0,
    supplier    VARCHAR(255),
    image_url   VARCHAR(512),
    active      BOOLEAN       DEFAULT TRUE,
    created_at  DATETIME,
    INDEX idx_merchandise_active (active)
);

-- ============================================================
-- 14. MERCHANDISE_SALE
-- ============================================================
CREATE TABLE IF NOT EXISTS merchandise_sale (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT,
    merchandise_id BIGINT,
    purchase_id    BIGINT,
    quantity       INT,
    total          DECIMAL(10,2),
    sale_date      DATETIME,
    CONSTRAINT fk_ms_user        FOREIGN KEY (user_id)        REFERENCES clients(id)     ON DELETE SET NULL,
    CONSTRAINT fk_ms_merchandise FOREIGN KEY (merchandise_id) REFERENCES merchandise(id),
    CONSTRAINT fk_ms_purchase    FOREIGN KEY (purchase_id)    REFERENCES purchase(id)    ON DELETE SET NULL
);

-- ============================================================
-- 15. REFUNDS
-- ============================================================
CREATE TABLE IF NOT EXISTS refunds (
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    purchase_id      BIGINT        NOT NULL,
    stripe_refund_id VARCHAR(255),
    amount           DECIMAL(10,2) NOT NULL,
    reason           VARCHAR(255),
    status           VARCHAR(50),
    created_at       DATETIME,
    CONSTRAINT fk_refund_purchase FOREIGN KEY (purchase_id) REFERENCES purchase(id) ON DELETE CASCADE
);

-- ============================================================
-- 16. SHIFT
--     status: SCHEDULED | COMPLETED | ABSENT
-- ============================================================
CREATE TABLE IF NOT EXISTS shift (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT,
    shift_date  DATE        NOT NULL,
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    notes       TEXT,
    status      VARCHAR(30) DEFAULT 'SCHEDULED',
    created_at  DATETIME,
    CONSTRAINT fk_shift_employee FOREIGN KEY (employee_id) REFERENCES workers(id) ON DELETE SET NULL,
    INDEX idx_shift_date   (shift_date),
    INDEX idx_shift_status (status)
);