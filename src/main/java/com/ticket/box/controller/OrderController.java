package com.ticket.box.controller;

import java.util.List;

import com.ticket.box.domain.response.ResUserOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ticket.box.domain.Order;
import com.ticket.box.domain.require.ReqOrderDto;
import com.ticket.box.domain.require.ReqUpdateOrderDto;
import com.ticket.box.domain.response.ResOrderDTO;
import com.ticket.box.domain.response.ResultPaginationDTO;
import com.ticket.box.service.OrderService;
import com.ticket.box.util.annotation.ApiMessage;
import com.ticket.box.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
  private OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping("/orders")
  @ApiMessage("Create new order")
  public ResponseEntity<ResOrderDTO> createNewOrder(@Valid @RequestBody ReqOrderDto order) {
    try {
      Order newOrder = this.orderService.handleCreateNewOrder(order);
      return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.handleCastToResOrderDTO(newOrder));

    } catch (IdInvalidException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
  }

  @PutMapping("/orders")
  @ApiMessage("Update a order")
  public ResponseEntity<ResOrderDTO> updateAOrder(@Valid @RequestBody ReqUpdateOrderDto order) {
    Order newOrder = this.orderService.handleUpdateAOrder(order);
    return ResponseEntity.status(HttpStatus.OK).body(this.orderService.handleCastToResOrderDTO(newOrder));
  }

  @DeleteMapping("/orders/{id}")
  @ApiMessage("Delete a order")
  public ResponseEntity<String> deleteAOrder(@PathVariable("id") long id) {
    Order currentOrder = this.orderService.handleGetAOrder(id);
    this.orderService.handleDeleteOrder(currentOrder);
    return ResponseEntity.ok("Successfully delete a order");
  }

  @GetMapping("/orders/{id}")
  @ApiMessage("Get a order")
  public ResponseEntity<ResOrderDTO> readAOrder(@PathVariable("id") long id) {
    Order currentOrder = this.orderService.handleGetAOrder(id);
    return ResponseEntity.ok().body(this.orderService.handleCastToResOrderDTO(currentOrder));
  }

  @GetMapping("/user_orders/{id}")
  @ApiMessage("Get user's orders")
  public ResponseEntity<List<ResUserOrder>> getUserOrders(@PathVariable("id") long id) throws IdInvalidException{
    try{
      return ResponseEntity.ok().body(this.orderService.handleGetOrdersOfUser(id));
    } catch (IdInvalidException e){
      throw new IdInvalidException("User not found");
    }
  }


  @GetMapping("/orders")
  @ApiMessage("Get all orders")
  public ResponseEntity<ResultPaginationDTO> readAllOrders(
      @Filter Specification<Order> spec, Pageable pageable) {
    return ResponseEntity.ok(this.orderService.handleGetAllOrders(spec, pageable));
  }

  @PutMapping("/orders/{id}")
  @ApiMessage("Update a order")
  public ResponseEntity<String> cancelOrder(@PathVariable("id") long id) {
    Order currentOrder = this.orderService.handleGetAOrder(id);
    this.orderService.handleCancelOrder(currentOrder);
    return ResponseEntity.ok("Successfully cancel a order");
  }

}
