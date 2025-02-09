
# Trading Submissions Application

This is a Spring Boot microservice designed to handle trade requests. It exposes REST APIs for submitting buy/sell trade requests, retrieving trade statuses, and fetching trade details. The service integrates with an external broker to execute trades asynchronously and handles timeouts for broker responses.

---

## 🛠 Tech Stack

### 1. Java 17
### 2. Spring Boot: For building the RESTful microservice.
### 3. Maven: For building the JAR artifact.
### 4. H2 Database: Embedded in-memory database for storing trade details.
### 5. Docker: For containerizing the application.
### 6. Lombok: For reducing boilerplate code.
### 7. CompletableFuture: For handling asynchronous execution and timeouts.

---

## 🚀 Setup & Run

### JAR Artifact Startup
#### a. Clone
- Clone the repository from [here](https://github.com/tooulakis13/trading-app).
```
  git clone https://github.com/tooulakis13/trading-app
  ```
#### b. Build
- Build the JAR artifact using Maven:
  ```
  mvn clean install
  ```
#### c. Run
- Run the JAR file:
  ```
  docker compose build
  docker compose up
  ```

---

## 📚 API Swagger Documentation
### You can find all APIs and their documentation [here](http://localhost:8080/swagger-ui/index.html).

---

## 📧 Contact
For any queries or issues, please reach out via GitHub or email.

---
