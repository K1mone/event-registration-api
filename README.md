# Lab 7 — Event Registration API: REST Controller (Spring Boot)

Spring Boot backend for the Event Registration scenario. Lab 6 added the first GET
endpoints; Lab 7 extends it to full CRUD: create, read, update, delete and search events
over HTTP. Data is kept in an in-memory list, so every change disappears when the
application restarts. No database, security, frontend or global exception handling.

## Requirements
- **JDK 17 or later** (tested on JDK 21 and 23)
- Gradle — the wrapper (`gradlew`) is included, nothing else to install
- Spring Boot 4.1.2, dependency: Spring Web
- Postman, Insomnia, curl or the IntelliJ HTTP Client for POST / PUT / DELETE
  (a browser alone can only send GET)

## How to run
**IntelliJ IDEA:** open the project folder, wait for Gradle import, open
`EventRegistrationApplication.java` and click Run next to `main()`.

**Terminal:**
```bash
./gradlew bootRun        # Windows: gradlew.bat bootRun
```
The console shows `Tomcat started on port 8080`. Base URL: `http://localhost:8080`.
Stop with `Ctrl+C`.

## Endpoints

| Method | URL | Body | Success | Errors |
|---|---|---|---|---|
| GET | `/api/events` | – | **200**, JSON array of all events | – |
| GET | `/api/events?title=quiz` | – | **200**, events whose title contains `quiz` (ignoring case); `[]` if none | – |
| GET | `/api/events/{id}` | – | **200**, one event | **404** if the id does not exist |
| POST | `/api/events` | `{"title", "location"}` | **201**, the created event with a new server-assigned id | **400** if title or location is missing/blank |
| PUT | `/api/events/{id}` | `{"title", "location"}` | **200**, the event with both fields replaced, same id | **404** if the id does not exist (nothing is created), **400** if title or location is missing/blank |
| DELETE | `/api/events/{id}` | – | **204**, no body | **404** if the id does not exist |

Optional challenge: `GET /api/events?location=hall` filters by location, and
`GET /api/events?title=workshop&location=room` combines both filters (an event must match both).

Endpoints kept from Lab 6: `GET /api/hello` (welcome message) and `GET /api/info`
(values from `application.properties`).

### Example JSON body (POST / PUT)
Send with header `Content-Type: application/json` (Postman: Body → raw → JSON):
```json
{
  "title": "Web Design Workshop",
  "location": "Room 101"
}
```
Response of `POST /api/events` — **201 Created**:
```json
{
  "id": 4,
  "title": "Web Design Workshop",
  "location": "Room 101"
}
```
Error response, e.g. `GET /api/events/999` — **404 Not Found**:
```json
{
  "status": 404,
  "error": "Event not found",
  "message": "Event with id 999 does not exist"
}
```

All test requests are also in [`requests.http`](requests.http) (open it in IntelliJ IDEA
and click ▶ next to a request).

## Sample data and IDs
The app starts with three events: **1** Web Design Workshop, **2** JavaScript Quiz,
**3** IT Career Talk. Clients never choose or change IDs: the server assigns them from a
separate counter that starts at 4 and only grows, so a deleted ID is never reused while
the app runs (delete 4, then POST → the new event gets 5). After a restart the list and
the counter are back to the initial state.

## Project structure
```
src/main/java/kz/iitu/registration/
  EventRegistrationApplication.java   – entry point (@SpringBootApplication)
  controller/RegistrationController.java – HTTP: URLs, parameters, body, status codes
  service/EventService.java           – in-memory list and the operations on it
  model/EventInfo.java                – event: id, title, location (record)
  model/EventRequest.java             – JSON body of POST/PUT: title, location (record)
src/main/resources/application.properties
requests.http                         – ready-made test requests
```

## How it works
- **Controller — `RegistrationController`** (`@RestController`, `@RequestMapping("/api")`)
  only handles HTTP. `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` map
  methods to URLs; `@PathVariable int id` reads the id from `/api/events/{id}`;
  `@RequestParam(required = false) String title` reads `?title=`; `@RequestBody
  EventRequest` turns the JSON body into a Java object. It checks that title and location
  are not blank (400 otherwise), calls the service and wraps the result in a
  `ResponseEntity` with the right status code (200 / 201 / 204 / 404).
- **Service — `EventService`** (`@Service`) holds the `ArrayList<EventInfo>` and the ID
  counter and does the actual work: `getAllEvents`, `searchEvents`, `findEventById`,
  `createEvent`, `updateEvent`, `deleteEvent`.
- **Constructor injection:** Spring creates one `EventService` bean and passes it to the
  controller's constructor; the controller never calls `new EventService()`.
