package com.ticket.box.controller;

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

import com.ticket.box.domain.Order;
import com.ticket.box.domain.OrderDetail;
import com.ticket.box.domain.require.ReqOrderDetailDto;
import com.ticket.box.domain.require.ReqUpdateOrderDetail;
import com.ticket.box.domain.require.ReqUpdateOrderDto;
import com.ticket.box.domain.response.ResOrderDTO;
import com.ticket.box.domain.response.ResOrderDetailDTO;
import com.ticket.box.domain.response.ResultPaginationDTO;
import com.ticket.box.service.OrderDetailService;
import com.ticket.box.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrderDetailController {
  private OrderDetailService orderDetailService;

  public OrderDetailController(OrderDetailService orderDetailService) {
    this.orderDetailService = orderDetailService;
  }

  @PostMapping("/order_detail")
  @ApiMessage("Create new order detail items")
  public ResponseEntity<ResOrderDetailDTO> createNewOrderItems(@Valid @RequestBody ReqOrderDetailDto item) {
    OrderDetail newOD = this.orderDetailService.handleCreateNewItems(item);
    return ResponseEntity.status(HttpStatus.CREATED).body(this.orderDetailService.convertToResOrderDetail(newOD));
  }

  @PutMapping("/order_detail")
  @ApiMessage("Update a order")
  public ResponseEntity<ResOrderDetailDTO> updateAOrderItems(@Valid @RequestBody ReqUpdateOrderDetail item) {
    OrderDetail current = this.orderDetailService.handleUpdateAOrderItems(item);
    return ResponseEntity.status(HttpStatus.OK).body(this.orderDetailService.convertToResOrderDetail(current));
  }

  @GetMapping("/order_detail/{id}")
  @ApiMessage("Read an order detail items")
  public ResponseEntity<ResOrderDetailDTO> readAnOrderDetail(@PathVariable("id") long id) {
    OrderDetail current = this.orderDetailService.handleGetOrderItems(id);
    return ResponseEntity.status(HttpStatus.CREATED).body(this.orderDetailService.convertToResOrderDetail(current));
  }

  @DeleteMapping("/order_detail/{id}")
  @ApiMessage("Read an order detail items")
  public ResponseEntity<String> deleteAnOrderDetail(@PathVariable("id") long id) {
    OrderDetail current = this.orderDetailService.handleGetOrderItems(id);
    this.orderDetailService.handleDeleteItems(current);
    return ResponseEntity.status(HttpStatus.CREATED).body("Successfully delete items");
  }

  @GetMapping("/order_detail")
  @ApiMessage("Get all order items")
  public ResponseEntity<ResultPaginationDTO> readAllOrderDetails(
      @Filter Specification<OrderDetail> spec, Pageable pageable) {
    return ResponseEntity.ok(this.orderDetailService.handleGetAllOrderDetail(spec, pageable));
  }

}
