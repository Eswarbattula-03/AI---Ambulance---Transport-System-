-- ====================================================================
-- AI-POWERED AMBULANCE TRANSPORTATION SYSTEM: SAVING LIVES
-- MySQL DDL Schema Script
-- Version: 1.0.0
-- DB Engine: MySQL 8.0+
-- ====================================================================

CREATE DATABASE IF NOT EXISTS liferescue_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE liferescue_db;

-- --------------------------------------------------------------------
-- 1. USERS TABLE
-- Stores all authentication credentials and roles
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    role ENUM('PATIENT', 'AMBULANCE_DRIVER', 'HOSPITAL_ADMIN', 'SYSTEM_ADMIN') NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 2. PATIENT PROFILES TABLE
-- Extended patient health info
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patient_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    blood_group VARCHAR(10),
    medical_history TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 3. HOSPITALS TABLE
-- Hospital bed capacity and geographic location
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hospitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(11, 7) NOT NULL,
    total_beds INT NOT NULL DEFAULT 0,
    available_beds INT NOT NULL DEFAULT 0,
    icu_beds INT NOT NULL DEFAULT 0,
    available_icu_beds INT NOT NULL DEFAULT 0,
    contact VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_hospitals_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 4. AMBULANCES TABLE
-- Vehicle details, current location, assigned driver
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ambulances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(50) NOT NULL UNIQUE,
    driver_id BIGINT UNIQUE,
    status ENUM('AVAILABLE', 'ASSIGNED', 'MAINTENANCE', 'OFFLINE') NOT NULL DEFAULT 'AVAILABLE',
    current_lat DECIMAL(10, 7),
    current_lng DECIMAL(11, 7),
    last_updated DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ambulance_driver FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_ambulance_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 5. HOSPITAL ADMIN ASSIGNMENTS TABLE
-- Maps hospital administrators to their managed hospital
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hospital_admin_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_admin_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_hospital (user_id, hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 6. EMERGENCY REQUESTS TABLE
-- Core dispatch and triage life cycle entity
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS emergency_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    ambulance_id BIGINT,
    hospital_id BIGINT,
    triage_priority ENUM('CRITICAL', 'HIGH', 'MEDIUM', 'LOW') NOT NULL DEFAULT 'HIGH',
    triage_score DECIMAL(5,2) DEFAULT 0.00,
    triage_explanation TEXT,
    status ENUM(
        'REQUESTED',
        'TRIAGED',
        'DISPATCHED',
        'ACCEPTED',
        'EN_ROUTE_TO_PICKUP',
        'PATIENT_ONBOARD',
        'EN_ROUTE_TO_HOSPITAL',
        'DELIVERED',
        'CANCELLED',
        'FAILED'
    ) NOT NULL DEFAULT 'REQUESTED',
    symptoms TEXT NOT NULL,
    pickup_lat DECIMAL(10, 7) NOT NULL,
    pickup_lng DECIMAL(11, 7) NOT NULL,
    pickup_address VARCHAR(255),
    destination_lat DECIMAL(10, 7),
    destination_lng DECIMAL(11, 7),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    accepted_at DATETIME,
    pickup_at DATETIME,
    delivered_at DATETIME,
    CONSTRAINT fk_emergency_patient FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_emergency_ambulance FOREIGN KEY (ambulance_id) REFERENCES ambulances(id) ON DELETE SET NULL,
    CONSTRAINT fk_emergency_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL,
    INDEX idx_emergency_status (status),
    INDEX idx_emergency_patient (patient_id),
    INDEX idx_emergency_ambulance (ambulance_id),
    INDEX idx_emergency_hospital (hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 7. EMERGENCY STATUS HISTORY TABLE
-- Audit trail of status transitions for every emergency request
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS emergency_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emergency_request_id BIGINT NOT NULL,
    status ENUM(
        'REQUESTED',
        'TRIAGED',
        'DISPATCHED',
        'ACCEPTED',
        'EN_ROUTE_TO_PICKUP',
        'PATIENT_ONBOARD',
        'EN_ROUTE_TO_HOSPITAL',
        'DELIVERED',
        'CANCELLED',
        'FAILED'
    ) NOT NULL,
    updated_by_user_id BIGINT,
    notes VARCHAR(255),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_history_request FOREIGN KEY (emergency_request_id) REFERENCES emergency_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_status_history_user FOREIGN KEY (updated_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_status_history_request (emergency_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 8. AMBULANCE LOCATION HISTORY TABLE
-- High-frequency telemetry coordinates
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ambulance_location_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ambulance_id BIGINT NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(11, 7) NOT NULL,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loc_history_ambulance FOREIGN KEY (ambulance_id) REFERENCES ambulances(id) ON DELETE CASCADE,
    INDEX idx_loc_history_amb_time (ambulance_id, timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 9. PATIENT VITALS TABLE
-- Tele-medicine vital metrics recorded by driver en route
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS patient_vitals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emergency_request_id BIGINT NOT NULL,
    heart_rate INT,
    bp_systolic INT,
    bp_diastolic INT,
    spo2 INT,
    temperature DECIMAL(4,1),
    logged_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vitals_request FOREIGN KEY (emergency_request_id) REFERENCES emergency_requests(id) ON DELETE CASCADE,
    INDEX idx_vitals_request (emergency_request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 10. NOTIFICATIONS TABLE
-- Real-time alerts for patients, drivers, and admins
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notification_user (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 11. REFRESH TOKENS TABLE
-- Session security and token invalidation
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_token_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- 12. AUDIT LOGS TABLE
-- Security compliance and operational audit log
-- --------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------------------
-- SAMPLE SEED DATA FOR DEVELOPMENT (TESTING ONLY)
-- --------------------------------------------------------------------
-- Note: Password for all seed users is: Password123! (BCrypt hashed below)
-- Hash: $2a$10$dQK68rsvkxsbufus/Q95o.AZgsUkmD5WdTtf318AvGXTwRJHPmev6
INSERT INTO users (id, name, email, password_hash, phone, role, enabled) VALUES
(1, 'System Admin', 'admin@liferescue.com', '$2a$10$dQK68rsvkxsbufus/Q95o.AZgsUkmD5WdTtf318AvGXTwRJHPmev6', '+919876543210', 'SYSTEM_ADMIN', TRUE),
(2, 'Dr. Rajesh Sharma', 'hospital.admin@liferescue.com', '$2a$10$dQK68rsvkxsbufus/Q95o.AZgsUkmD5WdTtf318AvGXTwRJHPmev6', '+919876543211', 'HOSPITAL_ADMIN', TRUE),
(3, 'Ramesh Kumar (Driver)', 'driver.ramesh@liferescue.com', '$2a$10$dQK68rsvkxsbufus/Q95o.AZgsUkmD5WdTtf318AvGXTwRJHPmev6', '+919876543212', 'AMBULANCE_DRIVER', TRUE),
(4, 'Suresh Patel (Patient)', 'patient.suresh@liferescue.com', '$2a$10$dQK68rsvkxsbufus/Q95o.AZgsUkmD5WdTtf318AvGXTwRJHPmev6', '+919876543213', 'PATIENT', TRUE);

INSERT INTO hospitals (id, name, latitude, longitude, total_beds, available_beds, icu_beds, available_icu_beds, contact, address) VALUES
(1, 'Apollo Emergency Center', 12.9715987, 77.5945627, 100, 25, 20, 5, '+918022221111', 'MG Road, Bengaluru, Karnataka'),
(2, 'Fortis Care Super Specialty', 12.9279232, 77.6271078, 150, 40, 30, 8, '+918033332222', 'Bannerghatta Road, Bengaluru, Karnataka');

INSERT INTO hospital_admin_assignments (user_id, hospital_id) VALUES
(2, 1);

INSERT INTO ambulances (id, vehicle_number, driver_id, status, current_lat, current_lng, last_updated) VALUES
(1, 'KA-01-EQ-9999', 3, 'AVAILABLE', 12.9650000, 77.6000000, CURRENT_TIMESTAMP);
