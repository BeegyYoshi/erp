CREATE DATABASE IF NOT EXISTS erp_db;
USE erp_db;

-- STUDENTS TABLE
CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(50),
    year INT,
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id)
);

-- INSTRUCTORS TABLE
CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,
    department VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id)
);

-- COURSES TABLE
CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    credits INT NOT NULL
);

-- SECTIONS TABLE
CREATE TABLE IF NOT EXISTS sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    instructor_id INT,
    day_time VARCHAR(50),
    room VARCHAR(50),
    capacity INT CHECK (capacity >= 0),
    semester VARCHAR(20),
    year INT,
    UNIQUE (course_id, instructor_id, semester, year),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id)
);

-- ENROLLMENTS TABLE
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status ENUM('enrolled', 'dropped') DEFAULT 'enrolled',
    UNIQUE KEY uq_student_section (student_id, section_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
);

-- GRADES TABLE
CREATE TABLE IF NOT EXISTS grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    component VARCHAR(50),   -- quiz, midterm, endsem etc.
    score DOUBLE,
    final_grade DOUBLE,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

-- GLOBAL SETTINGS TABLE (Maintenance Mode lives here)
CREATE TABLE IF NOT EXISTS settings (
    `key` VARCHAR(50) PRIMARY KEY,
    `value` VARCHAR(50)
);

-- Default: maintenance OFF
INSERT INTO settings(`key`, `value`)
VALUES ('maintenance', 'false')
ON DUPLICATE KEY UPDATE value=value;
