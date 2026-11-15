package kz.iitu.registration.service;

import kz.iitu.registration.model.EventInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Business logic layer. @Service registers this class as a Spring bean,
 * so Spring creates one instance and injects it where it is needed.
 * Data is kept in an in-memory list: every change is lost when the app restarts.
 */
@Service
public class EventService {

    private final List<EventInfo> events = new ArrayList<>();

    // Separate increasing counter, so a deleted id is never reused during one run.
    private int nextId;

    public EventService() {
        events.add(new EventInfo(1, "Web Design Workshop", "IITU, Room 301"));
        events.add(new EventInfo(2, "JavaScript Quiz", "IITU, Computer Lab 205"));
        events.add(new EventInfo(3, "IT Career Talk", "IITU, Main Hall"));
        nextId = 4; // above the largest sample id
    }

    public String getWelcomeMessage() {
        return "Welcome to the Event Registration API";
    }

    public synchronized List<EventInfo> getAllEvents() {
        return List.copyOf(events);
    }

    /**
     * Returns events whose title contains {@code title} and whose location contains
     * {@code location}, ignoring case. A null or blank filter is not applied.
     */
    public synchronized List<EventInfo> searchEvents(String title, String location) {
        List<EventInfo> result = new ArrayList<>();
        for (EventInfo event : events) {
            if (containsIgnoreCase(event.title(), title)
                    && containsIgnoreCase(event.location(), location)) {
                result.add(event);
            }
        }
        return result;
    }

    public synchronized Optional<EventInfo> findEventById(int id) {
        return events.stream()
                .filter(event -> event.id() == id)
                .findFirst();
    }

    public synchronized EventInfo createEvent(String title, String location) {
        EventInfo event = new EventInfo(nextId, title.trim(), location.trim());
        nextId++;
        events.add(event);
        return event;
    }

    /** Replaces title and location, keeps the id. Empty if the id does not exist. */
    public synchronized Optional<EventInfo> updateEvent(int id, String title, String location) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).id() == id) {
                EventInfo updated = new EventInfo(id, title.trim(), location.trim());
                events.set(i, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    /** Returns true if the event existed and was removed. */
    public synchronized boolean deleteEvent(int id) {
        return events.removeIf(event -> event.id() == id);
    }

    private static boolean containsIgnoreCase(String text, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        return text.toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT));
    }
}
