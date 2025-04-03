package com.ticket.box.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqUserDTO {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    @Pattern(regexp = "^.{10,15}$", message = "Phone number must be 10-15 characters long")
    private String phone;

    private String dob;

    private String province;

    private String district;

    private String ward;

    private String houseNumber;
    private Long role;

}
