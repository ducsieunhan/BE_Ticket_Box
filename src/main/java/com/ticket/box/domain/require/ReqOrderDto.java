package com.ticket.box.domain.require;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderDto {

  @PositiveOrZero(message = "Total price must be positive")
  private double totalPrice;

  @NotEmpty(message = "Receiver name is required")
  private String receiverName;

  @NotEmpty(message = "Receiver email is required")
  @Email(message = "Invalid email format")
  private String receiverEmail;

  @Pattern(regexp = "\\d{10,15}")
  private String receiverPhone;

  @Valid
  @NotEmpty(message = "At least one item is required")
  private List<ReqOrderDetailDto> items;

}
