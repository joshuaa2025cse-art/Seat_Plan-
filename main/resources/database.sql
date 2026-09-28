-- ===================================================================
-- SeatPlan - Exam Hall Seating Arrangement Generator
-- MySQL Database Setup Script
-- ===================================================================

CREATE DATABASE IF NOT EXISTS seat_plan 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE seat_plan;

-- 1. Examination Halls Table
CREATE TABLE IF NOT EXISTS halls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hall_number VARCHAR(50) NOT NULL UNIQUE,
    hall_name VARCHAR(150) NOT NULL,
    total_rows INT NOT NULL,
    total_columns INT NOT NULL,
    capacity INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Students Table (with Class / Branch and Section)
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    section VARCHAR(50) NOT NULL DEFAULT 'A',
    semester INT NOT NULL DEFAULT 1,
    email VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3. Exam Sessions Table
CREATE TABLE IF NOT EXISTS exam_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_name VARCHAR(200) NOT NULL,
    subject_code VARCHAR(50) NOT NULL,
    subject_name VARCHAR(150) NOT NULL,
    exam_date DATE NOT NULL,
    session_time VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 4. Allocated Seats Table
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(50) NOT NULL,
    seat_row INT NOT NULL,
    seat_col INT NOT NULL,
    hall_id BIGINT NOT NULL,
    exam_session_id BIGINT,
    student_id BIGINT,
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    remarks VARCHAR(255),
    CONSTRAINT fk_seat_hall FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE,
    CONSTRAINT fk_seat_session FOREIGN KEY (exam_session_id) REFERENCES exam_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_seat_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    CONSTRAINT uq_hall_session_row_col UNIQUE (hall_id, exam_session_id, seat_row, seat_col)
) ENGINE=InnoDB;
