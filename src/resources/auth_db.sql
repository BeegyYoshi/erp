CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    role ENUM('student', 'instructor', 'admin') NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    last_login DATETIME
);

-- Insert Sample Admin User (replace HASH_HERE with bcrypt hash)
INSERT INTO users_auth(username, role, password_hash)
VALUES ("student1", "student", "$2a$10$ErhUrfb04gDiLz/SGm67rOih5LSx9jbZf8qIqt3ZdZhtNFpQlAvpW"), -- password: admin123
        ("instructor1", "instructor", "$2a$10$ErhUrfb04gDiLz/SGm67rOih5LSx9jbZf8qIqt3ZdZhtNFpQlAvpW"), -- password: admin123
        ("admin1", "admin", "$2a$10$ErhUrfb04gDiLz/SGm67rOih5LSx9jbZf8qIqt3ZdZhtNFpQlAvpW"), -- password: admin123
        ("student2", "student", "$2a$10$ErhUrfb04gDiLz/SGm67rOih5LSx9jbZf8qIqt3ZdZhtNFpQlAvpW") -- password: admin123
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash);