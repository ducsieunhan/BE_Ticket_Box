package com.ticket.box.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ticket.box.domain.Event;
import com.ticket.box.domain.response.*;
import com.ticket.box.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.ticket.box.domain.Order;
import com.ticket.box.domain.OrderDetail;
import com.ticket.box.domain.User;
import com.ticket.box.domain.require.ReqOrderDetailDto;
import com.ticket.box.domain.require.ReqOrderDto;
import com.ticket.box.domain.require.ReqUpdateOrderDetail;
import com.ticket.box.domain.require.ReqUpdateOrderDto;
import com.ticket.box.repository.OrderDetailRepository;
import com.ticket.box.repository.OrderRepository;
import com.ticket.box.util.SecurityUtil;
import com.ticket.box.util.constant.StatusOrderEnum;
import com.ticket.box.util.error.IdInvalidException;
import com.ticket.box.util.error.OrderNotFoundException;

@Service
public class OrderService {
  private OrderRepository orderRepository;
  private UserService userService;
  private OrderDetailService orderDetailService;
  private OrderDetailRepository orderDetailRepository;

  private UserRepository userRepository;

  public OrderService(OrderRepository orderRepository, UserService userService, OrderDetailService orderDetailService,
      OrderDetailRepository orderDetailRepository, UserRepository userRepository) {
    this.orderRepository = orderRepository;
    this.userService = userService;
    this.orderDetailService = orderDetailService;
    this.orderDetailRepository = orderDetailRepository;
    this.userRepository = userRepository;
  }

  public Order handleCreateNewOrder(ReqOrderDto orderdDto) throws IdInvalidException {
    String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

    User currentUser = this.userService.handleGetUserByUsername(email);

    if (currentUser == null)
      throw new IdInvalidException("Not find this user");

    Order newOrder = new Order();
    newOrder.setReceiverName(orderdDto.getReceiverName());
    newOrder.setReceiverEmail(orderdDto.getReceiverEmail());
    newOrder.setReceiverPhone(orderdDto.getReceiverPhone());
    newOrder.setTotalPrice(orderdDto.getTotalPrice());
    newOrder.setStatus(StatusOrderEnum.PENDING);
    newOrder.setUser(currentUser);

    newOrder = this.orderRepository.save(newOrder);

    // order detail

    List<OrderDetail> items = new ArrayList<>();
    for (ReqOrderDetailDto dto : orderdDto.getItems()) {
      OrderDetail o = this.orderDetailService.handleCreateNewItems(dto, newOrder);
      items.add(o);
    }

    newOrder.setOrderDetails(items);

    return this.orderRepository.save(newOrder);
  }

  public Order handleUpdateAOrder(ReqUpdateOrderDto orderdDto) throws OrderNotFoundException {

    Order currentOrder = orderRepository.findById(orderdDto.getOrderId())
        .orElseThrow(() -> new OrderNotFoundException(orderdDto.getOrderId()));

    currentOrder.setTotalPrice(orderdDto.getTotalPrice());
    currentOrder.setReceiverName(orderdDto.getReceiverName());
    currentOrder.setReceiverEmail(orderdDto.getReceiverEmail());
    currentOrder.setReceiverPhone(orderdDto.getReceiverPhone());
    currentOrder.setStatus(orderdDto.getStatus());

    // order detail update

    List<OrderDetail> items = new ArrayList<>();
    for (ReqUpdateOrderDetail o : orderdDto.getItems()) {
      OrderDetail updated = this.orderDetailService.handleUpdateAOrderItems(o);
      items.add(updated);
    }

    return this.orderRepository.save(currentOrder);
  }

  public Order handleGetAOrder(long id) throws OrderNotFoundException {
    return this.orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
  }

  public List<ResUserOrder> handleGetOrdersOfUser(long id) throws IdInvalidException{
      User user = this.userRepository.findById(id)
              .orElseThrow(() -> new IdInvalidException("Not found user"));

      List<Order> orders = this.orderRepository.getByUserId(id);

      List<ResUserOrder> resOrders = new ArrayList<>();
      for (Order order : orders){
        ResUserOrder newOrder = handleCastToUserOrder(order);
        resOrders.add(newOrder);
      }
      return resOrders;
  }


  public void handleDeleteOrder(Order order) {
    List<OrderDetail> items = this.orderDetailRepository.findByOrderId(order.getId());
    for (OrderDetail o : items) {
      this.orderDetailService.handleDeleteItems(o);
    }
    this.orderRepository.delete(order);
  }

  public void handleCancelOrder(Order order) {
    order.setStatus(StatusOrderEnum.CANCELLED);
    this.orderRepository.save(order);
  }

  public void handleAfterPayment(Long orderId) {
    Optional<Order> optOrder = this.orderRepository.findById(orderId);
    if (!optOrder.isPresent()) {
      throw new OrderNotFoundException(orderId);
    }
    Order order = optOrder.get();
    order.setStatus(StatusOrderEnum.CONFIRMED);
    this.orderRepository.save(order);
  }

  public ResultPaginationDTO handleGetAllOrders(Specification<Order> spec, Pageable pageable) {
    Page<Order> pOrder = this.orderRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

    meta.setPage(pageable.getPageNumber());
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(pOrder.getTotalPages());
    meta.setTotal(pOrder.getTotalElements());

    rs.setMeta(meta);

    List<ResOrderDTO> orders = handleConvertToListResOrderDto(pOrder.getContent());

    rs.setResult(orders);

    return rs;
  }

  public ResOrderDTO handleCastToResOrderDTO(Order order) {
    ResOrderDTO orderDTO = new ResOrderDTO();

    orderDTO.setOrderId(order.getId());
    orderDTO.setReceiverEmail(order.getReceiverEmail());
    orderDTO.setReceiverName(order.getReceiverName());
    orderDTO.setReceiverPhone(order.getReceiverPhone());
    orderDTO.setTotalPrice(order.getTotalPrice());

    orderDTO.setStatus(order.getStatus());
    orderDTO.setCreatedAt(order.getCreatedAt());

    ResOrderDTO.UserLogin user = new ResOrderDTO.UserLogin();

    user.setEmail(order.getUser().getEmail());
    user.setName(order.getUser().getName());

    orderDTO.setUser(user);

    // order detail

    List<ResOrderDetailDTO> dto = new ArrayList<>();

    for (OrderDetail o : order.getOrderDetails()) {
      ResOrderDetailDTO converted = this.orderDetailService.convertToResOrderDetail(o);
      dto.add(converted);
    }

    orderDTO.setItems(dto);

    return orderDTO;
  }

  public ResUserOrder handleCastToUserOrder(Order order) {
    ResUserOrder orderDTO = new ResUserOrder();

    orderDTO.setOrderId(order.getId());
    orderDTO.setReceiverEmail(order.getReceiverEmail());
    orderDTO.setTotalPrice(order.getTotalPrice());

    orderDTO.setStatus(order.getStatus());
    orderDTO.setCreatedAt(order.getCreatedAt());


    // event

    if (!order.getOrderDetails().isEmpty()) {
      ResUserOrder.EventDTO eventDTO = getEventDTO(order);

      orderDTO.setEvent(eventDTO);
    }


    // order detail
    List<ResUserOrderDetail> dto = new ArrayList<>();


    for (OrderDetail o : order.getOrderDetails()) {
      ResUserOrderDetail converted = this.orderDetailService.convertToResUserOrderDetail(o);
      dto.add(converted);
    }
    orderDTO.setItems(dto);

    return orderDTO;
  }

  private static ResUserOrder.EventDTO getEventDTO(Order order) {
    Event event = order.getOrderDetails().get(0).getTicket().getEvent();
    ResUserOrder.EventDTO eventDTO = new ResUserOrder.EventDTO();
    eventDTO.setId(event.getId());
    eventDTO.setName(event.getName());
    eventDTO.setStartDate(event.getStartDate());
    eventDTO.setBanner(event.getBanner());
    eventDTO.setStatus(event.getStatus());
    eventDTO.setEndDate(event.getEndDate());
    eventDTO.setDistrict(event.getDistrict());
    eventDTO.setWard(event.getWard());
    eventDTO.setProvince(event.getProvince());
    eventDTO.setHouseNumber(event.getHouseNumber());
    return eventDTO;
  }

  public List<ResOrderDTO> handleConvertToListResOrderDto(List<Order> orders) {
    return orders.stream()
        .map(order -> {
          ResOrderDTO dto = handleCastToResOrderDTO(order);
          return dto;
        })
        .collect(Collectors.toList());
  }
}
