package com.ticket.box.util.error;

public class TicketNotFoundException extends RuntimeException {
  public TicketNotFoundException(Long ticketId) {
    super("Ticket not found with ID: " + ticketId);
  }
}
