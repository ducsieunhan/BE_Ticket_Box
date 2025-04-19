package com.ticket.box.util.error;

public class TicketQuantityNotAvail extends ArrayIndexOutOfBoundsException {
  public TicketQuantityNotAvail(String message) {
    super(message);
  }

}
