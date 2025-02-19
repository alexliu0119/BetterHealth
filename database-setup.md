# Database Setup Guide

This document will guide you through the process of setting up and using the database required for this project.

## Prerequisites
1. Install MySQL or MariaDB.
2. Make sure your database server is up and running.

## Importing the Database

1. Download the `database.sql` file.
2. Create a new database named `Healthy_data` and import the SQL data using the following command:

   ```bash
   mysql -u <username> -p Healthy_data < database.sql
-------------------
Java Configuration
In order to connect your Java application to the Healthy_data database, you need to configure your application.properties file.

Open your application.properties file located in the src/main/resources directory.

Update the following properties to match your database setup

spring.datasource.url=jdbc:mysql://localhost:3306/Healthy_data
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
