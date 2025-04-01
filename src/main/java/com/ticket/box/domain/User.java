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

  @NotBlank(message = "Email must not be blank")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Name must not be blank")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  private String name;

  @NotBlank(message = "Password must not be blank")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @NotBlank(message = "Phone number must not be blank")
  @Pattern(regexp = "\\d{10,15}", message = "Phone number must be between 10 and 15 characters")
  private String phone;

  @NotBlank(message = "Date of birth must not be blank")
  private String dob;

  private String refreshToken;

  @NotBlank(message = "Province must not be blank")
  private String province;

  @NotBlank(message = "District must not be blank")
  private String district;

  @NotBlank(message = "Ward must not be blank")
  private String ward;

  @NotBlank(message = "House number must not be blank")
  private String houseNumber;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;
}
