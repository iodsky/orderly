# Orderly: E-commerce 🛒 RESTful API

## 📝 Overview

**Orderly** is a foundational **RESTful API** for an e-commerce platform. It's built using **Spring Boot** and **MySQL**, designed to demonstrate the core principles and best practices of modern API development.

It features complete **CRUD** functionalities for all domain models and robust **Authentication, Authorization, and Role-Based Access Control (RBAC)** implemented with **Spring Security** and **JSON Web Tokens (JWT)**. API documentation is provided using **Swagger/OpenAPI**.

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

### Technical Details
* **API Documentation** using **Swagger UI**.
* Comprehensive **Unit Tests** for all Service layers.

---

## 🛠️ Technology Stack

* **Framework**: Spring Boot
* **Language**: Java
* **Database**: MySQL
* **Security**: Spring Security, JWT
* **Documentation**: Swagger/OpenAPI
* **Testing**: JUnit, Mockito

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
* MySQL Database Server

### Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/iodsky/orderly.git
    cd orderly
    ```

2.  **Configure the Database:**
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

3.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```

The API will be accessible at `http://localhost:8080/api`.

---

## 🔑 API Documentation (Swagger)

Once the application is running, the **Swagger UI** for testing and viewing all endpoints is available at:

[Swagger URL: `http://localhost:8080/api/swagger-ui.html`]

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

- [ ] **Containerization with Docker:** The immediate next step is to **Dockerize** the Spring Boot application to master containerization and ensure a consistent, easily portable development environment.

- [ ] **Database Migration to PostgreSQL:** Transitioning the persistence layer from MySQL to **PostgreSQL** is a great way to learn to manage different relational databases. This will be configured using **Docker Compose** to manage both the application and the database services together.

### Phase 2: Cloud Services and CI/CD

- [ ] **External Image Storage (AWS S3):** Integrating **Amazon Simple Storage Service (S3)** will teach valuable skills in handling external object storage. This practice of decoupling image storage from the database is a core architectural pattern for scalable applications.

- [ ] **CI/CD Pipeline & Cloud Deployment (AWS):**
    - [ ] Implement a **Continuous Integration/Continuous Deployment (CI/CD)** pipeline using **GitHub Actions**.
    - [ ] Deploy the containerized API to an appropriate AWS service to complete the full cycle of development, deployment, and cloud operations.