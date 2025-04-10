package com.ticket.box.domain;

import java.time.Instant;
import java.util.List;

import com.ticket.box.util.SecurityUtil;
import com.ticket.box.util.constant.StatusOrderEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private double totalPrice;

  @Enumerated(EnumType.STRING)
  private StatusOrderEnum status;

  @OneToMany(mappedBy = "order")
  private List<OrderDetail> orderDetails;

  private String receiverName;

  private String receiverEmail;

  private String receiverPhone;

  private Instant createdAt;
  private Instant updatedAt;
  private String createdBy;
  private String updatedBy;

  @PrePersist
  public void handleBeforeCreate() {
    this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";

    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
        ? SecurityUtil.getCurrentUserLogin().get()
        : "";

    this.updatedAt = Instant.now();
  }

}
