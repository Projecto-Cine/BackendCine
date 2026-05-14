-- ============================================================
--  Lumen Cinema — Schema completo
--  Ejecutar en MySQL Workbench o CLI:
--    mysql -u root -p < database/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS cinema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cinema;

-- ============================================================
-- 1. CLIENTS
-- ============================================================
CREATE TABLE IF NOT EXISTS clients (
    id                  BIGINT          AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255)    NOT NULL,
    last_name           VARCHAR(255),
    email               VARCHAR(255)    NOT NULL UNIQUE,
    password            VARCHAR(255),
    birth_date          DATE,
    user_type           VARCHAR(20),        -- ADULT | STUDENT | SENIOR
    visits_current_year INT             DEFAULT 0,
    discount_active     BOOLEAN         DEFAULT FALSE,
    role                VARCHAR(20)     DEFAULT 'CLIENTE',  -- ADMIN | CLIENTE
    image_url           VARCHAR(512),
    created_at          DATETIME,
    updated_at          DATETIME
);

-- ============================================================
-- 2. MOVIE
-- ============================================================
CREATE TABLE IF NOT EXISTS movie (
    id           BIGINT          AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(255)    NOT NULL,
    description  TEXT,
    duration_min INT             NOT NULL,
    genre        VARCHAR(100)    NOT NULL,
    age_rating   VARCHAR(10),               -- ALL | 7 | 12 | 16 | 18  (via AgeRatingConverter)
    image_url    VARCHAR(512),
    active       BOOLEAN         DEFAULT TRUE,
    language     VARCHAR(50),
    schedule     VARCHAR(255),
    created_at   DATETIME
);

-- ============================================================
-- 3. THEATER
-- ============================================================
CREATE TABLE IF NOT EXISTS theater (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    total_capacity INT          NOT NULL,
    num_rows       INT,
    num_columns    INT
);

-- ============================================================
-- 4. SEAT
-- ============================================================
CREATE TABLE IF NOT EXISTS seat (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    theater_id  BIGINT      NOT NULL,
    seat_row    VARCHAR(10) NOT NULL,
    seat_number INT         NOT NULL,
    seat_type   VARCHAR(20),               -- STANDARD | VIP
    UNIQUE KEY uq_seat (theater_id, seat_row, seat_number),
    CONSTRAINT fk_seat_theater FOREIGN KEY (theater_id) REFERENCES theater(id)
);

-- ============================================================
-- 5. SCREENING
-- ============================================================
CREATE TABLE IF NOT EXISTS screening (
    id             BIGINT         AUTO_INCREMENT PRIMARY KEY,
    movie_id       BIGINT         NOT NULL,
    theater_id     BIGINT         NOT NULL,
    start_datetime DATETIME       NOT NULL,
    end_datetime   DATETIME,
    occupied_seats INT            DEFAULT 0,
    is_full        BOOLEAN        DEFAULT FALSE,
    base_price     DECIMAL(10,2)  NOT NULL,
    CONSTRAINT fk_screening_movie   FOREIGN KEY (movie_id)   REFERENCES movie(id),
    CONSTRAINT fk_screening_theater FOREIGN KEY (theater_id) REFERENCES theater(id)
);

-- ============================================================
-- 6. SCREENING_SEAT
-- ============================================================
CREATE TABLE IF NOT EXISTS screening_seat (
    id             BIGINT   AUTO_INCREMENT PRIMARY KEY,
    screening_id   BIGINT   NOT NULL,
    seat_id        BIGINT   NOT NULL,
    occupied       BOOLEAN  DEFAULT FALSE,
    reserved_until DATETIME DEFAULT NULL,
    UNIQUE KEY uq_screening_seat (screening_id, seat_id),
    CONSTRAINT fk_ss_screening FOREIGN KEY (screening_id) REFERENCES screening(id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_seat      FOREIGN KEY (seat_id)      REFERENCES seat(id)
);

-- ============================================================
-- 7. PURCHASE
-- ============================================================
CREATE TABLE IF NOT EXISTS purchase (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    screening_id     BIGINT         NOT NULL,
    status           VARCHAR(20)    DEFAULT 'PENDING',  -- PENDING | PAID | CANCELLED
    total_amount     DECIMAL(10,2)  NOT NULL,
    discount_applied BOOLEAN        DEFAULT FALSE,
    discount_amount  DECIMAL(10,2)  DEFAULT 0.00,
    email_sent       BOOLEAN        DEFAULT FALSE,
    purchase_date    DATETIME,
    payment_intent_id VARCHAR(255),
    payment_method    VARCHAR(20),
    paid_at           DATETIME,
    CONSTRAINT fk_purchase_user      FOREIGN KEY (user_id)      REFERENCES clients(id),
    CONSTRAINT fk_purchase_screening FOREIGN KEY (screening_id) REFERENCES screening(id)
);

-- ============================================================
-- 8. TICKET
-- ============================================================
CREATE TABLE IF NOT EXISTS ticket (
    id           BIGINT         AUTO_INCREMENT PRIMARY KEY,
    purchase_id  BIGINT         NOT NULL,
    seat_id      BIGINT         NOT NULL,
    screening_id BIGINT         NOT NULL,
    ticket_type  VARCHAR(20)    NOT NULL,  -- CHILD | ADULT | STUDENT | SENIOR
    price        DECIMAL(10,2)  NOT NULL,
    UNIQUE KEY uq_ticket (screening_id, seat_id),
    CONSTRAINT fk_ticket_purchase  FOREIGN KEY (purchase_id)  REFERENCES purchase(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_seat      FOREIGN KEY (seat_id)      REFERENCES seat(id),
    CONSTRAINT fk_ticket_screening FOREIGN KEY (screening_id) REFERENCES screening(id)
);

-- ============================================================
-- 9. INCIDENT
-- ============================================================
CREATE TABLE IF NOT EXISTS incident (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    severity    VARCHAR(50)  NOT NULL,
    resolved    BOOLEAN      DEFAULT FALSE,
    created_at  DATETIME,
    updated_at  DATETIME
);

-- ============================================================
-- 10. ROOM
-- ============================================================
CREATE TABLE IF NOT EXISTS room (
    id             BIGINT         AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    capacity       INT            NOT NULL,
    room_type      VARCHAR(50)    DEFAULT 'STANDARD',  -- STANDARD | PREMIUM | VIP | ...
    description    TEXT,
    price_per_hour DECIMAL(10,2),
    active         BOOLEAN        DEFAULT TRUE,
    created_at     DATETIME
);

-- ============================================================
-- 11. ROOM_BOOKING
-- ============================================================
CREATE TABLE IF NOT EXISTS room_booking (
    id           BIGINT         AUTO_INCREMENT PRIMARY KEY,
    room_id      BIGINT         NOT NULL,
    user_id      BIGINT         NOT NULL,
    booking_date DATE           NOT NULL,
    start_time   TIME           NOT NULL,
    end_time     TIME           NOT NULL,
    total_price  DECIMAL(10,2),
    status       VARCHAR(20)    DEFAULT 'PENDING',  -- PENDING | CONFIRMED | CANCELLED
    notes        TEXT,
    created_at   DATETIME,
    CONSTRAINT fk_booking_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES clients(id)
);

-- ============================================================
-- 12. MERCHANDISE
-- ============================================================
CREATE TABLE IF NOT EXISTS merchandise (
    id          BIGINT         AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    category    VARCHAR(50),   -- CLOTHING | ACCESSORIES | POSTERS | COLLECTIBLES | FOOD | DRINK | MERCHANDISE | OTHER
    price       DECIMAL(10,2),
    stock       INT            DEFAULT 0,
    image_url   VARCHAR(512),
    active      BOOLEAN        DEFAULT TRUE,
    created_at  DATETIME
);

-- ============================================================
-- 13. MERCHANDISE_SALE
-- ============================================================
CREATE TABLE IF NOT EXISTS merchandise_sale (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT,
    merchandise_id   BIGINT,
    purchase_id      BIGINT,
    quantity         INT,
    total            DECIMAL(10,2),
    sale_date        DATETIME,
    CONSTRAINT fk_ms_user        FOREIGN KEY (user_id)        REFERENCES clients(id),
    CONSTRAINT fk_ms_merchandise FOREIGN KEY (merchandise_id) REFERENCES merchandise(id),
    CONSTRAINT fk_ms_purchase    FOREIGN KEY (purchase_id)    REFERENCES purchase(id)
);

-- ============================================================
-- 14. REFUNDS
-- ============================================================
CREATE TABLE IF NOT EXISTS refunds (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    purchase_id      BIGINT         NOT NULL,
    stripe_refund_id VARCHAR(255),
    amount           DECIMAL(10,2)  NOT NULL,
    reason           VARCHAR(255),
    status           VARCHAR(50),
    created_at       DATETIME
);

-- ============================================================
-- 15. WORKERS
-- ============================================================
CREATE TABLE IF NOT EXISTS workers (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    role       VARCHAR(50)  NOT NULL,  -- CAJERO | GERENCIA | SEGURIDAD | LIMPIEZA
    created_at DATETIME
);

-- ============================================================
-- 16. SHIFT
-- ============================================================
CREATE TABLE IF NOT EXISTS shift (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT,
    shift_date  DATE        NOT NULL,
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    notes       TEXT,
    status      VARCHAR(30) DEFAULT 'SCHEDULED',  -- SCHEDULED | COMPLETED | ABSENT
    created_at  DATETIME,
    CONSTRAINT fk_shift_employee FOREIGN KEY (employee_id) REFERENCES workers(id)
);
