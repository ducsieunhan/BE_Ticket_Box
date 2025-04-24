package com.ticket.box.domain.request;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ticket.box.domain.Event;
import com.ticket.box.domain.Organizer;
import com.ticket.box.domain.Ticket;
import com.ticket.box.util.constant.StatusEventEnum;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqEventDTO {

    private String name;
    private StatusEventEnum status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String category;

    private String province;

    private String district;

    private String ward;

    private String houseNumber;
    private String imgEventInfo;
    private String banner;
    private String logo;

    private String organizerName;
    private String organizerInfo;
    @Size(min = 1, message = "The tickets list must contain at least 1 ticket")
    private List<EventTicket> tickets;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventTicket {
        @Size(min = 2, max = 50, message = "Type name must be between 2 and 50 characters")
        private String type;

        private double price;

        @Min(value = 1, message = "Quantity must be bigger than 1")
        private long quantity;

        @Column(columnDefinition = "MEDIUMTEXT")
        private String description;
    }

}
