package com.ticket.box.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ticket.box.domain.request.ReqEventDTO;
import com.ticket.box.domain.response.ResEventDTO;
import com.ticket.box.service.EventService;
import com.ticket.box.util.annotation.ApiMessage;
import com.ticket.box.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/api/v1")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    @ApiMessage("Get all events")
    public ResponseEntity<List<ResEventDTO>> getAllEvents() {
        List<ResEventDTO> res = this.eventService.getAllEvents();
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/event/{id}")
    @ApiMessage("Get event by id")
    public ResponseEntity<ResEventDTO> getEventById(@PathVariable("id") Long id) throws IdInvalidException {
        ResEventDTO res = this.eventService.getEventById(id);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/event/{id}")
    @ApiMessage("Delete event by id")
    public ResponseEntity<String> deleteEventById(@PathVariable("id") Long id) {
        this.eventService.handleDeleteEvent(id);
        return ResponseEntity.ok().body("Event deleted");
    }

    @PutMapping("/event/{id}")
    public ResponseEntity<ResEventDTO> updateEvent(@PathVariable("id") Long id, @RequestBody ReqEventDTO reqEventDTO)
            throws IdInvalidException {
        // TODO: process PUT request
        ResEventDTO res = this.eventService.handleUpdateEvent(reqEventDTO, id);
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/event")
    public ResponseEntity<ResEventDTO> createNewEvent(@RequestBody ReqEventDTO reqEventDTO) throws IdInvalidException {
        // TODO: process POST request
        ResEventDTO res = this.eventService.handleCreateEvent(reqEventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}
