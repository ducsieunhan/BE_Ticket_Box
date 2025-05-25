package com.ticket.box.UnitTest.Service;

import com.ticket.box.domain.Category;
import com.ticket.box.domain.Event;
import com.ticket.box.domain.Organizer;
import com.ticket.box.domain.Ticket;
import com.ticket.box.domain.request.ReqEventDTO;
import com.ticket.box.domain.response.ResEventDTO;
import com.ticket.box.domain.response.ResultPaginationDTO;
import com.ticket.box.repository.*;
import com.ticket.box.service.EventService;
import com.ticket.box.service.FileService;
import com.ticket.box.util.constant.StatusEventEnum;
import com.ticket.box.util.error.IdInvalidException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
  @Mock
  private EventRepository eventRepository;
  @Mock
  private OrganizerRepository organizerRepository;
  @Mock
  private TicketRepository ticketRepository;
  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  @Spy
  private EventService eventService;

  private ReqEventDTO reqEventDTO;
  private Event event;
  private ResEventDTO resEventDTO;


  @BeforeEach
  public void setUp(){
    ReqEventDTO.EventTicket eventTicket = new ReqEventDTO.EventTicket();
    eventTicket.setDescription("Vip");
    eventTicket.setQuantity(1000);

    reqEventDTO = new ReqEventDTO();
    reqEventDTO.setName("HelloSummer!");
    reqEventDTO.setCategory("Live music");
    reqEventDTO.setOrganizerName("ntpmm");
    reqEventDTO.setStatus(StatusEventEnum.OPEN);
    reqEventDTO.setTickets(List.of(eventTicket));

    Category category = new Category();
    category.setName("Live music");
    Organizer organizer = new Organizer();
    organizer.setEmail("ntpmm");
    organizer.setName("ntpmm");

    event = new Event();
    event.setName(reqEventDTO.getName());
    event.setCategory(category);
    event.setOrganizer(organizer);
    event.setStatus(reqEventDTO.getStatus());

    resEventDTO = new ResEventDTO();
    resEventDTO.setName(event.getName());
    resEventDTO.setStatus(event.getStatus());
    resEventDTO.setCategory(event.getCategory().getName());
    ResEventDTO.ResOrganizer resOrganizer = new ResEventDTO.ResOrganizer();
    resOrganizer.setName(event.getOrganizer().getName());
    resEventDTO.setOrganizer(resOrganizer);
  }

  @Test
  @DisplayName("Test create new event")
  public void shouldNewEventCreated() throws DataFormatException, IdInvalidException {
    when(categoryRepository.findByName(any(String.class))).thenReturn(Optional.of(event.getCategory()));
    when(organizerRepository.findByName(any(String.class))).thenReturn(Optional.of(event.getOrganizer()));

    when(eventRepository.save(any(Event.class))).thenReturn(event);

    Ticket newTicket = new Ticket();
    newTicket.setEvent(event);
    newTicket.setDescription("Vip");
    newTicket.setQuantity(1000);

    when(ticketRepository.save(any(Ticket.class))).thenReturn(newTicket);
    ResEventDTO newEvent = this.eventService.handleCreateEvent(reqEventDTO);
    assertNotNull(newEvent);
    assertEquals(event.getName(), newEvent.getName());
    assertEquals(event.getStatus(), newEvent.getStatus());
    assertEquals(event.getCategory().getName(), newEvent.getCategory());
    assertEquals(event.getOrganizer().getName(), newEvent.getOrganizer().getName());
  }

  @Test
  public void shouldGetAllEvents() throws DataFormatException {
    Page<Event> pEvent  = new PageImpl<>(List.of(event));
    when(this.eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pEvent);
    when(organizerRepository.findByName(any(String.class))).thenReturn(Optional.of(event.getOrganizer()));


    Specification<Event> spec = ((root, query, cb) -> cb.equal(root.get("name"), "HelloSummer!"));
    Pageable pageable = PageRequest.of(1,10);

    ResultPaginationDTO rs = this.eventService.getAllEvents(spec, pageable);

    assertNotNull(rs);

    verify(eventRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    verify(eventService, times(List.of(event).size())).toResEventDTO(any(Event.class));
  }




}
