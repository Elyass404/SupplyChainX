-- This script creates a new user (role) and a dedicated database for SonarQube.

-- 1. Create the dedicated user for SonarQube.
-- IMPORTANT: The password ('sonar_password_secret') MUST match the SONAR_JDBC_PASSWORD
-- in the SonarQube service configuration in docker-compose.yml.
CREATE ROLE sonar_user WITH LOGIN PASSWORD 'sonar_password_secret';

-- 2. Create the dedicated database for SonarQube and assign ownership to the new user.
CREATE DATABASE sonarqube_db WITH OWNER sonar_user;
GRANT ALL PRIVILEGES ON DATABASE sonarqube_db TO sonar_user;