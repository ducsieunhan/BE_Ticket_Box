package com.ticket.box.domain.response;

import java.time.Instant;
import java.time.LocalDateTime;

import com.ticket.box.util.constant.StatusOrderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResOrderDTO {
  private long orderId;

  private double totalPrice;

  private StatusOrderEnum status;

  private String receiverName;

  private String receiverEmail;

  private String receiverPhone;

  private Instant createdAt;

  private UserLogin user;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserLogin {
    private String email;
    private String name;
  }
}
