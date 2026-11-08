package kz.iitu.registration.service;

import kz.iitu.registration.model.EventInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Business logic layer. @Service registers this class as a Spring bean,
 * so Spring creates one instance and injects it where it is needed.
 * Data is kept in memory; there is no database in this lab.
 */
@Service
public class EventService {

    private final List<EventInfo> events = List.of(
            new EventInfo(1, "Web Design Workshop", "IITU, Room 301", "2026-11-20"),
            new EventInfo(2, "JavaScript Quiz", "IITU, Computer Lab 205", "2026-11-27"),
            new EventInfo(3, "IT Career Talk", "IITU, Main Hall", "2026-12-04")
    );

    public String getWelcomeMessage() {
        return "Welcome to the Event Registration API";
    }

    public List<EventInfo> getAllEvents() {
        return events;
    }

    public Optional<EventInfo> findEventById(int id) {
        return events.stream()
                .filter(event -> event.id() == id)
                .findFirst();
    }
}
