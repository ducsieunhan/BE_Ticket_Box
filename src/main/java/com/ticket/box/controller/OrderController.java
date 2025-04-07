package com.ticket.box.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ticket.box.domain.Order;
import com.ticket.box.domain.require.ReqOrderDto;
import com.ticket.box.domain.response.ResOrderDTO;
import com.ticket.box.service.OrderService;
import com.ticket.box.util.annotation.ApiMessage;
import com.ticket.box.util.error.IdInvalidException;
import com.ticket.box.util.error.OrderNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
  private OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/order")
  @ApiMessage("Create new order")
  public ResponseEntity<ResOrderDTO> createNewOrder(@Valid @RequestBody ReqOrderDto order) {
    try {

      Order newOrder = this.orderService.handleCreateNewOrder(order);
      return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.handleCastToResOrderDTO(newOrder));

    } catch (IdInvalidException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
  }

  @DeleteMapping("/order/{id}")
  @ApiMessage("Delete a order")
  public ResponseEntity<String> deleteAOrder(@PathVariable("id") long id) {
    Order currentOrder = this.orderService.handleGetAOrder(id);
    this.orderService.handleDeleteOrder(currentOrder);
    return ResponseEntity.ok("Successfully delete a order");
  }
}
