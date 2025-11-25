#!/bin/bash
echo "Starting ERP Database Setup..."
read -sp "Enter MySQL root password: " ROOTPASS
echo ""

mariadb -u root -p"$ROOTPASS" < ./src/resources/auth_db.sql
mariadb -u root -p"$ROOTPASS" < ./src/resources/erp_db.sql
mariadb -u root -p"$ROOTPASS" < ./src/resources/sample_data.sql
