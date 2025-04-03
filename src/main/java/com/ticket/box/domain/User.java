package com.ticket.box.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter

public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String email;

  private String name;

  private String password;

  private String phone;

  private String dob;

  private String refreshToken;

  private String province;

  private String district;

  private String ward;

  private String houseNumber;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;
}
