
-----

# BE Ticket Box - Event Ticketing System Backend

## 1\. Project Introduction

**BE Ticket Box** is the backend system for an online event ticketing application. This project provides a robust set of RESTful APIs to manage the entire business workflow, from managing events, organizers, and tickets to processing orders and integrating multiple payment gateways.

The system is built on the **Java Spring Boot** platform, focusing on security, performance, and scalability, ready to be connected to any frontend interface (Web or Mobile).

## 2\. Key Features

  * **User Management & Authentication:**

      * Registration, Login, and user profile management.
      * Authentication based on **JSON Web Tokens (JWT)**.
      * Support for social login (**OAuth2** - e.g., Google).
      * Account verification via Email.

  * **Event Management:**

      * Create, Read, Update, Delete (CRUD) for events.
      * Manage detailed information: name, description, location, time, images.
      * Event categorization (e.g., Music, Sports, Conferences).

  * **Organizer Management:**

      * Manage information for event organizers.
      * Link events to their respective organizers.

  * **Ticket & Order Management:**

      * Define ticket types (VIP, Standard, etc.) and quantities for each event.
      * Handle business logic for booking tickets and creating orders.
      * Manage order statuses (Pending, Paid, Canceled).

  * **Multi-Channel Payment Integration:**

      * Integrated with domestic payment gateway: **VNPay**.
      * Integrated with international payment gateway: **PayPal**.
      * Handle payment callbacks and automatically update order status after payment.

  * **Email Service:**

      * Automatically send registration confirmation emails.
      * Send email notifications upon successful order and payment (with ticket/QR code attachment - if developed).

## 3\. System Architecture

The project is built following the standard 3-Layer Architecture of Spring Boot, ensuring a clear separation of concerns:

1.  **Controller Layer (API):**

      * Receives HTTP requests (GET, POST, PUT, DELETE) from the client.
      * Uses **Spring Security** to protect endpoints.
      * Calls the `Service Layer` to handle business logic.

2.  **Service Layer (Business Logic):**

      * Contains all the main business logic of the application.
      * Example: `OrderService` handles order creation logic, `PaymentService` communicates with VNPay/PayPal APIs.

3.  **Repository Layer (Data Access):**

      * Uses **Spring Data JPA (Hibernate)** to interact with the database.
      * Abstracts SQL queries, allowing safe and efficient database operations (MySQL).

## 4\. Technology Stack

This project utilizes modern and popular technologies from the Java ecosystem:

  * **Programming Language:** **Java 17** (or 11+)
  * **Main Framework:** **Spring Boot**
  * **Security:** **Spring Security** (JWT Authentication, OAuth2 Social Login)
  * **Data Access:** **Spring Data JPA (Hibernate)**
  * **Database:** **MySQL**
  * **Build & Dependency Management:** **Apache Maven**
  * **API:** RESTful APIs
  * **Payment Integration:**
      * **PayPal** SDK
      * **VNPay** SDK
  * **Email Service:** **Spring Boot Mail Sender** (SMTP)

## 5\. Installation and Setup

### Prerequisites

  * [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) (Version 17)
  * [Apache Maven](https://maven.apache.org/download.cgi) (Version 3.x)
  * [liên kết đáng ngờ đã bị xóa] (Version 8.x)

### Installation Steps

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/ducsieunhan/be_ticket_box.git
    cd be_ticket_box
    ```

2.  **Configure the Database:**

      * Open your MySQL Server and create a new database (e.g., `ticket_box_db`).
      * Open the file `src/main/resources/application.properties`.
      * Update the following properties to connect to your database:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/ticket_box_db?useSSL=false
        spring.datasource.username=your_username
        spring.datasource.password=your_password
        spring.jpa.hibernate.ddl-auto=update
        ```

3.  **Configure External Services (Email, Payment):**

      * Still in the `application.properties` file, update the configuration details for:
          * **Spring Mail (SMTP):** `spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password`.
          * **PayPal:** `paypal.client.id`, `paypal.client.secret`.
          * **VNPay:** `vnpay.tmnCode`, `vnpay.hashSecret`, `vnpay.returnUrl`.

4.  **Build the project (Install dependencies):**

      * Open a terminal in the project's root directory and run:

    <!-- end list -->

    ```bash
    mvn clean install
    ```

5.  **Run the application:**

      * After a successful build, run the following command:

    <!-- end list -->

    ```bash
    mvn spring-boot:run
    ```

      * *Alternatively, run the built JAR file (in the `target` directory):*

    <!-- end list -->

    ```bash
    java -jar target/be_ticket_box-0.0.1-SNAPSHOT.jar
    ```

## 6\. Usage

  * After a successful launch, the API system will be available at (by default): `http://localhost:8080`.
  * You can use API Client tools like [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/) to test the endpoints.
  * **Some key endpoints:**
      * `POST /auth/register`: Create a new account.
      * `POST /auth/login`: Log in (returns a JWT token).
      * `GET /api/v1/events`: Get a list of events (public).
      * `GET /api/v1/events/{id}`: Get event details (public).
      * `POST /api/v1/orders`: Create a new order (requires JWT authentication).
      * `POST /api/v1/payment/vnpay`: Create a payment request via VNPay (requires JWT authentication).

