package com.ticket.box.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ticket.box.domain.response.ResEventDTO;

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

  @Email(message = "Invalid email format")
  private String email;

  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  @Size(min = 6, message = "Password must be at least 6 characters long")
  private String password;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String refreshToken;

  @OneToMany(mappedBy = "user")
  private List<Order> orders;

  @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
  @Pattern(regexp = "^.{10,15}$", message = "Phone number must be 10-15 characters long")
  private String phone;

  private String dob;

  private String province;

  private String district;

  private String ward;

  private String houseNumber;

  private String avatar;
  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

}
