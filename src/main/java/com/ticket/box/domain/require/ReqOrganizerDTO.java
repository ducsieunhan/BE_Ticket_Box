package com.ticket.box.domain.require;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqOrganizerDTO {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    @Email(message = "Invalid email format")
    private String email;
    @Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits")
    @Pattern(regexp = "^.{10,15}$", message = "Phone number must be 10-15 characters long")
    private String phone;
    private String description;
}
