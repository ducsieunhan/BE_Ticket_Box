package com.ticket.box.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ticket.box.domain.require.ReqOrganizerDTO;
import com.ticket.box.domain.response.ResOrganizerDTO;
import com.ticket.box.service.OrganizerService;
import com.ticket.box.util.annotation.ApiMessage;
import com.ticket.box.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/api/v1")
public class OrganizerController {
    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @GetMapping("/organizers")
    @ApiMessage("get all organizers")
    public ResponseEntity<List<ResOrganizerDTO>> getAllOrganizers() {
        List<ResOrganizerDTO> reqOrganizerDTO = this.organizerService.getAllOrganizers();
        return ResponseEntity.ok().body(reqOrganizerDTO);
    }

    @GetMapping("/organizer/{id}")
    @ApiMessage("get all organizers")
    public ResponseEntity<ResOrganizerDTO> getAllOrganizers(@PathVariable("id") Long id) throws IdInvalidException {
        ResOrganizerDTO reqOrganizerDTO = this.organizerService.getOrganizerById(id);
        return ResponseEntity.ok().body(reqOrganizerDTO);
    }

    @PostMapping("/organizer")
    @ApiMessage("create new organizer")
    public ResponseEntity<ResOrganizerDTO> createNewOrganizer(@RequestBody ReqOrganizerDTO reqOrganizerDTO) {
        // TODO: process POST request
        ResOrganizerDTO newOrganizer = this.organizerService.createNewOrganizer(reqOrganizerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrganizer);
    }

    @PutMapping("/organizer/{id}")
    @ApiMessage("update organizer by id")
    public ResponseEntity<ResOrganizerDTO> putMethodName(@PathVariable("id") Long id,
            @RequestBody ReqOrganizerDTO reqOrganizerDTO) throws IdInvalidException {
        // TODO: process PUT request
        ResOrganizerDTO updatedOrganizerDTO = this.organizerService.updateOrganizerById(id, reqOrganizerDTO);
        return ResponseEntity.ok().body(updatedOrganizerDTO);
    }

    @PutMapping("/organizer")
    @ApiMessage("update organizer by name")
    public ResponseEntity<ResOrganizerDTO> updateOrganizer(@RequestBody ReqOrganizerDTO reqOrganizerDTO)
            throws IdInvalidException {
        // TODO: process PUT request
        ResOrganizerDTO updatedOrganizerDTO = this.organizerService.updateOrganizer(reqOrganizerDTO);
        return ResponseEntity.ok().body(updatedOrganizerDTO);
    }

    @DeleteMapping("/organizer/{id}")
    @ApiMessage("delete organizer")
    public ResponseEntity<String> deleteOrganizer(@PathVariable("id") Long id) {
        this.organizerService.deleteOrganizer(id);
        return ResponseEntity.ok().body("deleted");
    }
}
