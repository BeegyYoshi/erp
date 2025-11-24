#!/bin/bash
echo "Starting ERP Database Setup..."
read -sp "Enter MySQL root password: " ROOTPASS
echo "ROOTPASS"

sudo mariadb -u root -p"$ROOTPASS" < ./src/resources/auth_db.sql
sudo mariadb -u root -p"$ROOTPASS" < ./src/resources/erp_db.sql
sudo mariadb -u root -p"$ROOTPASS" < ./src/resources/sample_data.sql
