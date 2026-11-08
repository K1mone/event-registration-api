package kz.iitu.registration.model;

/**
 * One event that students can register for.
 * A record automatically has a constructor, getters (id(), title(), ...),
 * equals/hashCode and toString. Jackson turns it into a JSON object.
 */
public record EventInfo(int id, String title, String location, String date) {
}
