package com.ticket.box.domain.require;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateOrderDetail {

  @Positive(message = "Order detail id must be not null")
  private long orderDetailId;

  @Valid
  private TicketDetail ticket;

  @PositiveOrZero(message = "Sub total price must be positive ")
  private double subTotal;

  @Positive(message = "Quantity must be positive")
  private long quantity;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TicketDetail {
    @Positive(message = "Ticket Id must be not null")
    private long ticketId;
  }
}
