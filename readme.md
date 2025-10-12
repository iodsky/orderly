# Orderly: E-commerce 🛒 RESTful API

[![Java](https://img.shields.io/badge/Java-21-blue)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)]()
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)]()
[![CI/CD](https://img.shields.io/badge/GitHub_Actions-Active-lightgrey)]()
[![Deployment](https://img.shields.io/badge/Deployed_on-AWS-orange)]()
[![CI/CD](https://github.com/iodsky/orderly/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/iodsky/orderly/actions/workflows/ci-cd.yml)

## 📝 Overview

**Orderly** is a full-featured **RESTful API** for an e-commerce platform, built with **Spring Boot** and **MySQL**.  
It showcases modern **API design principles**, clean **service-layer architecture**, and production-ready **DevOps integration**.

The API provides complete **CRUD operations** across all core e-commerce entities and implements secure **Authentication, Authorization, and Role-Based Access Control (RBAC)** using **Spring Security** and **JWT**.  
All endpoints are documented and testable through **Swagger/OpenAPI**.

From a DevOps standpoint, **Orderly** is **containerized with Docker**, orchestrated via **Docker Compose**, and continuously integrated and deployed through a **GitHub Actions CI/CD pipeline**.  
It is hosted on **AWS EC2**, with automated image updates managed by **Watchtower**, and leverages **AWS S3** for external image storage.

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
* **Dockerized** application with multi-service setup via **Docker Compose** (Spring, MySQL, Watchtower).
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
| **Database** | MySQL (to be migrated to AWS RDS or Supabase) |
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
    * Create a MySQL database named `orderly_db`.
    * The application uses **`application.yml`** for configuration. Ensure your database credentials match the following structure in your `application.yml`:

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/orderly # <-- Use your database URL
        username: [YOUR_DB_USERNAME]
        password: [YOUR_DB_PASSWORD]
    # ...
    security:
      jwt:
        secret-key: [A_SECURE_SECRET_KEY_FOR_JWT] # <-- Generate a strong, unique key
        expiration-time: 3600000
    ```

3.  **Run locally:**
    ```bash
    ./mvnw spring-boot:run
    ```

The API will be accessible at `http://localhost:8000/api`.

---

## 🐳 Dockerized Configuration

The project uses **two Docker Compose configurations** for development and product environments.

### 1. Development (compose.yml)

This setup is ideal for **local development**, allowing you to rebuild and iterate on the code quickly.

**Usage:**

```bash
docker-compose up --build -d
```

This will:
* Build your local Spring Boot image.
* Run MySQL in a linked container.
* Expose the app on port 8000.

### 2. Production (compose.prod.yml)

The production configuration is optimized for deployment on AWS EC2 and uses a pre-built Docker image from the GitHub Container Registry (GHCR) instead of rebuilding locally.

**Usage:**

```bash
docker-compose -f compose.prod.yml up -d
```

This will:
* Connects it to a MySQL container.
* Uses Watchtower to automatically update the container when a new image is pushed to GHCR.
* Sends update notifications via email.

### 🧾 3. Environment Variables

Your configuration values are managed via a .env file (excluded from version control).
An example template is provided:

```env
# .env.template

MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=
MYSQL_PASSWORD=

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
2. Fill in your values in .env.
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

- [ ] **Database Migration to Supabase or AWS RDS:** Next step is to migrate the database from **MySQL** to **AWS RDS** or **Supabase**, enabling managed cloud persistence and improved scalability.

### Phase 2: Cloud Services and CI/CD

- [x] **External Image Storage (AWS S3):** Integrating **Amazon Simple Storage Service (S3)** will teach valuable skills in handling external object storage. This practice of decoupling image storage from the database is a core architectural pattern for scalable applications.

- [x] **CI/CD Pipeline & Cloud Deployment (AWS):**
  - [x] Implement a **Continuous Integration/Continuous Deployment (CI/CD)** pipeline using **GitHub Actions**.
  - [x] Deployed the containerized API to an **AWS EC2 instance**, configured to automatically pull and restart the latest image via **Watchtower**.
  - [ ] Implement **HTTPS** to secure production traffic.