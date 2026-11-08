package kz.iitu.registration.controller;

import kz.iitu.registration.model.EventInfo;
import kz.iitu.registration.service.EventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP layer. @RestController = @Controller + @ResponseBody: every return
 * value is written to the response body as JSON (by Jackson).
 * All endpoints start with /api.
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

    // GET /api/events -> JSON array of three events
    @GetMapping("/events")
    public List<EventInfo> getEvents() {
        return eventService.getAllEvents();
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

    // Bonus: GET /api/events/{id} -> one event, or 404 with a clear message
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable int id) {
        return eventService.findEventById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(notFoundBody(id)));
    }

    private Map<String, Object> notFoundBody(int id) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 404);
        body.put("error", "Event not found");
        body.put("message", "Event with id " + id + " does not exist");
        return body;
    }
}
