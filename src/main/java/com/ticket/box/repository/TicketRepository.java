package com.ticket.box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket.box.domain.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
