package com.ticket.box.domain.require;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderDetailDto {
  private long orderId;
  private long ticketId;
  private double subTotal;
  private long quantity;
}
