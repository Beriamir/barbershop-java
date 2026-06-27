CREATE DATABASE IF NOT EXISTS barbershop_db;
USE barbershop_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'BARBER', 'ADMIN', 'ANONYMOUS') NOT NULL DEFAULT 'CUSTOMER'
);

CREATE TABLE IF NOT EXISTS services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration INT NOT NULL -- minutes
);

CREATE TABLE IF NOT EXISTS barbers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT DEFAULT NULL,
    barber_id INT NOT NULL,
    service_id INT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    status ENUM('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    walk_in BOOLEAN NOT NULL DEFAULT FALSE,
    walk_in_name VARCHAR(100) DEFAULT NULL,
    FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (barber_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- Seed data
INSERT INTO services (name, price, duration) 
SELECT * FROM (SELECT 'Haircut' AS name, 15.00 AS price, 30 AS duration) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name='Haircut') LIMIT 1;

INSERT INTO services (name, price, duration) 
SELECT * FROM (SELECT 'Beard Trim' AS name, 10.00 AS price, 15 AS duration) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name='Beard Trim') LIMIT 1;

INSERT INTO services (name, price, duration) 
SELECT * FROM (SELECT 'Hair + Beard Combo' AS name, 22.00 AS price, 45 AS duration) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name='Hair + Beard Combo') LIMIT 1;
