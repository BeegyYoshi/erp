echo "Starting ERP Database Setup..."

sudo mariadb < ./src/resources/auth_db.sql -u root -p
sudo mariadb < ./src/resources/erp_db.sql -u root -p
sudo mariadb < ./src/resources/sample_data.sql -u root -p

mvn clean compile
mvn exec:java
