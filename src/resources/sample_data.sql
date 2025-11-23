USE auth_db;

-- Sample Instructor
INSERT IGNORE INTO users_auth(username, role, password_hash)
VALUES ("inst1", "instructor", "HASH_HERE");

-- Sample Students
INSERT IGNORE INTO users_auth(username, role, password_hash)
VALUES ("stu1", "student", "HASH_HERE"),
       ("stu2", "student", "HASH_HERE");

USE erp_db;

-- Link ERP profiles to Auth DB
INSERT IGNORE INTO instructors(user_id, department)
VALUES (2, "CSE");  -- user_id = 2 is inst1

INSERT IGNORE INTO students(user_id, roll_no, program, year)
VALUES
(3, "2023STU1", "BTech CSE", 1),
(4, "2023STU2", "BTech CSE", 1);

-- Courses
INSERT IGNORE INTO courses(code, title, credits)
VALUES
("CS101", "Intro to CS", 4),
("CS102", "Data Structures", 4);

-- Sections
INSERT INTO sections(course_id, instructor_id, day_time, room, capacity, semester, year)
VALUES
(1, 2, "Mon 10-12", "A101", 40, "Fall", 2024),
(2, 2, "Wed 2-4", "B202", 40, "Fall", 2024)
ON DUPLICATE KEY UPDATE capacity = VALUES(capacity);

-- Example enrollment
INSERT IGNORE INTO enrollments(student_id, section_id)
VALUES (3, 1);  -- stu1 enrolled in CS101 section
