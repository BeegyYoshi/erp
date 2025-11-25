#!/bin/bash

MYSQL_USER="root"

echo "Refreshing auth_db and erp_db (dropping all tables)..."
read -sp "Enter MySQL root password: " ROOTPASS
echo ""

mariadb -u$MYSQL_USER -p$ROOTPASS <<EOF

-- ================================
-- AUTH DB CLEANUP
-- ================================
USE auth_db;

-- Disable foreign key checks to allow safe deletion
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables
DROP TABLE IF EXISTS users_auth;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================
-- ERP DB CLEANUP
-- ================================
USE erp_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS grade_components;
DROP TABLE IF EXISTS grade_scores;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS sections;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS instructors;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS settings;

SET FOREIGN_KEY_CHECKS = 1;

EOF

echo "Done. All tables deleted. AUTO_INCREMENT reset."
echo "Now run:  ./setup.sh  to recreate tables and insert base data."
