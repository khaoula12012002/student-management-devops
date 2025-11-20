# Copilot Instructions — student-management

These notes make AI agents productive immediately in this Spring Boot repo.

## Big Picture

- Stack: Spring Boot 3.5, Java 17, Spring Web, Spring Data JPA, MySQL, Lombok, springdoc-openapi.
- Layers: `controllers` → `services` → `repositories` → `entities` (no DTOs). Controllers return entities directly.
- DB: MySQL `studentdb` (auto-created), username `root`, empty password. JPA `ddl-auto=update`.
- App runs on port `8089` with servlet context path `/student`.
- CORS is open to `http://localhost:4200` on all controllers (Angular-friendly).

## Build, Run, Debug

- Prereq: MySQL reachable at `localhost:3306` with the credentials in `src/main/resources/application.properties`.
- Run (Windows PowerShell):
  - `./mvnw.cmd spring-boot:run`
- Build jar:
  - `./mvnw.cmd clean package`
- Tests:
  - `./mvnw.cmd test`
- Swagger UI (springdoc 2.x):
  - Try `http://localhost:8089/student/swagger-ui/index.html` (or `/swagger-ui.html`).

## Routing & API Style

- All routes are under the context path `/student`.
- Resource base paths vary in casing and include verbs:
  - Students: `/students/getAllStudents`, `/students/getStudent/{id}`, `/students/createStudent`, `/students/updateStudent`, `/students/deleteStudent/{id}`.
  - Departments: `/Depatment/getAllDepartment`, `/Depatment/getDepartment/{id}`, `/Depatment/createDepartment`, `/Depatment/updateDepartment`, `/Depatment/deleteDepartment/{id}`.
  - Enrollments: `/Enrollment/getAllEnrollment`, `/Enrollment/getEnrollment/{id}`, etc.
- Creation and update both use `save(...)` in services; deletes return `void`.

## Data Model (examples in `entities/`)

- `Student` ↔ `Department` is `@ManyToOne` (students belong to a department).
- `Enrollment` joins `Student` and `Course` (`@ManyToOne` each) with fields like `grade`, `status` (`enum Status`).
- IDs use `@GeneratedValue(strategy = IDENTITY)`; Lombok provides getters/setters.

## Persistence & Services

- Repositories extend `JpaRepository<..., Long>` with no custom queries yet.
- Service pattern: interface (e.g., `IStudentService`) + `@Service` impl that delegates to the repository.
- Fetch-by-id uses either `findById(...).orElse(null)` or `.get()` (be aware of `NoSuchElementException`).

## Adding A New Feature (follow existing conventions)

1. Create an `@Entity` with relationships and Lombok.
2. Add a `JpaRepository` interface.
3. Add a service interface and `@Service` implementation (CRUD via repository; reuse `save(...)`).
4. Add a `@RestController` with base path and verb-like subpaths matching existing naming.
5. If the frontend hits from `localhost:4200`, add `@CrossOrigin(origins = "http://localhost:4200")`.

## Concrete Examples

- List students: `GET http://localhost:8089/student/students/getAllStudents`
- Get student: `GET http://localhost:8089/student/students/getStudent/1`
- Create student: `POST http://localhost:8089/student/students/createStudent` with JSON body matching `entities.Student`.

## Key Files

- Config: `src/main/resources/application.properties`
- HTTP: `controllers/*`
- Business: `services/*`
- Data: `repositories/*`, `entities/*`
- Entry: `StudentManagementApplication.java`
