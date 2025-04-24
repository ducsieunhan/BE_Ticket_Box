package com.ticket.box.domain.response;

import java.sql.Date;
import java.util.List;

import com.ticket.box.domain.Organizer;
import com.ticket.box.domain.Ticket;
import com.ticket.box.domain.User;
import com.ticket.box.util.constant.StatusEventEnum;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResEventDTO {
    private Long id;
    private String name;

    private Date startDate;
    private Date endDate;
    private String category;

    private String province;

    private String district;

    private String ward;

    private String houseNumber;
    private String imgEventInfo;
    private String banner;
    private String logo;

    private List<EventTicket> tickets;
    private List<Participant> participants;
    private StatusEventEnum status;

    private ResOrganizer organizer;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventTicket {
        private Long id;
        private String type;

        private double price;

        private long quantity;

        private String description;
        private Long sold;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResOrganizer {
        private Long id;
        private String name;

        private String email;

        private String phone;

        private String description;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Participant {
        private Long id;
        private String name;
        private String email;
        private String phone;
    }

}
