package com.ticket.box.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResOrganizerDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String description;
}
