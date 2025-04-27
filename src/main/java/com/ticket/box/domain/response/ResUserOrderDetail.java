package com.ticket.box.domain.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.Instant;

@Getter
@Setter
public class ResUserOrderDetail {
  private long orderDetailId;
  private TicketOrder ticket;
  private long quantity;
  private double price;
  private double subTotal;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class TicketOrder {
    private long ticketId;
    private String name;
  }
}
