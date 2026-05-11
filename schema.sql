CREATE DATABASE IF NOT EXISTS cinema;
USE cinema;

-- ============================================
-- ENUMS
-- ============================================
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    birth_date DATE,
    user_type VARCHAR(50),
    visits_current_year INT DEFAULT 0,
    discount_active BOOLEAN DEFAULT FALSE,
    role VARCHAR(50) DEFAULT 'CLIENTE',
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_min INT NOT NULL,
    genre VARCHAR(255) NOT NULL,
    age_rating VARCHAR(50),
    image_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    language VARCHAR(255),
    schedule VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS theater (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_capacity INT NOT NULL,
    num_rows INT,
    num_columns INT
);

CREATE TABLE IF NOT EXISTS seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theater_id BIGINT NOT NULL,
    seat_row VARCHAR(255) NOT NULL,
    seat_number INT NOT NULL,
    seat_type VARCHAR(50),
    UNIQUE KEY uk_seat_theater (theater_id, seat_row, seat_number),
    FOREIGN KEY (theater_id) REFERENCES theater(id)
);

CREATE TABLE IF NOT EXISTS shift (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    notes TEXT,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES workers(id)
);

CREATE TABLE IF NOT EXISTS screening (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    theater_id BIGINT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME,
    occupied_seats INT DEFAULT 0,
    is_full BOOLEAN DEFAULT FALSE,
    base_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (theater_id) REFERENCES theater(id)
);

CREATE TABLE IF NOT EXISTS screening_seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    screening_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    occupied BOOLEAN DEFAULT FALSE,
    UNIQUE KEY uk_screening_seat (screening_id, seat_id),
    FOREIGN KEY (screening_id) REFERENCES screening(id),
    FOREIGN KEY (seat_id) REFERENCES seat(id)
);

CREATE TABLE IF NOT EXISTS purchase (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    screening_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    discount_applied BOOLEAN DEFAULT FALSE,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    email_sent BOOLEAN DEFAULT FALSE,
    purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES clients(id),
    FOREIGN KEY (screening_id) REFERENCES screening(id)
);

CREATE TABLE IF NOT EXISTS ticket (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    screening_id BIGINT NOT NULL,
    ticket_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    UNIQUE KEY uk_ticket_screening_seat (screening_id, seat_id),
    FOREIGN KEY (purchase_id) REFERENCES purchase(id),
    FOREIGN KEY (seat_id) REFERENCES seat(id),
    FOREIGN KEY (screening_id) REFERENCES screening(id)
);

CREATE TABLE IF NOT EXISTS room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    room_type VARCHAR(50) DEFAULT 'STANDARD',
    description TEXT,
    price_per_hour DECIMAL(10,2),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS room_booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_price DECIMAL(10,2),
    status VARCHAR(50) DEFAULT 'PENDING',
    notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES room(id),
    FOREIGN KEY (user_id) REFERENCES clients(id)
);

CREATE TABLE IF NOT EXISTS merchandise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    category VARCHAR(50),
    price DECIMAL(10,2),
    stock INT DEFAULT 0,
    image_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS merchandise_sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    merchandise_id BIGINT,
    quantity INT,
    total DECIMAL(10,2),
    sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES clients(id),
    FOREIGN KEY (merchandise_id) REFERENCES merchandise(id)
);

CREATE TABLE IF NOT EXISTS incident (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    severity VARCHAR(255) NOT NULL,
    resolved BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
