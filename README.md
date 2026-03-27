# RevConnect-P2

RevConnect is a full-stack monolithic social networking web application built for personal users, creators, and businesses to connect, share content, grow their audience, and manage their professional presence.

---

## Core Features

* Secure authentication and authorization
* Role-based access control
* Profile creation and management
* Posts, likes, comments, shares, and reposts
* Follow and connection system
* Notification system with preferences
* Search users, posts, and hashtags
* Creator and business analytics
* Product and service showcase

---

## Architecture Overview

RevConnect follows a monolithic full-stack architecture:

```
Frontend Layer     -> Angular 17 SPA
Backend Layer      -> Spring Boot REST API
Service Layer      -> Business Logic
Repository Layer   -> JPA/Hibernate
Database Layer     -> MySQL
Security Layer     -> JWT + Spring Security
```

---

## Application Layers

* Frontend Layer – Angular UI with modules, routing, guards, and services
* Controller Layer – REST endpoints handling requests
* Service Layer – Business logic and validations
* Repository Layer – Data access using JPA/Hibernate
* Security Layer – JWT authentication and authorization
* Database Layer – MySQL relational schema

---

## Functional Features

### Authentication and Profile

* User registration (Personal, Creator, Business)
* JWT-based login
* BCrypt password encryption
* Profile creation and editing
* Public and private profiles
* User search

---

### Post Management

* Create, edit, and delete posts
* Like, comment, and share posts
* Hashtag support and trending
* Personalized feed
* Promotional posts with CTA

---

### Social Networking

* Follow and unfollow users
* Send and manage connection requests
* View followers and connections

---

### Notification System

* Notifications for likes, comments, follows, and connections
* Mark notifications as read/unread
* Notification preferences

---

### Creator and Business Features

* Product and service showcase
* Post analytics
* Follower insights

---

## Project Structure

```
RevConnect-P2/
│
├── Backend/        # Spring Boot backend
├── revconnect/     # Angular frontend
└── README.md
```

---

## Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring Security
* JWT Authentication
* Hibernate / JPA
* Maven

### Frontend

* Angular 17
* TypeScript
* RxJS
* Angular Router

### Database

* MySQL

---

## Setup Instructions

### 1. Clone Repository

```
git clone https://github.com/vishalbhure611/RevConnect-P2.git
cd RevConnect-P2
```

---

### 2. Backend Setup

Edit:

```
Backend/src/main/resources/application.properties
```

Example:

```
spring.datasource.url=jdbc:mysql://localhost:3306/RevConnectP2
spring.datasource.username=root
spring.datasource.password=yourpassword

jwt.secret=your-secret
```

Run backend:

```
cd Backend
mvn spring-boot:run
```

Backend URL:

```
http://localhost:8080
```

---

### 3. Frontend Setup

```
cd revconnect
npm install
npm start
```

Frontend URL:

```
http://localhost:4200
```

---

## Application Flow

* Angular sends API requests
* JWT is attached via interceptor
* Spring Security validates requests
* Controllers → Services → Repository
* MySQL stores data
* Response sent back to frontend

---

## Key Highlights

* Full-stack application (Angular + Spring Boot)
* Clean layered architecture
* Secure authentication system
* Real-world social networking features
* Scalable and modular design

---

## Future Improvements

* Real-time notifications (WebSockets)
* Cloud media storage
* Recommendation system
* Docker deployment

---

## Author

Vishal Bhure

