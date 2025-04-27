package com.ticket.box.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {

    private long id;

    private String email;

    private String name;

    private String password;
    private String phone;
    private String dob;
    private String province;
    private String district;
    private String ward;
    private String houseNumber;
    private String role;
    private String avatar;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }

}
