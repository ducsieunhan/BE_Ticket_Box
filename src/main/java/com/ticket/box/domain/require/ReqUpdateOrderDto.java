package com.ticket.box.domain.require;

import com.ticket.box.util.constant.StatusOrderEnum;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateOrderDto {
  private long id;

  private String receiverName;

  private String receiverEmail;

  @Pattern(regexp = "\\d{10,15}")
  private String receiverPhone;

  private StatusOrderEnum status;

}
