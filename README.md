# BarberFind API

REST API for BarberFind — a barbershop marketplace platform built with Spring Boot and PostgreSQL.

---

## 📌 Overview

BarberFind is a marketplace platform that connects users with barbershops and barbers.

This API provides:

- 🔐 JWT Authentication (Spring Security)
- 👤 User management
- 💈 Barbershops & Barbers management
- 📅 Appointment scheduling
- ⭐ Reviews & ratings
- ❤️ Favorites
- 💳 Subscription control
- 📊 Activity logging

---

## 🏗 Architecture

This project follows **Clean Architecture with layered structure**:

### Layers

- **Controller** → REST endpoints
- **Service** → Business rules
- **Repository** → Data access (Spring Data JPA)
- **Entity** → Domain models
- **DTO** → Request/Response contracts
- **Exception Handler** → Centralized error handling
- **Security** → JWT-based authentication

---

## 🛠 Tech Stack

- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL
- SpringDoc OpenAPI (Swagger)
- Maven

---

## ⚙️ Environment Configuration

Create a `.env` file in the root directory:
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

DB_HOST=localhost
DB_PORT=5432
DB_NAME=barberdb
DB_USER=postgres
DB_PASSWORD=postgres

JPA_DDL_AUTO=update
JPA_SHOW_SQL=true

SWAGGER_ENABLED=true

JWT_SECRET=dev-secret-super-simple-change-later
JWT_EXPIRES_MINUTES=60

CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:19006


Use `.env.example` as a template.

> ⚠️ Never commit your real `.env` file.

---

## 🗄 Database

Make sure PostgreSQL is running and create the database:

```sql
CREATE DATABASE barberdb;
```

🚀 Running the Project
```mvn clean install
mvn spring-boot:run
```

API will run at:
```
http://localhost:8080
```

API Documentation (Swagger)

After starting the application:

http://localhost:8080/swagger-ui/index.html

OpenAPI JSON:

http://localhost:8080/v3/api-docs

Authentication

The API uses JWT (JSON Web Token).

Authentication flow:

POST /auth/login

Receive JWT token

Send token in header:

Authorization: Bearer <your_token>

📂 Project Structure
src/main/java/com/barberfind/api
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── security
└── exception

🧪 Testing

mvn test
