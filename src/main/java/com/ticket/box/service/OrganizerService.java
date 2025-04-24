package com.ticket.box.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticket.box.domain.Organizer;
import com.ticket.box.domain.request.ReqEventDTO;
import com.ticket.box.domain.require.ReqOrganizerDTO;
import com.ticket.box.domain.response.ResOrganizerDTO;
import com.ticket.box.repository.OrderRepository;
import com.ticket.box.repository.OrganizerRepository;
import com.ticket.box.util.error.IdInvalidException;

@Service
public class OrganizerService {
    private OrganizerRepository organizerRepository;

    public OrganizerService(OrganizerRepository organizerRepository) {
        this.organizerRepository = organizerRepository;
    }

    public ResOrganizerDTO castToResOrganizer(Organizer organizer) {
        ResOrganizerDTO res = new ResOrganizerDTO(organizer.getId(), organizer.getName(), organizer.getEmail(),
                organizer.getPhone(), organizer.getDescription());
        return res;
    }

    public Organizer castFromReqToEntity(ReqOrganizerDTO reqOrganizerDTO) {
        Organizer organizer = new Organizer();
        organizer.setDescription(reqOrganizerDTO.getDescription());
        organizer.setEmail(reqOrganizerDTO.getEmail());
        organizer.setName(reqOrganizerDTO.getName());
        organizer.setPhone(reqOrganizerDTO.getPhone());
        return organizer;
    }

    public ResOrganizerDTO createNewOrganizer(ReqOrganizerDTO reqOrganizerDTO) {
        Organizer organizer = this.organizerRepository.save(castFromReqToEntity(reqOrganizerDTO));
        return castToResOrganizer(organizer);
    }

    public List<ResOrganizerDTO> getAllOrganizers() {
        List<Organizer> organizers = this.organizerRepository.findAll();
        List<ResOrganizerDTO> reqList = new ArrayList<>();
        for (Organizer req : organizers) {
            reqList.add(castToResOrganizer(req));
        }
        return reqList;
    }

    public ResOrganizerDTO getOrganizerById(Long id) throws IdInvalidException {
        Optional<Organizer> organizer = this.organizerRepository.findById(id);
        if (!organizer.isPresent()) {
            throw new IdInvalidException("Organizer is not exist.");
        }
        return castToResOrganizer(organizer.get());
    }

    public ResOrganizerDTO updateOrganizer(ReqOrganizerDTO reqOrganizerDTO) throws IdInvalidException {
        Optional<Organizer> organizer = this.organizerRepository.findByName(reqOrganizerDTO.getName());
        if (!organizer.isPresent()) {
            throw new IdInvalidException("Organizer is not exist.");
        }
        Organizer result = organizer.get();
        result.setEmail(reqOrganizerDTO.getEmail());
        result.setDescription(reqOrganizerDTO.getDescription());
        result.setPhone(reqOrganizerDTO.getPhone());
        this.organizerRepository.save(result);
        return castToResOrganizer(result);
    }

    public ResOrganizerDTO updateOrganizerById(Long id, ReqOrganizerDTO reqOrganizerDTO) throws IdInvalidException {
        Optional<Organizer> organizer = this.organizerRepository.findById(id);
        if (!organizer.isPresent()) {
            throw new IdInvalidException("Organizer is not exist.");
        }
        Organizer result = organizer.get();
        result.setName(reqOrganizerDTO.getName());
        result.setEmail(reqOrganizerDTO.getEmail());
        result.setDescription(reqOrganizerDTO.getDescription());
        result.setPhone(reqOrganizerDTO.getPhone());
        this.organizerRepository.save(result);
        return castToResOrganizer(result);
    }

    public void deleteOrganizer(Long id) {
        this.organizerRepository.deleteById(id);
    }
}
