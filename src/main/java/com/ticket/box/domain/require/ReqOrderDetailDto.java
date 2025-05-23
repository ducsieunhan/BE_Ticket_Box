package com.ticket.box.domain.require;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderDetailDto {

  private long orderId;

  @Positive(message = "Ticket Id must be not null")
  private long ticketId;

  @PositiveOrZero(message = "Sub total price must be positive ")
  private double subTotal;

  @Positive(message = "Quantity must be positive")
  private long quantity;
}
