package com.ticket.box.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ticket.box.domain.Order;
import com.ticket.box.domain.OrderDetail;
import com.ticket.box.domain.Ticket;
import com.ticket.box.domain.require.ReqOrderDetailDto;
import com.ticket.box.domain.require.ReqUpdateOrderDetail;
import com.ticket.box.domain.response.ResOrderDTO;
import com.ticket.box.domain.response.ResOrderDetailDTO;
import com.ticket.box.domain.response.ResultPaginationDTO;
import com.ticket.box.repository.OrderDetailRepository;
import com.ticket.box.repository.OrderRepository;
import com.ticket.box.repository.TicketRepository;
import com.ticket.box.util.error.OrderNotFoundException;
import com.ticket.box.util.error.TicketNotFoundException;

@Service
public class OrderDetailService {

  private OrderDetailRepository orderDetailRepository;
  private OrderRepository orderRepository;
  private TicketRepository ticketRepository;

  public OrderDetailService(OrderDetailRepository orderDetailRepository, OrderRepository orderRepository,
      TicketRepository ticketRepository) {
    this.orderDetailRepository = orderDetailRepository;
    this.orderRepository = orderRepository;
    this.ticketRepository = ticketRepository;
  }

  public OrderDetail handleCreateNewItems(ReqOrderDetailDto dto)
      throws OrderNotFoundException, TicketNotFoundException {

    Order currentOrder = this.orderRepository.findById(dto.getOrderId())
        .orElseThrow(() -> new OrderNotFoundException(dto.getOrderId()));

    Ticket currentTicket = this.ticketRepository.findById(dto.getTicketId())
        .orElseThrow(() -> new TicketNotFoundException(dto.getTicketId()));

    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrder(currentOrder);
    orderDetail.setTicket(currentTicket);

    orderDetail.setPrice(dto.getSubTotal());
    orderDetail.setQuantity(dto.getQuantity());

    return this.orderDetailRepository.save(orderDetail);
  }

  public OrderDetail handleUpdateAOrderItems(ReqUpdateOrderDetail dto) throws OrderNotFoundException {
    OrderDetail currentOrder = this.orderDetailRepository.findById(dto.getOrderDetailId())
        .orElseThrow(() -> new OrderNotFoundException(dto.getOrderDetailId()));

    Ticket currentTicket = this.ticketRepository.findById(dto.getTicketId())
        .orElseThrow(() -> new TicketNotFoundException(dto.getTicketId()));

    currentOrder.setTicket(currentTicket);
    currentOrder.setQuantity(dto.getQuantity());
    currentOrder.setPrice(dto.getSubTotal());

    return currentOrder;

    // ????
  }

  public OrderDetail handleGetOrderItems(long id) throws OrderNotFoundException {
    return this.orderDetailRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
  }

  public ResultPaginationDTO handleGetAllOrderDetail(Specification<OrderDetail> spec, Pageable pageable) {
    Page<OrderDetail> items = this.orderDetailRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

    meta.setPage(pageable.getPageNumber());
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(items.getTotalPages());
    meta.setTotal(items.getTotalElements());

    rs.setMeta(meta);

    List<ResOrderDetailDTO> orderDetails = convertAllToResOrderDetail(items.getContent());

    rs.setResult(orderDetails);

    return rs;
  }

  public void handleDeleteItems(OrderDetail orderDetail) {
    this.orderDetailRepository.delete(orderDetail);
  }

  public ResOrderDetailDTO convertToResOrderDetail(OrderDetail orderDetail) {
    ResOrderDetailDTO dto = new ResOrderDetailDTO();
    ResOrderDetailDTO.TicketOrder ticket = new ResOrderDetailDTO.TicketOrder();

    dto.setId(orderDetail.getId());
    dto.setOrderId(orderDetail.getOrder().getId());
    dto.setPrice(orderDetail.getTicket().getPrice());
    dto.setQuantity(orderDetail.getQuantity());
    dto.setSubTotal(dto.getPrice() * dto.getQuantity());
    dto.setCreatedAt(orderDetail.getCreatedAt());

    ticket.setEventDate(null);
    ticket.setId(orderDetail.getTicket().getId());
    ticket.setName(orderDetail.getTicket().getDescription());

    dto.setTicket(ticket);

    return dto;

  }

  public List<ResOrderDetailDTO> convertAllToResOrderDetail(List<OrderDetail> orderDetails) {
    return orderDetails.stream().map(orderDetail -> {
      ResOrderDetailDTO dto = new ResOrderDetailDTO();
      ResOrderDetailDTO.TicketOrder ticket = new ResOrderDetailDTO.TicketOrder();

      dto.setId(orderDetail.getId());
      dto.setOrderId(orderDetail.getOrder().getId());
      dto.setPrice(orderDetail.getTicket().getPrice());
      dto.setQuantity(orderDetail.getQuantity());
      dto.setSubTotal(dto.getPrice() * dto.getQuantity());
      dto.setCreatedAt(orderDetail.getCreatedAt());

      ticket.setEventDate(null);
      ticket.setId(orderDetail.getTicket().getId());
      ticket.setName(orderDetail.getTicket().getDescription());

      dto.setTicket(ticket);
      return dto;
    }).collect(Collectors.toList());

  }

}
