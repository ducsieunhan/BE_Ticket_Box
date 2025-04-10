package com.ticket.box.domain.require;

import java.util.List;

import com.ticket.box.util.constant.StatusOrderEnum;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateOrderDto {

  @Positive(message = "Items id must be not null")
  private long id;

  @PositiveOrZero(message = "Total price must be positive")
  private double totalPrice;

  @NotEmpty(message = "Receiver name is required")
  private String receiverName;

  @NotEmpty(message = "Receiver email is required")
  @Email(message = "Invalid email format")
  private String receiverEmail;

  @Pattern(regexp = "\\d{10,15}")
  private String receiverPhone;

  @Enumerated(EnumType.STRING)
  @Column(name = "StatusOrderEnum")
  private StatusOrderEnum status;

  @Valid
  @NotEmpty(message = "At least one item is required")
  private List<ReqUpdateOrderDetail> items;

}
