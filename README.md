# Lab 6 — Event Registration API (Spring Boot)

First Spring Boot backend for the Event Registration scenario. It exposes simple JSON
endpoints, keeps data in memory, and uses constructor-based dependency injection.
No database, no frontend, no CRUD — those come in later labs.

## Requirements
- **JDK 17 or later** (tested on JDK 23)
- Gradle — the wrapper (`gradlew`) is included, nothing else to install
- Spring Boot 4.1.2, dependency: Spring Web

## How to run
**IntelliJ IDEA:** open the project folder, wait for Gradle import, open
`EventRegistrationApplication.java` and click Run next to `main()`.

**Terminal:**
```bash
./gradlew bootRun        # Windows: gradlew.bat bootRun
```
The console shows `Tomcat started on port 8080`. Stop with `Ctrl+C`.

## Endpoints
| Method | URL | Response |
|---|---|---|
| GET | http://localhost:8080/api/hello | `{"message": "Welcome to the Event Registration API"}` |
| GET | http://localhost:8080/api/events | JSON array of 3 events |
| GET | http://localhost:8080/api/info | application name, course and semester from `application.properties` |
| GET | http://localhost:8080/api/events/{id} | *(bonus)* one event, or **404** with `{"error": "Event not found", ...}` |

## Project structure
```
src/main/java/kz/iitu/registration/
  EventRegistrationApplication.java   – entry point (@SpringBootApplication)
  controller/RegistrationController.java
  service/EventService.java
  model/EventInfo.java
src/main/resources/application.properties
```

## How it works
- **Model — `EventInfo`**: a Java `record` with `id`, `title`, `location`, `date`.
  Spring (Jackson) converts it to a JSON object automatically.
- **Service — `EventService`** (`@Service`): business logic. Holds the in-memory
  `List<EventInfo>` (Web Design Workshop, JavaScript Quiz, IT Career Talk), returns the
  welcome message, all events, and finds an event by id.
- **Controller — `RegistrationController`** (`@RestController`, `@RequestMapping("/api")`):
  receives HTTP GET requests and returns data as JSON. It gets `EventService` **through its
  constructor** — Spring creates the service bean and passes it in; the controller never
  calls `new EventService()`. Settings `spring.application.name`, `app.course` and
  `app.semester` are injected from `application.properties` with `@Value`.
