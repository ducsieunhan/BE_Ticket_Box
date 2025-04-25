package com.ticket.box.util.error;

public class OrganizerCreateException extends RuntimeException {
    public OrganizerCreateException() {
        super("Organizer connect with some events");
    }
}
