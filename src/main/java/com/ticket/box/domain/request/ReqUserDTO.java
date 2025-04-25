package com.ticket.box.domain.request;

import com.ticket.box.domain.User;

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
    @Size(min = 6, message = "Password must be at least 6 characters long")
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
    private String avatar;

    public static ReqUserDTO fromEntity(User user) {
        ReqUserDTO userDTO = new ReqUserDTO();

        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setPassword(user.getPassword()); // Note: Be careful with password in DTOs
        userDTO.setPhone(user.getPhone());
        userDTO.setDob(user.getDob());
        userDTO.setProvince(user.getProvince());
        userDTO.setDistrict(user.getDistrict());
        userDTO.setWard(user.getWard());
        userDTO.setHouseNumber(user.getHouseNumber());

        if (user.getRole() != null) {
            userDTO.setRole(user.getRole().getId());
        } else {
            userDTO.setRole((long) 1);
        }

        return userDTO;
    }
}
