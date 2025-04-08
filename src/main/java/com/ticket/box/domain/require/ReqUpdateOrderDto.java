package com.ticket.box.domain.require;

import java.util.List;

import com.ticket.box.util.constant.StatusOrderEnum;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateOrderDto {
  private long id;

  private double totalPrice;

  private String receiverName;

  private String receiverEmail;

  @Pattern(regexp = "\\d{10,15}")
  private String receiverPhone;

  private StatusOrderEnum status;

  private List<ReqUpdateOrderDetail> items;

}
