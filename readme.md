# Orderly: E-commerce 🛒 RESTful API

[![Java](https://img.shields.io/badge/Java-21-blue)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)]()
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)]()
[![CI/CD](https://img.shields.io/badge/GitHub_Actions-Active-lightgrey)]()
[![Deployment](https://img.shields.io/badge/Deployed_on-AWS-orange)]()
[![CI/CD](https://github.com/iodsky/orderly/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/iodsky/orderly/actions/workflows/ci-cd.yml)

## 📝 Overview

**Orderly** is a full-featured RESTful API for an e-commerce platform, built with **Spring Boot**.
It demonstrates clean **service-layer architecture**, modern **API design principles**, and production-grade **DevOps integration**.

The API supports complete **CRUD operations** across all major e-commerce entities and features secure **Authentication**, **Authorization**, and **Role-Based Access Control (RBAC)** implemented with **Spring Security** and **JWT**.
All endpoints are fully documented and testable through Swagger/OpenAPI.

From a DevOps perspective, **Orderly** is:
* Containerized with Docker and orchestrated via Docker Compose.
* Backed by GitHub Actions for CI/CD automation.
* Hosted on AWS EC2, with Watchtower enabling automated container updates.
* Integrated with AWS S3 for external image storage.

Databases:
* 🧩 Local development → uses MySQL, managed via Docker Compose
* ☁️ Production → uses PostgreSQL, provisioned through AWS RDS
---


## 🚀 Features

### Core Functionality
* **CRUD** operations for all domain models (Product, Category, User, etc.).
* **Role-Based Access Control (RBAC)** to define user permissions.
* **Shopping Cart Management** (add, remove, update quantity, clear cart, checkout).
* **Order Creation** and tracking.

### Security
* **Spring Security** for securing endpoints.
* **JWT (JSON Web Tokens)** for stateless authentication.
* **Two primary roles**: `ADMIN` and `CUSTOMER`.

| Role | Permissions |
| :--- | :--- |
| **`CUSTOMER`** | Browse products, view categories, manage shopping cart, place orders. |
| **`ADMIN`** | All `CUSTOMER` permissions plus managing products, categories, users, and viewing all orders. |


### DevOps & Cloud
* **Dockerized** application with multi-service setup via **Docker Compose** (Spring, PostgreSQL, Watchtower).
* **GitHub Actions CI/CD** pipeline for:
    * Running unit tests
    * Building Docker images
    * Pushing images to **GitHub Container Registry (GHCR)**
* **Deployed to AWS EC2** using the latest GHCR image.
* Automatic image updates handled by **Watchtower**.

--- 

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-------------|
| **Framework** | Spring Boot |
| **Language** | Java 21 |
| **Database** | PostgreSQL (Local via Docker Compose · Production via AWS RDS) |
| **Cloud Storage** | AWS S3 |
| **Security** | Spring Security, JWT |
| **Containerization** | Docker, Docker Compose |
| **CI/CD** | GitHub Actions |
| **Deployment** | AWS EC2 |
| **Documentation** | Swagger/OpenAPI |
| **Testing** | JUnit, Mockito |
---

## 📦 Domain Models

The API is built around the following essential e-commerce models:

* **`User`**: User details and authentication information.
* **`Role`**: User roles (e.g., `ADMIN`, `CUSTOMER`).
* **`Product`**: Details of an item for sale.
* **`Image`**: Product images (currently stored in the database).
* **`Category`**: Classification for products.
* **`Cart`**: A user's current collection of items before checkout.
* **`CartItem`**: An individual item within a `Cart` with quantity.
* **`Order`**: A confirmed transaction.
* **`OrderItem`**: An individual item within a completed `Order`.

### Entity-Relationship Diagram (ERD)

![](/docs/orderly_erd.png)

---

## 💻 Getting Started

### Prerequisites

* Java 21+
* Maven
* Docker & Docker Compose

### Local Setup (Development)

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/iodsky/orderly.git
    cd orderly
    ```

2.  **Configure the environment:**
    * The application uses three YAML configuration files for different environments:
      * `application.yml` - Base configuration (shared across all environments)
      * `application-local.yml` - Local development configuration
      * `application-prod.yml` - Production development
    * You will also need a `.env` file (see the [Environment Variables](#-3-environment-variables) section below).
    
    **Example**
    ```yaml
    # application.yml
    spring:
      application:
      name: orderly
    
    server:
      port: ${PORT}
      servlet:
      context-path: /api
    
    security:
      jwt:
        secret-key: ${JWT_SECRET_KEY}
        expiration-time: ${JWT_EXPIRATION_TIME}
    
    springdoc:
    api-docs:
      path: /docs
    swagger-ui:
      path: /swagger-ui.html
    
    aws:
      s3:
      region: ${AWS_S3_REGION}
      bucket: ${AWS_S3_BUCKET}
      base-folder: ${AWS_S3_BASE_FOLDER}
    ```
    ```yaml
    # application-local.yml
    spring:
      config:
        activate:
          on-profile: local
    
      datasource:
        url: jdbc:postgresql://${DATASOURCE_HOST}:${DATASOURCE_PORT}/${DATASOURCE_DB}
        username: ${DATASOURCE_USER}
        password: ${DATASOURCE_PASSWORD}
        driver-class-name: org.postgresql.Driver
    
      jpa:
        hibernate:
          ddl-auto: update
        properties:
          hibernate:
            format_sql: true
            show_sql: true
            dialect: org.hibernate.dialect.PostgreSQLDialect
        defer-datasource-initialization: true
    
      sql:
        init:
          mode: always
    
    aws:
      s3:
        access-key: ${AWS_ACCESS_KEY_ID}
        secret-key: ${AWS_SECRET_ACCESS_KEY}
    ```
    
3.  **Run locally:**
    ```bash
    # Start only the development database
    docker compose -f compose.db.yml up -d
    
    # Run the Spring Boot app on the host with the local profile
    ./mvnw spring-boot:run -Dspring.profiles.active=local
    ```

The API will be accessible at `http://localhost:8000/api`.

---

## 🐳 Dockerized Configuration

The project includes **two Docker Compose configurations** for different environments.

### 1. Development database (compose.db.yml)

This setup runs only PostgreSQL for local development. The Spring Boot app runs on the host.

**Usage:**

```bash
docker-compose -f compose.db.yml up -d
```

This will:
* Run PostgreSQL with credentials from DATASOURCE_* variables. 
* Expose PostgreSQL on port 5432.

### 2. Production (compose.prod.yml)

The production configuration is optimized for deployment on AWS EC2 and uses a pre-built Docker image from the GitHub Container Registry (GHCR) instead of rebuilding locally.

**Usage:**

```bash
docker-compose -f compose.prod.yml up -d
```

This will:
* Run the API with the prod profile.
* Connect it to a PostgreSQL database via AWS RDS.
* Uses Watchtower to automatically update the container when a new image is pushed to GHCR.
* Sends update notifications via email.

### 🧾 3. Environment Variables

All configuration values are managed through a .env file (excluded from version control).
An example template is provided in **.env.template**:

```env
# Server
PORT=
SPRING_PROFILES_ACTIVE=local

# JWT
JWT_SECRET_KEY=
JWT_EXPIRATION_TIME=

# Production datasource
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=

# Development datasource (used by application-local.yml and compose.db.yml)
DATASOURCE_HOST=localhost
DATASOURCE_PORT=5432
DATASOURCE_DB=
DATASOURCE_USER=
DATASOURCE_PASSWORD=

# AWS S3
AWS_S3_REGION=
AWS_S3_BUCKET=
AWS_S3_BASE_FOLDER=

# AWS (dev)
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=

# Watchtower
WATCHTOWER_EMAIL_FROM=
WATCHTOWER_EMAIL_TO=
WATCHTOWER_EMAIL_SERVER=
WATCHTOWER_EMAIL_USER=
WATCHTOWER_EMAIL_PASSWORD=
```

**Usage**
1. Copy the template:
    ```bash
    cp .env.template .env
    ```
2. Fill in your values in `.env`.
3. Docker Compose will automatically load these environment variables during startup.

---

## 🔑 API Documentation (Swagger)

Once the application is running, the **Swagger UI** for testing and viewing all endpoints is available at:

[Swagger URL: `http://localhost:8000/api/swagger-ui.html`]

---

## ✅ Tests

To run the unit tests for the Service layer:

```bash
./mvnw test
```

## ⏭️ Future Implementations

The following enhancements are planned to evolve **Orderly** by integrating industry-standard practices and expanding my skill set, making the project a more complete demonstration of modern API architecture:

---

### Phase 1: Environment and Deployment Skills

- [x] **Containerization with Docker:** The immediate next step is to **Dockerize** the Spring Boot application to master containerization and ensure a consistent, easily portable development environment.

- [x] **Production Database Migration (PostgreSQL on AWS RDS):** The production database has been successfully migrated from MySQL to PostgreSQL, hosted on AWS RDS for improved reliability, scalability, and performance.
- [x] **Secure HTTPS Configuration (Traefik):** HTTPS has been fully implemented using Traefik as a reverse proxy and certificate manager, ensuring encrypted and secure production traffic.

### Phase 2: Cloud Services and CI/CD

- [x] **External Image Storage (AWS S3):** Integrating **Amazon Simple Storage Service (S3)** will teach valuable skills in handling external object storage. This practice of decoupling image storage from the database is a core architectural pattern for scalable applications.

- [x] **CI/CD Pipeline & Cloud Deployment (AWS):**
  - [x] Implement a **Continuous Integration/Continuous Deployment (CI/CD)** pipeline using **GitHub Actions**.
  - [x] Deployed the containerized API to an **AWS EC2 instance**, configured to automatically pull and restart the latest image via **Watchtower**.

- [ ] **Monitoring & Observability:** Implement centralized logging and metrics monitoring using tools like Prometheus and Grafana.
