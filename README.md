# MIF API

## 1. Introduction

This project is a Spring Boot application that provides a RESTful API for managing movies, actors, groups, and news. It uses MongoDB as the database and includes JWT-based authentication.

### 1.1. Technology used:

- **Java**: The primary programming language used for the application.
- **Spring Boot**: A framework for building Java-based applications, used for creating the RESTful API.
- **MongoDB**: A NoSQL database used for storing data.
- **JWT (JSON Web Tokens)**: Used for authentication and authorization.
- **Maven**: A build automation tool used for managing project dependencies and building the project.
- **Lombok**: A library used to reduce boilerplate code in Java.
- **Mapstruct**: A library used for object mapping.
- **Spring Security**: A framework used for securing the application.
- **Spring Data MongoDB**: A library used for integrating MongoDB with Spring Boot.
- **Spring Web**: A library used for building web applications and RESTful services.
- **Springdoc OpenAPI**: A library used for generating API documentation.
- **Spring WebSocket**: Used for real-time communication features.
- **Spring AMQP**: Used for message queuing with RabbitMQ.
- **Spring Session**: Used for session management.
- **Spring Mail**: Used for sending emails.
- **Spring Cache**: Used for caching functionality.
- **Spring Data Redis**: Used for Redis integration.
- **Quartz**: Used for scheduling jobs.
- **Passay**: Used for password validation.
- **AWS SDK**: Used for AWS services integration (S3, Comprehend).
- **JSON Web Token (JWT)**: Used for token-based authentication.
- **Spring OAuth2 Client**: Used for OAuth2 authentication.

## 2. Setup

### 2.1. Prerequisites

- Java 17 or higher
- Maven
- MongoDB

### 2.2. Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/badb0y4life/mif_api.git
   cd mif_api
   ```

2. Configure the database settings in `src/main/resources/application.yml`:

   ```yaml
   spring:
     data:
       mongodb:
         authentication-database: admin
         database: movieInsideForum
         host: localhost
         port: 27017
         username: admin
         password: admin
   ```

3. Build the project:

   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## 3. Project Structure

### 3.1. Directory Structure

```
src/main/java/com/mif/movieInsideForum/
├── Annotation/          # Custom annotations
├── Collection/          # MongoDB collections
├── Config/             # Configuration classes
├── Controller/         # REST API controllers
├── DTO/               # Data Transfer Objects
├── Exception/         # Custom exception handling
├── Filter/            # Request/Response filters
├── Job/               # Scheduled jobs
├── Mapper/            # Object mapping utilities
├── Messaging/         # Message handling
├── ModelMapperUtil/   # ModelMapper configuration
├── Module/            # Business logic modules
│   ├── Movie/         # Movie-related functionality
│   └── ...           # Other modules
├── Property/          # Property configurations
├── Queue/             # Queue processing
├── Security/          # Security configurations
├── Util/              # Utility classes
└── Validation/        # Custom validators
```

### 3.2. Key Components

- **Annotation/**: Chứa các annotation tùy chỉnh được sử dụng trong dự án
- **Collection/**: Định nghĩa các MongoDB collections và entities
- **Config/**: Các lớp cấu hình Spring Boot
- **Controller/**: Các REST API endpoints
- **DTO/**: Các đối tượng chuyển đổi dữ liệu
- **Module/**: Chứa các module chính của ứng dụng, mỗi module có thể có:
  - Service layer
  - Repository layer
  - Entity classes
  - DTOs
  - Controllers
