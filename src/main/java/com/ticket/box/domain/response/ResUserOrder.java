package com.ticket.box.domain.response;

import com.ticket.box.util.constant.StatusEventEnum;
import com.ticket.box.util.constant.StatusOrderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResUserOrder {
  private long orderId;

  private double totalPrice;

  private StatusOrderEnum status;

  private String receiverEmail;

  private Instant createdAt;

  private List<ResUserOrderDetail> items;

  private EventDTO event;

  @Getter
  @Setter
  public static class EventDTO {
    private long id ;
    private String name;
    private String banner ;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private StatusEventEnum status ;
    private String district;
    private String province;
    private String ward;
    private String houseNumber; 
  }

}
