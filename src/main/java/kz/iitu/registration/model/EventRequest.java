package kz.iitu.registration.model;

/**
 * JSON body of POST and PUT requests: {"title": "...", "location": "..."}.
 * It has no id on purpose: ids are assigned by the server, never by the client.
 */
public record EventRequest(String title, String location) {
}
