# Beeline SMS — Backend API

Spring Boot REST API for the Beeline Advanced Diploma in English student management system ("SMS" = Student Management System). Serves the [React frontend](../sms-app) with endpoints for authentication, student/staff/branch administration, attendance (manual and QR-code based), homework tracking, announcements, and fee data.

## Tech Stack

- **Java 17**
- **Spring Boot 3.3.2** — Spring MVC (`@RestController`), embedded Tomcat, port `8080`
- **Spring Data JPA** + **Hibernate** — ORM
- **PostgreSQL** — database (`beeline_sms`)
- **Lombok** — reduces entity/DTO boilerplate (`@Data`, `@Builder`, etc.)
- **Maven** — build tool
- No Spring Security / JWT — authentication is currently a simple credential check (see [Authentication](#authentication))

## Project Structure

```
beeline-sms-api/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/beeline/sms/
    │   │   ├── BeelineSmsApplication.java   # Entry point
    │   │   ├── config/
    │   │   │   └── CorsConfig.java           # Global CORS for /api/**
    │   │   ├── controller/                    # REST controllers
    │   │   ├── dto/                            # Request/response DTOs
    │   │   ├── entity/                         # JPA entities
    │   │   ├── enums/                          # Role, UserStatus, Gender, HomeworkStatus
    │   │   ├── repository/                     # Spring Data JPA repositories
    │   │   └── service/                        # Business logic
    │   └── resources/
    │       ├── application.properties          # DB config, JPA settings, server port
    │       └── data.sql                        # Seed data (runs on every startup)
    └── test/java/com/beeline/sms/
        └── BeelineSmsApplicationTests.java
```

## Features / API Endpoints

All endpoints are under `/api/v1`.

- **Auth** (`/auth`) — `POST /login`
- **Admin** (`/admin`) — Branch CRUD (`/branches`), Student CRUD + search (`/students`, `/students/next-id`), Staff CRUD + attendance-permission toggle (`/staff/{id}/attendance-permission`)
- **Attendance** (`/attendance`) — mark by student ID or by QR code, list (filterable by branch/date), per-student, `/my` for the logged-in student
- **Homework** (`/homework`) — assign tasks, list by branch/date, student view, search student, batch grading (`/submit-status`), `/my`
- **Teacher** (`/teacher`) — announcements (create/list, branch-targeted or global)
- **Student** (`/student`) — `/fees`, `/fees/my`, `/profile`

## Data Model

Key entities (see `entity/` and `database.md` in the project root for full detail):

- **User** — auth record: username, plaintext password, role (`ADMIN`/`TEACHER`/`STAFF`/`STUDENT`), status; 1:1 with Student or Staff
- **Student** — per-branch unique student ID, linked to a `User` and a `Branch`
- **Staff** — linked to a `User`, `canMarkAttendance` flag, many-to-many with `Branch`
- **Branch** — name, duration, schedule, fee/installment config; one-to-many with Student, many-to-many with Staff
- **Attendance** — student + date (unique together), present flag, who marked it
- **Announcement** — title/content, posted by, optional target branch (null = all branches)
- **HomeworkTask** — branch, assigned date, task detail
- **StudentHomeworkSubmission** — student + task (unique together), status (`PENDING`/`SUBMITTED`/`INCOMPLETE`)

Schema is managed automatically by Hibernate (`ddl-auto=update`); `data.sql` seeds demo data idempotently on every startup.

## Setup & Running

### Prerequisites
- JDK 17
- Maven
- A running PostgreSQL instance with a `beeline_sms` database

### Configure
Database connection lives in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/beeline_sms
spring.datasource.username=postgres
spring.datasource.password=1234
```
Update these to match your local PostgreSQL setup. Credentials are currently hardcoded here rather than externalized via environment variables — worth changing before any production deployment.

### Run
```bash
mvn spring-boot:run             # run in dev mode
mvn clean package                # build a jar into target/
java -jar target/beeline-sms-api-1.0.0.jar   # run the built jar
mvn test                          # run tests
```

The API starts on `http://localhost:8080`, with CORS open to all origins on `/api/**` for local development with the frontend.

## Authentication

`POST /api/v1/auth/login` compares the submitted password against the plaintext password stored in the `users` table and returns the user's profile as JSON on success. There is no JWT/session mechanism — the frontend persists the returned profile client-side in `localStorage` and relies on it purely for client-side access control. No `Authorization` header is required or checked on subsequent requests. This is a dev/prototype-stage auth setup, not production-hardened security.

## Third-Party Integrations

None. Despite "SMS" in the name, this is a Student Management System, not an SMS/text-messaging service — there is no Twilio, Beeline telecom gateway, or other external messaging integration. QR code generation/scanning for attendance is handled entirely client-side by the frontend.
