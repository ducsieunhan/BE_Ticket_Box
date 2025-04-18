package com.ticket.box.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ticket.box.domain.Category;
import com.ticket.box.domain.Event;
import com.ticket.box.domain.Organizer;
import com.ticket.box.domain.Ticket;
import com.ticket.box.domain.User;
import com.ticket.box.domain.request.ReqEventDTO;
import com.ticket.box.domain.request.ReqEventDTO.EventTicket;
import com.ticket.box.domain.response.ResEventDTO;
import com.ticket.box.repository.CategoryRepository;
import com.ticket.box.repository.EventRepository;
import com.ticket.box.repository.OrganizerRepository;
import com.ticket.box.repository.TicketRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.util.constant.StatusEventEnum;
import com.ticket.box.util.error.IdInvalidException;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final CategoryRepository categoryRepository;

    public EventService(EventRepository eventRepository, OrganizerRepository organizerRepository,
            TicketRepository ticketRepository, UserRepository userRepository,
            FileService fileService, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.categoryRepository = categoryRepository;
    }

    public List<ResEventDTO> getAllEvents() {
        List<Event> events = this.eventRepository.findAll();
        List<ResEventDTO> results = events.stream()
                .map(event -> toResEventDTO(event))
                .collect(Collectors.toList());
        return results;
    }

    public ResEventDTO getEventById(Long id) throws IdInvalidException {
        Optional<Event> event = this.eventRepository.findById(id);
        if (!event.isPresent()) {
            throw new IdInvalidException("Event is not exist");
        }
        ResEventDTO res = toResEventDTO(event.get());
        return res;
    }

    public ResEventDTO handleCreateEvent(ReqEventDTO reqEvent) throws IdInvalidException {
        Optional<Category> currCategory = this.categoryRepository
                .findByName(reqEvent.getCategory());
        Optional<Organizer> optOrganizer = this.organizerRepository.findByName(reqEvent.getOrganizerName());
        if (!optOrganizer.isPresent()) {
            Organizer newOrganizer = new Organizer();
            newOrganizer.setName(reqEvent.getOrganizerName());
            newOrganizer.setDescription(reqEvent.getOrganizerInfo());
            ;
            this.organizerRepository.save(newOrganizer);
        }
        if (!currCategory.isPresent()) {
            Category newCategory = new Category();
            newCategory.setName(reqEvent.getCategory());
            this.categoryRepository.save(newCategory);
        }
        Event optEvent = fromReqDtoToEvent(reqEvent);
        List<EventTicket> tickets = reqEvent.getTickets();
        optEvent.setStatus(StatusEventEnum.WAITING_FOR_SALE);
        Event currEvent = this.eventRepository.save(optEvent);
        for (EventTicket ticket : tickets) {
            Ticket newTicket = new Ticket();
            newTicket.setDescription(ticket.getDescription());
            newTicket.setEvent(currEvent);
            newTicket.setPrice(ticket.getPrice());
            newTicket.setQuantity(ticket.getQuantity());
            newTicket.setSold(0);
            newTicket.setType(ticket.getType());
            this.ticketRepository.save(newTicket);
        }
        return toResEventDTO(currEvent);
    }

    public ResEventDTO handleUpdateEvent(ReqEventDTO reqEvent, Long id) throws IdInvalidException {
        Optional<Category> currCategory = this.categoryRepository
                .findByName(reqEvent.getCategory());
        if (!currCategory.isPresent()) {
            Category newCategory = new Category();
            newCategory.setName(reqEvent.getCategory());
            this.categoryRepository.save(newCategory);
        }
        Event optEvent = fromReqDtoToEvent(reqEvent);
        optEvent.setId(id);
        Event currEvent = this.eventRepository.save(optEvent);
        return toResEventDTO(currEvent);
    }

    public void handleDeleteEvent(Long id) {
        // delete other table also
        this.eventRepository.deleteById(id);
    }

    public List<User> findAllParticipants(Long eventId) {
        return this.userRepository.findUsersByEventId(eventId);
    }

    public ResEventDTO toResEventDTO(Event event) {
        ResEventDTO res = new ResEventDTO();
        List<ResEventDTO.EventTicket> tickets = new ArrayList<>();
        List<ResEventDTO.Participant> participants = new ArrayList<>();

        // Set basic event properties
        res.setId(event.getId());
        res.setName(event.getName());
        res.setBanner(event.getBanner());
        res.setCategory(event.getCategory().getName());
        res.setDistrict(event.getDistrict());
        res.setEndDate(event.getEndDate());
        res.setHouseNumber(event.getHouseNumber());
        res.setImgEventInfo(event.getImgEventInfo());
        res.setLogo(event.getLogo());
        res.setOrganizerName(event.getOrganizer().getName());
        res.setProvince(event.getProvince());
        res.setStartDate(event.getStartDate());
        res.setWard(event.getWard());
        res.setStatus(event.getStatus());
        List<Ticket> tList = event.getTickets();
        if (tList != null && !tList.isEmpty()) {
            for (Ticket ticket : tList) {
                ResEventDTO.EventTicket eventTicket = new ResEventDTO.EventTicket();
                eventTicket.setType(ticket.getType());
                eventTicket.setDescription(ticket.getDescription());
                eventTicket.setPrice(ticket.getPrice());
                eventTicket.setQuantity(ticket.getQuantity());
                eventTicket.setSold(ticket.getSold());
                eventTicket.setId(ticket.getId());
                tickets.add(eventTicket);
            }
        }
        res.setTickets(tickets);

        // res.setParticipants(...);
        List<User> users = findAllParticipants(event.getId());
        if (users != null && !users.isEmpty()) {
            for (User u : users) {
                ResEventDTO.Participant participant = new ResEventDTO.Participant();
                participant.setEmail(u.getEmail());
                participant.setName(u.getName());
                participant.setPhone(u.getPhone());
                participants.add(participant);
            }
        }
        res.setParticipants(participants);
        return res;
    }

    public Event fromReqDtoToEvent(ReqEventDTO reqEventDTO) throws IdInvalidException {
        // Optional<Event> optEvent = this.eventRepository.findById(id);
        Optional<Organizer> optOrganizer = this.organizerRepository.findByName(reqEventDTO.getOrganizerName());
        Optional<Category> currCategory = this.categoryRepository
                .findByName(reqEventDTO.getCategory());
        List<Ticket> tickets = new ArrayList<>();
        if (!currCategory.isPresent()) {
            throw new IdInvalidException("Category is not exist");
        }
        // if (!optEvent.isPresent()) {
        // throw new IdInvalidException("Event is not exist");
        // }
        if (!optOrganizer.isPresent()) {
            throw new IdInvalidException("Organizer is not exist");
        }
        Event event = new Event();

        event.setStatus(reqEventDTO.getStatus());
        event.setName(reqEventDTO.getName());
        event.setStartDate(reqEventDTO.getStartDate());
        event.setEndDate(reqEventDTO.getEndDate());
        event.setProvince(reqEventDTO.getProvince());
        event.setDistrict(reqEventDTO.getDistrict());
        event.setWard(reqEventDTO.getWard());
        event.setHouseNumber(reqEventDTO.getHouseNumber());
        event.setImgEventInfo(reqEventDTO.getImgEventInfo());
        event.setBanner(reqEventDTO.getBanner());
        event.setLogo(reqEventDTO.getLogo());
        event.setOrganizer(optOrganizer.get());
        event.setCategory(currCategory.get());
        // lack setTickets
        for (ReqEventDTO.EventTicket ticket : reqEventDTO.getTickets()) {
            Ticket t = new Ticket();
            t.setType(ticket.getType());
            t.setDescription(ticket.getDescription());
            t.setPrice(ticket.getPrice());
            t.setQuantity(ticket.getQuantity());

            tickets.add(t);
        }
        event.setTickets(tickets);
        return event;
    }

}
