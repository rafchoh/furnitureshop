# Furniture Shop - Spring Fundamentals Platform

This repository contains the backend and web-tier implementation of a full-stack e-commerce platform for a furniture shop. Built as a single monolithic architecture, this application demonstrates robust data persistence, dynamic session-based security, and strict MVC design principles. 

## Tech Stack

The application relies on the following modern Java ecosystem technologies:

* **Language:** Java 17
* **Core Framework:** Spring Boot 3.4.0
* **Build Tool:** Maven
* **Database:** MySQL (Relational Database) accessed via Spring Data JPA
* **Frontend:** Spring MVC + Thymeleaf (HTML, CSS, Bootstrap)
* **Security:** Spring Security 6 (BCrypt Password Hashing, Session Management)
* **Boilerplate Reduction:** Lombok

## Domain Entities

The system's data model utilizes `UUID` identifiers for all primary keys and maintains a clean, scalable structure:
1. **Users:** Manages authentication, roles (ADMIN/USER), and profile integrity.
2. **Furniture:** The core inventory entity storing text details, decimal pricing, boolean availability, and `LONGBLOB` byte arrays for image data.
3. **Booking:** The transactional bridge establishing the entity relationship between `Users` and `Furniture`, tracking order status (PENDING, CONFIRMED, CANCELLED) and delivery details.

## Supported Features & Functionalities

The platform implements complex, user-triggered domain functionalities that perform CRUD operations and yield direct visible results on the frontend:

### 1. Administrative Catalog Management (CRUD) 
(***To try out the administrative platform after registration and login go to "Edit User" and change user type while confirming your password!***)

Administrators have exclusive access to a suite of tools to manage the shop's inventory. They can create new furniture listings by uploading images and defining parameters (POST), update existing descriptions, prices, or replace images (PUT/POST), and safely delete items from the database, which cascades to remove any pending cart associations (DELETE).

### 2. Interactive Shopping Cart
Logged-in users can browse the dynamic catalog and add available items to their personal staging cart. This feature creates a `PENDING` booking (POST), recalculates the total cart value dynamically, and allows users to remove items they no longer wish to purchase (DELETE), instantly updating the UI.

### 3. Secure Checkout & Inventory Allocation
Upon proceeding to checkout, users submit delivery parameters via a rigorously validated form. Processing this form transitions all pending cart items into `CONFIRMED` bookings (UPDATE) and simultaneously toggles the availability of the purchased furniture to false (UPDATE), instantly removing it from the public catalog.

### 4. Order History & Cancellation
Users have access to a personalized dashboard to review their historic and active orders. They maintain the ability to cancel an active booking. Executing a cancellation updates the booking status to `CANCELLED` and safely restores the associated furniture item's availability back to the public catalog (UPDATE).

### 5. Profile & Security Management
Features secure session-based authentication with distinct role checks. Users can dynamically update their profile information, including their username, email, and password, utilizing robust server-side validation to ensure data integrity without breaking the active security context.

## Integrations

* **MySQL Engine:** Integrated natively with a MySQL instance, relying on Hibernate's automatic schema updates for seamless development and persistent storage.
* **Spring Session JDBC:** User sessions bypass standard memory storage and are integrated directly into the SQL database (`spring.session.jdbc.initialize-schema=always`). This guarantees high reliability and session persistence across server restarts.
* **Base64 Image Rendering:** The backend dynamically converts raw byte array blobs from the database into Base64 encoded strings, seamlessly integrated with Thymeleaf to render rich images directly to the client's browser without requiring a dedicated external file server.
