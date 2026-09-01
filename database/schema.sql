-- Vahana: Vehicle Tracking System schema
-- Note: with spring.jpa.hibernate.ddl-auto=update, Hibernate will create these
-- tables automatically on first run. This file is here so the schema is
-- reviewable/runnable independent of the app, and so the index is explicit.

CREATE DATABASE IF NOT EXISTS vahana_db;
USE vahana_db;

CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(20) NOT NULL UNIQUE,
    driver_name VARCHAR(100) NOT NULL,
    vehicle_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE'
);

CREATE TABLE IF NOT EXISTS location_pings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    timestamp DATETIME(6) NOT NULL,
    speed DOUBLE,
    CONSTRAINT fk_location_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Composite index: this is what makes "get recent history for vehicle X"
-- fast instead of a full table scan as location_pings grows.
CREATE INDEX idx_vehicle_timestamp ON location_pings (vehicle_id, timestamp);

-- Seed a few vehicles so the app isn't empty on first run.
INSERT INTO vehicles (registration_number, driver_name, vehicle_type, status) VALUES
    ('MH12AB1234', 'Ramesh Kulkarni', 'TRUCK', 'OFFLINE'),
    ('MH14CD5678', 'Sunita Pawar', 'VAN', 'OFFLINE'),
    ('MH04EF9012', 'Amit Deshmukh', 'CAR', 'OFFLINE')
ON DUPLICATE KEY UPDATE registration_number = registration_number;
