package com.ticket.box.domain.response;

import java.sql.Date;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResOrderDetailDTO {
  private long orderDetailId;
  private long orderId;
  private TicketOrder ticket;
  private long quantity;
  private double price;
  private double subTotal;
  private Instant createdAt;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class TicketOrder {
    private long ticketId;
    private String name;
    private Date eventDate;
    private String event;
  }
}
