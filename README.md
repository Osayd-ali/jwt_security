# 🔐 SecuringRestfulAPIs--JWTAuthentication-and-RoleBasedAuthorisation

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2-green)
![JWT](https://img.shields.io/badge/JWT-0.12.3-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-red)

> Enterprise-grade RESTful API security implementation featuring JWT-based stateless authentication, database-backed user management, and comprehensive role-based access control (RBAC).

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Database Configuration](#-database-configuration)
- [Running the Application](#-running-the-application)
- [API Endpoints](#-api-endpoints)
- [Authentication Flow](#-authentication-flow)
- [Component Architecture](#-component-architecture)
- [Security Features](#-security-features)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)

---

## 🌟 Overview

This project demonstrates a production-ready implementation of secure RESTful APIs using **JWT (JSON Web Tokens)** for stateless authentication and **Role-Based Access Control (RBAC)** for fine-grained authorization. The system eliminates server-side session management, making it highly scalable and suitable for microservices architectures.

### Key Highlights

- ✅ **Stateless Authentication** - No server-side sessions, fully scalable
- ✅ **Database-Backed Users** - MySQL/PostgreSQL with JPA/Hibernate
- ✅ **Role-Based Authorization** - ADMIN, USER, MANAGER roles
- ✅ **BCrypt Password Encryption** - Industry-standard password hashing
- ✅ **Rate Limiting** - Protection against brute-force attacks
- ✅ **JWT Token Management** - Secure token generation and validation
- ✅ **Multi-Layer Security** - URL-based and method-based access control
- ✅ **Professional Error Handling** - Custom 401/403 JSON responses
- ✅ **Clean Architecture** - Layered design with separation of concerns

---

## ✨ Features

### Authentication
- JWT-based stateless authentication
- BCrypt password hashing (adaptive cost factor)
- Secure token generation with embedded roles
- Token expiration and validation
- Custom authentication entry point

### Authorization
- Role-Based Access Control (RBAC)
- Three-tier role hierarchy: ADMIN, MANAGER, USER
- URL-based authorization rules
- Method-level security with `@PreAuthorize`
- Custom access denied handler

### Security
- Global rate limiting (100 requests/minute per IP)
- HMAC-SHA256 token signing
- Protection against common attacks (CSRF not needed for JWT)
- Secure password storage
- Token-based request validation

### API Features
- RESTful design principles
- JSON request/response format
- Comprehensive error handling
- Standardized error responses
- Public and protected endpoints

---

## 🏗️ System Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│  (Browser / Mobile App / API Client / Postman)                  │
│                                                                  │
│  1. Send credentials → Receive JWT token                        │
│  2. Store token (localStorage / secure storage)                 │
│  3. Include token in Authorization header for protected routes   │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP/HTTPS
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SECURITY FILTER CHAIN                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  1. GlobalRateLimiterFilter                            │    │
│  │     - Tracks requests per IP                           │    │
│  │     - Blocks if > 100 req/min                          │    │
│  │     - Returns 429 if rate exceeded                     │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  2. JwtAuthenticationFilter                            │    │
│  │     - Extract JWT from Authorization header            │    │
│  │     - Validate token signature                         │    │
│  │     - Extract username and roles from token            │    │
│  │     - Load UserDetails from database                   │    │
│  │     - Set SecurityContext with authorities             │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  3. Spring Security Authorization                      │    │
│  │     - Check URL-based rules                            │    │
│  │     - Verify @PreAuthorize annotations                 │    │
│  │     - Grant or deny access                             │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  AssignmentTestController                              │    │
│  │  - POST   /api/login                                   │    │
│  │  - GET    /api/user/useronly       [@PreAuthorize]    │    │
│  │  - GET    /api/admin/adminonly     [@PreAuthorize]    │    │
│  │  - GET    /api/manager/manageronly [@PreAuthorize]    │    │
│  │  - GET    /api/common/allok        [@PreAuthorize]    │    │
│  │  - GET    /api/public              [Public]           │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  AuthServiceImpl                                       │    │
│  │  - authenticateAndGenerateToken()                      │    │
│  │  - Handle login logic                                  │    │
│  │  - Generate JWT with embedded roles                    │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  CustomUserDetailsServiceImpl                          │    │
│  │  - loadUserByUsername()                                │    │
│  │  - Query database for user + roles                     │    │
│  │  - Convert to Spring Security UserDetails              │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  JwtUtil                                               │    │
│  │  - generateToken()    - Create signed JWT              │    │
│  │  - validateToken()    - Verify signature & expiry      │    │
│  │  - extractUsername()  - Parse token claims             │    │
│  │  - extractRoles()     - Get authorities from token     │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                     PERSISTENCE LAYER                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  UserRepository extends JpaRepository                  │    │
│  │  - findByUsername(String username)                     │    │
│  │  - Custom query methods                                │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  RoleRepository extends JpaRepository                  │    │
│  │  - findByName(String name)                             │    │
│  │  - Role management queries                             │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                        DATABASE LAYER                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────┐        ┌──────────────┐        ┌──────────┐     │
│   │  users   │        │  user_roles  │        │  roles   │     │
│   ├──────────┤        ├──────────────┤        ├──────────┤     │
│   │ id (PK)  │◄──────►│ user_id (FK) │        │ id (PK)  │     │
│   │ username │        │ role_id (FK) │◄──────►│ name     │     │
│   │ password │        └──────────────┘        └──────────┘     │
│   └──────────┘                                                  │
│                                                                  │
│   Many-to-Many Relationship: User ←→ Role                       │
│   FetchType: EAGER (roles loaded with user)                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

### Backend Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Core programming language |
| **Spring Boot** | 3.2.x | Application framework |
| **Spring Security** | 6.2.x | Security framework |
| **Spring Data JPA** | 3.2.x | Data persistence |
| **Hibernate** | 6.4.x | ORM implementation |

### Security & Authentication
| Technology | Version | Purpose |
|------------|---------|---------|
| **JJWT (Java JWT)** | 0.12.3 | JWT token creation & validation |
| **BCrypt** | (Spring Security) | Password hashing algorithm |

### Database
| Technology | Version | Purpose |
|------------|---------|---------|
| **MySQL** | 8.0+ | Primary database (or PostgreSQL) |
| **H2 Database** | (Optional) | In-memory testing |

### Build Tools
| Technology | Version | Purpose |
|------------|---------|---------|
| **Maven** | 3.9.x | Dependency management & build |

### Development Tools
| Tool | Purpose |
|------|---------|
| **IntelliJ IDEA / Eclipse** | IDE |
| **Postman / Insomnia** | API testing |
| **MySQL Workbench** | Database management |
| **Git** | Version control |

---

## 📦 Prerequisites

Before running this application, ensure you have the following installed:

### Required
- ✅ **Java 17** or higher
```bash
  java -version
  # Should output: java version "17.x.x"
```

- ✅ **Maven 3.9+**
```bash
  mvn -version
  # Should output: Apache Maven 3.9.x
```

- ✅ **MySQL 8.0+** (or PostgreSQL 13+)
```bash
  mysql --version
  # Should output: mysql Ver 8.0.x
```

### Recommended
- 🔧 **Git** - For cloning the repository
- 🔧 **Postman** - For testing API endpoints
- 🔧 **IntelliJ IDEA** - IDE with Spring Boot support

---

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/SecuringRestfulAPIs--JWTAuthentication-and-RoleBasedAuthorisation.git
cd SecuringRestfulAPIs--JWTAuthentication-and-RoleBasedAuthorisation
```

### 2. Configure Database

Create a MySQL database:
```sql
CREATE DATABASE jwt_security_db;
```

### 3. Update `application.properties`

Edit `src/main/resources/application.properties`:
```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_security_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=YOUR_BASE64_ENCODED_SECRET_KEY_HERE
jwt.expirationMs=3600000

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.edu.ali.jwt_security=DEBUG
```

### 4. Generate JWT Secret Key

Run the `JwtSecretGenerator` utility:
```bash
# Compile and run
mvn compile exec:java -Dexec.mainClass="edu.ali.jwt_security.utils.JwtSecretGenerator"

# Copy the generated Base64 key and paste it in application.properties
```

**Example output:**
```
Generated Base64-encoded secret key:
3cfa76ef9db97e0cfadc2b1e4a8b3c7f2a1e5d9c8b7a6f5e4d3c2b1a0f9e8d7c
```

### 5. Build the Project
```bash
mvn clean install
```

### 6. Initialize Database with Sample Data

Create a data initialization file `src/main/resources/data.sql`:
```sql
-- Insert roles
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('MANAGER');

-- Insert sample users (password: "password123" for all)
INSERT INTO users (username, password) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1),  -- admin has ADMIN
(1, 2),  -- admin has USER
(2, 2),  -- user has USER
(3, 3);  -- manager has MANAGER
```

Add to `application.properties`:
```properties
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data.sql
```

---

## 🏃 Running the Application

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using JAR
```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/jwt-security-0.0.1-SNAPSHOT.jar
```

### Option 3: Using IDE

1. Open project in IntelliJ IDEA / Eclipse
2. Locate `JwtSecurityApplication.java` (main class)
3. Right-click → Run

### Verify Application is Running
```bash
curl http://localhost:8080/api/public

# Expected: 200 OK with public endpoint response
```

**Console Output:**
```
Started JwtSecurityApplication in 3.456 seconds
Tomcat started on port(s): 8080 (http)
```

---

## 📡 API Endpoints

### Authentication Endpoints

#### **POST** `/api/login` - User Login
Authenticate user and receive JWT token.

**Request:**
```json
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwiaWF0IjoxNzMwMDY3NjAwLCJleHAiOjE3MzAwNzEyMDB9.7KwXg3hF9mZQR5xYqN8PvL2Jc4DnE6TmS1Aa9BgHiKw"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "error": "Invalid username or password",
  "status": 401,
  "timestamp": "2025-11-07T20:30:00",
  "path": "/api/login",
  "exception": "BadCredentialsException"
}
```

---

### Protected Endpoints

All protected endpoints require JWT token in Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

#### **GET** `/api/user/useronly` - USER Role Required

**Required Role:** `ROLE_USER`

**Request:**
```bash
GET http://localhost:8080/api/user/useronly
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Success Response (200 OK):**
```
Hello User admin with roles [ROLE_ADMIN, ROLE_USER]
```

**Error Response (403 Forbidden):**
```json
{
  "error": "Access Denied",
  "message": "You do not have permission to access this resource."
}
```

---

#### **GET** `/api/admin/adminonly` - ADMIN Role Required

**Required Role:** `ROLE_ADMIN`

**Request:**
```bash
GET http://localhost:8080/api/admin/adminonly
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Success Response (200 OK):**
```
Hello Admin admin with roles [ROLE_ADMIN, ROLE_USER]
```

---

#### **GET** `/api/manager/manageronly` - MANAGER Role Required

**Required Role:** `ROLE_MANAGER`

**Request:**
```bash
GET http://localhost:8080/api/manager/manageronly
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Success Response (200 OK):**
```
Hello Manager manager with roles [ROLE_MANAGER]
```

---

#### **GET** `/api/common/allok` - Any Authenticated Role

**Required Roles:** `ROLE_ADMIN` OR `ROLE_USER` OR `ROLE_MANAGER`

**Request:**
```bash
GET http://localhost:8080/api/common/allok
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Success Response (200 OK):**
```
Hello admin with roles [ROLE_ADMIN, ROLE_USER]
```

---

#### **GET** `/api/public` - Public Access

**Required Role:** None (accessible to anyone)

**Request:**
```bash
GET http://localhost:8080/api/public
```

**Success Response (200 OK):**
```
Public endpoint - No authentication required
```

---

### Error Responses

#### 401 Unauthorized - Missing/Invalid Token
```json
{
  "error": "Missing or invalid token",
  "message": "Full authentication is required to access this resource"
}
```

#### 403 Forbidden - Insufficient Permissions
```json
{
  "error": "Access Denied",
  "message": "You do not have permission to access this resource."
}
```

#### 429 Too Many Requests - Rate Limit Exceeded
```json
{
  "error": "Rate Limit Exceeded",
  "message": "Exceeded 100 calls per 60 seconds. Please slow down."
}
```

---

## 🔐 Authentication Flow

### Login Flow
```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client sends credentials                                  │
│    POST /api/login                                           │
│    { "username": "admin", "password": "password123" }       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. GlobalRateLimiterFilter                                   │
│    - Check IP rate limit (100 req/min) ✅                   │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. AuthController receives request                           │
│    - Calls authService.authenticateAndGenerateToken()       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. AuthenticationManager validates credentials               │
│    - CustomUserDetailsService.loadUserByUsername("admin")   │
│    - Load user from database with roles (EAGER)            │
│    - BCrypt password verification ✅                        │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. JwtUtil generates token                                   │
│    - Embed username: "admin"                                │
│    - Embed roles: ["ROLE_ADMIN", "ROLE_USER"]              │
│    - Set expiration: 1 hour                                 │
│    - Sign with HMAC-SHA256                                  │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. Return JWT token to client                                │
│    { "token": "eyJhbGc..." }                                │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. Client stores token                                       │
│    - localStorage / sessionStorage / secure cookie          │
│    - Uses in Authorization header for future requests       │
└─────────────────────────────────────────────────────────────┘
```

### Protected Request Flow
```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client sends request with token                           │
│    GET /api/admin/adminonly                                  │
│    Authorization: Bearer eyJhbGc...                         │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. GlobalRateLimiterFilter                                   │
│    - Check rate limit ✅                                     │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. JwtAuthenticationFilter                                   │
│    a) Extract token from header                             │
│    b) Validate signature & expiration ✅                    │
│    c) Extract username: "admin"                             │
│    d) Extract roles from token: ["ROLE_ADMIN", "ROLE_USER"]│
│    e) Load UserDetails from database                        │
│    f) Create Authentication object                          │
│    g) Set SecurityContext                                   │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Spring Security Authorization                             │
│    - URL check: /api/admin/adminonly requires ADMIN ✅      │
│    - @PreAuthorize check: hasRole('ADMIN') ✅              │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Controller method executes                                │
│    - Business logic runs                                    │
│    - Returns response                                       │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. Response sent to client                                   │
│    "Hello Admin admin with roles [ROLE_ADMIN, ROLE_USER]"  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧩 Component Architecture

### Complete Component Summary

| Layer | Component | File | Responsibility |
|-------|-----------|------|----------------|
| **Entities** | User | `User.java` | User entity with many-to-many roles |
| | Role | `Role.java` | Role entity for RBAC |
| **Repositories** | UserRepository | `UserRepository.java` | User data access |
| | RoleRepository | `RoleRepository.java` | Role data access |
| **DTOs** | LoginRequest | `LoginRequest.java` | Login credentials wrapper |
| | JwtResponse | `JwtResponse.java` | JWT token response |
| | ApiExceptionDto | `ApiExceptionDto.java` | Standardized error format |
| **Services** | AuthService | `AuthService.java` | Authentication interface |
| | AuthServiceImpl | `AuthServiceImpl.java` | Login & authorization logic |
| | CustomUserDetailsService | `CustomUserDetailsService.java` | Interface |
| | CustomUserDetailsServiceImpl | `CustomUserDetailsServiceImpl.java` | Load users from DB |
| **Security** | JwtUtil | `JwtUtil.java` | JWT generation & validation |
| | JwtAuthenticationFilter | `JwtAuthenticationFilter.java` | Token validation filter |
| | GlobalRateLimiterFilter | `GlobalRateLimiterFilter.java` | Rate limiting protection |
| | SecurityConfig | `SecurityConfig.java` | Security configuration |
| | CustomAuthenticationEntryPoint | `CustomAuthenticationEntryPoint.java` | 401 error handler |
| | CustomAccessDeniedHandler | `CustomAccessDeniedHandler.java` | 403 error handler |
| **Controllers** | AssignmentTestController | `AssignmentTestController.java` | REST API endpoints |
| **Utils** | JwtSecretGenerator | `JwtSecretGenerator.java` | Generate JWT secret key |

---

### Detailed Component Descriptions

#### **Entities Layer**

**User.java**
- JPA entity representing users
- Many-to-many relationship with Role
- EAGER fetching for roles (required for authentication)
- BCrypt-encrypted password storage

**Role.java**
- JPA entity representing roles (ADMIN, USER, MANAGER)
- Unique constraint on role name
- Bidirectional relationship with User

---

#### **Repository Layer**

**UserRepository.java**
- Extends JpaRepository for CRUD operations
- Custom query: `findByUsername(String username)`
- Used by CustomUserDetailsService during authentication

**RoleRepository.java**
- Extends JpaRepository for role management
- Custom query: `findByName(String name)`
- Used for role assignment during user registration

---

#### **Service Layer**

**AuthServiceImpl.java**
- Core authentication logic
- `authenticateAndGenerateToken()`: Validates credentials and generates JWT
- Uses AuthenticationManager for credential verification
- Delegates token generation to JwtUtil
- Method-level security demonstrations (doUserThing, doAdminThing, etc.)

**CustomUserDetailsServiceImpl.java**
- Implements Spring Security's UserDetailsService
- Loads user from database via UserRepository
- Converts User entity to Spring Security's UserDetails
- Adds "ROLE_" prefix to authorities for Spring Security compatibility

---

#### **Security Components**

**JwtUtil.java**
- JWT token generation with embedded username and roles
- Token validation (signature, expiration)
- Claims extraction (username, roles)
- Uses HMAC-SHA256 for signing
- Configurable token expiration

**JwtAuthenticationFilter.java**
- OncePerRequestFilter for stateless authentication
- Extracts JWT from Authorization header
- Validates token on every request
- Loads user details and sets SecurityContext
- Extracts roles from token (no DB query needed for authorization)

**GlobalRateLimiterFilter.java**
- Sliding window rate limiting algorithm
- 100 requests per 60 seconds per IP
- Thread-safe with ConcurrentHashMap
- Returns 429 status when rate exceeded
- Prevents brute-force and DDoS attacks

**SecurityConfig.java**
- Central security configuration
- Configures filter chain order
- Defines URL-based authorization rules
- Disables CSRF (not needed for stateless JWT)
- Configures AuthenticationManager with BCrypt
- Enables @PreAuthorize method security

**CustomAuthenticationEntryPoint.java**
- Handles 401 Unauthorized errors
- Returns JSON error response (not HTML)
- Triggered when authentication fails or token missing

**CustomAccessDeniedHandler.java**
- Handles 403 Forbidden errors
- Returns JSON error response
- Triggered when user lacks required role

---

#### **Controller Layer**

**AssignmentTestController.java**
- REST API endpoints for authentication and testing
- Login endpoint (public)
- Role-specific endpoints (USER, ADMIN, MANAGER)
- Common endpoint (any role)
- Method-level security with @PreAuthorize
- Exception handlers for clean error responses

---

## 🔒 Security Features

### Multi-Layer Security
```
Layer 1: Rate Limiting
  ↓ Blocks excessive requests
Layer 2: JWT Authentication
  ↓ Validates token signature & expiration
Layer 3: URL-Based Authorization
  ↓ Checks SecurityConfig rules
Layer 4: Method-Based Authorization
  ↓ Checks @PreAuthorize annotations
Layer 5: Business Logic
  ↓ Executes controller method
```

### Security Best Practices Implemented

✅ **Stateless Authentication** - No server-side sessions  
✅ **Password Hashing** - BCrypt with adaptive cost  
✅ **Token Signing** - HMAC-SHA256 signature  
✅ **Token Expiration** - Configurable TTL (1 hour default)  
✅ **Rate Limiting** - Prevent brute-force attacks  
✅ **Role-Based Access** - Fine-grained authorization  
✅ **CSRF Protection** - Not needed for JWT (stateless)  
✅ **SQL Injection Protection** - JPA parameterized queries  
✅ **Error Handling** - No sensitive info in error messages  
✅ **HTTPS Ready** - Can be deployed with TLS/SSL  

### Token Security

**JWT Structure:**
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "admin",
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "iat": 1730067600,
  "exp": 1730071200
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

**Security Notes:**
- ⚠️ JWT payload is Base64-encoded (not encrypted) - visible to anyone
- ✅ Signature prevents tampering
- ✅ Never put sensitive data (passwords, SSNs) in JWT
- ✅ Use HTTPS in production to prevent token interception

---

## 🧪 Testing

### Using Postman

#### 1. Login and Get Token
```
POST http://localhost:8080/api/login
Content-Type: application/json

Body:
{
  "username": "admin",
  "password": "password123"
}
```

**Copy the returned token**

#### 2. Access Protected Endpoint
```
GET http://localhost:8080/api/admin/adminonly
Authorization: Bearer <paste-token-here>
```

### Using cURL

#### Login
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

#### Store Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}' \
  | jq -r '.token')

echo $TOKEN
```

#### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/admin/adminonly \
  -H "Authorization: Bearer $TOKEN"
```

### Test Scenarios

#### ✅ Test 1: Successful Login
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# Expected: 200 OK with JWT token
```

#### ✅ Test 2: Invalid Credentials
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'

# Expected: 401 Unauthorized
```

#### ✅ Test 3: Access Without Token
```bash
curl -X GET http://localhost:8080/api/admin/adminonly

# Expected: 401 Unauthorized
```

#### ✅ Test 4: Access With Valid Token, Correct Role
```bash
# Login as admin
TOKEN=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}' | jq -r '.token')

# Access admin endpoint
curl -X GET http://localhost:8080/api/admin/adminonly \
  -H "Authorization: Bearer $TOKEN"

# Expected: 200 OK - "Hello Admin admin with roles..."
```

#### ✅ Test 5: Access With Valid Token, Wrong Role
```bash
# Login as user (has ROLE_USER only)
TOKEN=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password123"}' | jq -r '.token')

# Try to access admin endpoint
curl -X GET http://localhost:8080/api/admin/adminonly \
  -H "Authorization: Bearer $TOKEN"

# Expected: 403 Forbidden
```

#### ✅ Test 6: Rate Limiting
```bash
# Send 101 requests rapidly
for i in {1..101}; do
  echo "Request $i"
  curl -X GET http://localhost:8080/api/public
done

# Expected: First 100 succeed, 101st returns 429 Too Many Requests
```

#### ✅ Test 7: Expired Token
```bash
# Wait for token to expire (1 hour by default)
# Or manually set jwt.expirationMs=5000 (5 seconds) in application.properties

# Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}' | jq -r '.token')

# Wait 6 seconds
sleep 6

# Try to use expired token
curl -X GET http://localhost:8080/api/admin/adminonly \
  -H "Authorization: Bearer $TOKEN"

# Expected: 401 Unauthorized - "JWT expired"
```

---

## 📂 Project Structure
```
SecuringRestfulAPIs--JWTAuthentication-and-RoleBasedAuthorisation/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/
│   │   │       └── ali/
│   │   │           └── jwt_security/
│   │   │               ├── config/
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   ├── CustomAuthenticationEntryPoint.java
│   │   │               │   └── CustomAccessDeniedHandler.java
│   │   │               │
│   │   │               ├── controllers/
│   │   │               │   └── AssignmentTestController.java
│   │   │               │
│   │   │               ├── dtos/
│   │   │               │   ├── LoginRequest.java
│   │   │               │   ├── JwtResponse.java
│   │   │               │   └── ApiExceptionDto.java
│   │   │               │
│   │   │               ├── entities/
│   │   │               │   ├── User.java
│   │   │               │   └── Role.java
│   │   │               │
│   │   │               ├── jwt/
│   │   │               │   └── JwtUtil.java
│   │   │               │
│   │   │               ├── repositories/
│   │   │               │   ├── UserRepository.java
│   │   │               │   └── RoleRepository.java
│   │   │               │
│   │   │               ├── services/
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── AuthServiceImpl.java
│   │   │               │   ├── CustomUserDetailsService.java
│   │   │               │   └── CustomUserDetailsServiceImpl.java
│   │   │               │
│   │   │               ├── utils/
│   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │               │   ├── GlobalRateLimiterFilter.java
│   │   │               │   └── JwtSecretGenerator.java
│   │   │               │
│   │   │               └── JwtSecurityApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql (optional)
│   │
│   └── test/
│       └── java/
│           └── edu/
│               └── ali/
│                   └── jwt_security/
│                       └── JwtSecurityApplicationTests.java
│
├── .gitignore
├── pom.xml
├── README.md
└── LICENSE
```

---

## 🔮 Future Enhancements

### Planned Features

- [ ] **Refresh Token Mechanism**
  - Short-lived access tokens (15 min)
  - Long-lived refresh tokens (7 days)
  - Token refresh endpoint

- [ ] **User Registration Endpoint**
  - POST /api/register
  - Email validation
  - Default USER role assignment

- [ ] **Password Reset Flow**
  - Forgot password endpoint
  - Email-based token
  - Password reset confirmation

- [ ] **Token Blacklist / Logout**
  - Store revoked tokens in Redis
  - Immediate access revocation
  - True logout functionality

- [ ] **Email Verification**
  - Send verification email on registration
  - Verify email endpoint
  - Account activation

- [ ] **Multi-Factor Authentication (MFA)**
  - TOTP (Time-based OTP)
  - SMS verification
  - Backup codes

- [ ] **Audit Logging**
  - Log all authentication attempts
  - Track successful/failed logins
  - Security event monitoring

- [ ] **Profile Management**
  - Update user profile
  - Change password
  - View login history

- [ ] **API Documentation**
  - Swagger/OpenAPI integration
  - Interactive API documentation
  - Automatic endpoint discovery

- [ ] **Docker Support**
  - Dockerfile
  - docker-compose.yml
  - Multi-container setup (app + database)

- [ ] **CI/CD Pipeline**
  - GitHub Actions
  - Automated testing
  - Deployment automation

- [ ] **Monitoring & Metrics**
  - Prometheus integration
  - Grafana dashboards
  - Health check endpoints

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Write comprehensive JavaDoc comments
- Include unit tests for new features
- Maintain code coverage above 80%
- Use meaningful commit messages

## 👤 Author

**Mir Osayd Ali**
- GitHub: [@Osayd-ali](https://github.com/Osayd-ali)
- LinkedIn: [Mir Osayd Ali](https://www.linkedin.com/in/mir-osayd-ali-7681a0260/)

---

## 🙏 Acknowledgments

- Spring Framework Team for excellent documentation
- JWT.io for JWT resources
- in28minutes for Spring Security tutorials


**Built with ❤️ using Spring Boot and JWT**
