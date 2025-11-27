#!/bin/bash
echo "Starting ERP Database Setup..."
read -sp "Enter MySQL root password: " ROOTPASS
echo ""

mariadb -u root -p"$ROOTPASS" < ./src/resources/auth_db.sql
mariadb -u root -p"$ROOTPASS" < ./src/resources/erp_db.sql
mariadb -u root -p"$ROOTPASS" < ./src/resources/sample_data.sql

mariadb -u root -p"$ROOTPASS" -e "
CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY 'password123';

GRANT ALL PRIVILEGES ON auth_db.* TO 'appuser'@'localhost';
GRANT ALL PRIVILEGES ON erp_db.* TO 'appuser'@'localhost';

FLUSH PRIVILEGES;
"