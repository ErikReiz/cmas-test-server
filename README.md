# CMAS Test Server

This repository contains the backend server application for the CMAS (Clinical Measurement Aggregation System) project. It is built using Spring Boot.

## Overview

The application manages interactions between Doctors, Patients, and Administrators for assigning and evaluating CMAS tests. It features role-based access control (Patient, Doctor) and JWT-based authentication.

## Core Technologies

* **Framework:** Spring Boot 3.5
* **Language:** Java 21
* **Build Tool:** Maven 
* **Database:** MySQL 
* **Database Migrations:** Flyway
* **Authentication:** Spring Security, JWT
* **Data Mapping:** MapStruct
* **Testing:** JUnit Jupiter, Testcontainers (MySQL)

## API Documentation

The server exposes RESTful APIs for various functionalities. For details on specific endpoints, please refer to the code controllers:

The **API login endpoint documentation** can be found at: https://chatgpt.com/canvas/shared/67f19df4255881919d1cd1103b7df81a
The Server is hosted in Azure at: https://cmas-test-server-amauh3bjc4cug8gs.northeurope-01.azurewebsites.net
