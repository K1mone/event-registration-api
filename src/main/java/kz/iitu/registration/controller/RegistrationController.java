package kz.iitu.registration.controller;

import kz.iitu.registration.model.EventInfo;
import kz.iitu.registration.model.EventRequest;
import kz.iitu.registration.service.EventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP layer. @RestController = @Controller + @ResponseBody: every return
 * value is written to the response body as JSON (by Jackson).
 * The controller only handles HTTP (URL, parameters, body, status codes);
 * EventService holds and changes the list of events.
 */
@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final EventService eventService;
    private final String applicationName;
    private final String course;
    private final int semester;

    /**
     * Constructor-based dependency injection: Spring sees that the controller
     * needs an EventService, takes the existing @Service bean and passes it
     * here. Values from application.properties are injected with @Value.
     * The controller never calls "new EventService()".
     */
    public RegistrationController(EventService eventService,
                                  @Value("${spring.application.name}") String applicationName,
                                  @Value("${app.course}") String course,
                                  @Value("${app.semester}") int semester) {
        this.eventService = eventService;
        this.applicationName = applicationName;
        this.course = course;
        this.semester = semester;
    }

    // GET /api/hello -> {"message": "..."}
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", eventService.getWelcomeMessage());
    }

    // GET /api/info -> values read from application.properties
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("application", applicationName);
        info.put("course", course);
        info.put("semester", semester);
        return info;
    }

    // GET /api/events                    -> all events
    // GET /api/events?title=quiz         -> titles containing "quiz" (ignoring case), [] if none
    // GET /api/events?location=hall      -> optional challenge: filter by location
    // GET /api/events?title=t&location=l -> both filters combined
    @GetMapping("/events")
    public List<EventInfo> getEvents(@RequestParam(required = false) String title,
                                     @RequestParam(required = false) String location) {
        if (title == null && location == null) {
            return eventService.getAllEvents();
        }
        return eventService.searchEvents(title, location);
    }

    // GET /api/events/{id} -> 200 with the event, or 404
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable int id) {
        return eventService.findEventById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound(id));
    }

    // POST /api/events + JSON {title, location} -> 201 with the new event (id from the server)
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody EventRequest request) {
        String error = validate(request);
        if (error != null) {
            return badRequest(error);
        }
        EventInfo created = eventService.createEvent(request.title(), request.location());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/events/{id} + JSON {title, location} -> 200 with the updated event, or 404
    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable int id, @RequestBody EventRequest request) {
        String error = validate(request);
        if (error != null) {
            return badRequest(error);
        }
        return eventService.updateEvent(id, request.title(), request.location())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> notFound(id));
    }

    // DELETE /api/events/{id} -> 204 with no body, or 404
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable int id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.noContent().build();
        }
        return notFound(id);
    }

    /** Basic input check: returns an error message, or null if the body is valid. */
    private String validate(EventRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            return "Title is required";
        }
        if (request.location() == null || request.location().isBlank()) {
            return "Location is required";
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> notFound(int id) {
        return error(HttpStatus.NOT_FOUND, "Event not found", "Event with id " + id + " does not exist");
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, "Bad request", message);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
