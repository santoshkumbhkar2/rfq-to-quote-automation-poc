# 🚀 RFQ to Quote Automation Service (Spring Boot Reference)

A production-grade, cleanly architected **Spring Boot 3.3.x + Java 17+** reference project demonstrating how to build robust enterprise microservices using standard design patterns.

---

## 🧭 The 5 Golden Rules of Spring Boot

When working with modern Spring Boot (especially when reviewing or supervising AI-generated code), keep these 5 laws in mind:

1. **Inversion of Control (IoC) & Dependency Injection (DI)**:
   - Never write `new MyService()` or `new MyRepository()`.
   - Let the Spring ApplicationContext instantiate and wire beans.
   - Always use **Constructor Injection** (or `@RequiredArgsConstructor`), never `@Autowired` on private fields.

2. **Strict Layer Separation**:
   ```
   Controller Layer  (@RestController)   -> Handles HTTP, validates input, returns ResponseEntity
          │
          ▼
   Service Layer     (@Service)           -> Executes business logic, defines @Transactional boundaries
          │
          ▼
   Repository Layer  (@Repository)        -> Spring Data JPA queries, interacts with DB
          │
          ▼
   Database Layer    (H2 / PostgreSQL)    -> Persistent relational storage
   ```
   - Controller talks **only** to Service.
   - Service coordinates logic and calls Repository.
   - **Entities NEVER leave the Service layer to the Controller**: Always accept Request DTOs and return Response DTOs.

3. **Stateless Singletons**:
   - Spring beans (`@Service`, `@RestController`) are singletons shared across all threads/requests.
   - Never keep mutable instance fields in a service or controller class.

4. **Fail-Fast Input Validation**:
   - Use Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Positive`, etc.) on DTO records.
   - Annotate Controller request bodies with `@Valid` so bad requests fail immediately before hitting any business logic.

5. **Centralized Error Handling**:
   - Never let raw database errors or stack traces reach API clients.
   - Use `@RestControllerAdvice` to translate all exceptions into predictable, uniform JSON error responses.

---

## 📂 Project Directory Structure

```
rfq-to-quote-automation-poc/
├── pom.xml                                  # Maven dependencies, plugins, and Java 17 configuration
├── README.md                                # This reference manual
└── src/
    ├── main/
    │   ├── java/com/santosh/rfq/
    │   │   ├── RfqApplication.java          # Main Spring Boot entry point (@SpringBootApplication)
    │   │   │
    │   │   ├── controller/                  # REST API Layer
    │   │   │   └── QuoteController.java     # Endpoints: POST, GET, PATCH, DELETE /api/v1/quotes
    │   │   │
    │   │   ├── service/                     # Business Logic Layer
    │   │   │   ├── QuoteService.java        # Interface defining business contract
    │   │   │   └── impl/
    │   │   │       └── QuoteServiceImpl.java# Implementation with @Transactional & DTO mapping
    │   │   │
    │   │   ├── repository/                  # Data Access Layer
    │   │   │   └── QuoteRequestRepository.java # JpaRepository with derived & JPQL queries
    │   │   │
    │   │   ├── entity/                      # Database Schema / ORM Models
    │   │   │   ├── QuoteRequest.java        # JPA Entity (@Entity, @Table)
    │   │   │   └── QuoteStatus.java         # Lifecycle Enum (PENDING, APPROVED, etc.)
    │   │   │
    │   │   ├── dto/                         # Data Transfer Objects (External API Contracts)
    │   │   │   ├── request/
    │   │   │   │   └── CreateQuoteRequest.java # Record with @NotBlank, @Min, @Email
    │   │   │   └── response/
    │   │   │       ├── QuoteResponse.java   # Record returning formatted quote + calculated costs
    │   │   │       └── ErrorResponse.java   # Standardized error payload structure
    │   │   │
    │   │   ├── exception/                   # Error Handling
    │   │   │   ├── ResourceNotFoundException.java # Domain 404 Exception
    │   │   │   └── GlobalExceptionHandler.java    # @RestControllerAdvice for uniform errors
    │   │   │
    │   │   └── config/                      # Spring Configuration
    │   │       └── OpenApiConfig.java       # Swagger / OpenAPI 3.0 documentation bean
    │   │
    │   └── resources/
    │       └── application.yml              # Config: Port 8080, H2 In-Memory DB, JPA, Swagger
    │
    └── test/java/com/santosh/rfq/
        ├── service/
        │   └── QuoteServiceTest.java        # Fast unit test using Mockito (no Tomcat startup)
        └── controller/
            └── QuoteControllerTest.java     # Web slice test using @WebMvcTest and MockMvc
```

---

## 🏃 How to Run the Application

### Option A: Using Maven CLI
Ensure you have Java 17+ installed. From this directory run:

```bash
# 1. Run automated tests
mvn clean test

# 2. Run the application
mvn spring-boot:run
```

### Option B: Using Any IDE (IntelliJ IDEA / VS Code / Eclipse)
1. Open the `rfq-to-quote-automation-poc` folder.
2. Locate `src/main/java/com/santosh/rfq/RfqApplication.java`.
3. Right-click &rarr; **Run 'RfqApplication'**.

---

## 🔍 Interactive Testing & Tools

Once the server is running on `http://localhost:8080`:

### 1. Swagger UI (Interactive API Explorer)
Open in your browser:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
- View and execute all endpoints directly from your browser.
- View schemas for request and response payloads.

### 2. H2 Database Console
Open in your browser:
👉 **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**
- **JDBC URL**: `jdbc:h2:mem:rfqdb`
- **User Name**: `sa`
- **Password**: *(leave blank)*
- Click **Connect** to query the `QUOTE_REQUESTS` table live!

---

## 🧪 Sample cURL Commands

### 1. Submit a New Quote (201 Created)
```bash
curl -X POST http://localhost:8080/api/v1/quotes \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "procurement@aerotech.com",
    "partNumber": "TITANIUM-VALVE-44",
    "quantity": 250,
    "targetUnitPrice": 45.00,
    "notes": "Target delivery within 4 weeks"
  }'
```

### 2. Test Input Validation (400 Bad Request)
```bash
curl -X POST http://localhost:8080/api/v1/quotes \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "not-a-valid-email",
    "partNumber": "",
    "quantity": 0
  }'
```

### 3. Get Quote by ID (200 OK or 404 Not Found)
```bash
curl http://localhost:8080/api/v1/quotes/1
```

### 4. Query Quotes by Customer Email
```bash
curl "http://localhost:8080/api/v1/quotes?email=procurement@aerotech.com"
```

### 5. Update Quote Status
```bash
curl -X PATCH "http://localhost:8080/api/v1/quotes/1/status?newStatus=APPROVED"
```

---

## 🎓 The Zero-to-Advanced Learning Curriculum

| Level | Topic | Key Skills & Annotations |
| :--- | :--- | :--- |
| **0. Foundations** | Java 17 & Core Spring | `record`, `Optional`, IoC, DI, Bean Lifecycle, `@Component`, `@Configuration` |
| **1. REST API** | Web & Validation | `@RestController`, `@GetMapping`, `@PostMapping`, `@Valid`, `@NotBlank`, HTTP codes |
| **2. Persistence** | Spring Data JPA | `@Entity`, `@Table`, `JpaRepository`, Derived Queries, JPQL, `@Transactional` |
| **3. Architecture** | Production Patterns | `@RestControllerAdvice`, MapStruct, `@ConfigurationProperties`, Spring Profiles |
| **4. Security** | Auth & Permissions | Spring Security 6, `SecurityFilterChain`, JWT filter, `@PreAuthorize` (RBAC) |
| **5. Advanced** | Cloud & Performance | Redis `@Cacheable`, `@Async`, Spring Events, Actuator metrics, Docker & Testcontainers |
