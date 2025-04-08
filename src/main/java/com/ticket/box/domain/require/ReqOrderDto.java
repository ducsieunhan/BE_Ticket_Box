package com.ticket.box.domain.require;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderDto {

  private double totalPrice;

  private String receiverName;

  private String receiverEmail;

  @Pattern(regexp = "\\d{10,15}")
  private String receiverPhone;

  private List<ReqOrderDetailDto> items;

}
