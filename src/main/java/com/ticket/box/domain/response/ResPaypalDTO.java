package com.ticket.box.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResPaypalDTO {
    private String id;
    private String state;
    private String createTime;
    private String updateTime;
    private String intent;
    private String payerEmail;
    private String payerFirstName;
    private String payerLastName;
    private String currency;
    private String totalAmount;
}