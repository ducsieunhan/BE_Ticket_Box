package com.ticket.box.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticket.box.domain.Order;
import com.ticket.box.domain.User;
import com.ticket.box.domain.require.ReqOrderDto;
import com.ticket.box.domain.response.ResOrderDTO;
import com.ticket.box.repository.OrderRepository;
import com.ticket.box.util.SecurityUtil;
import com.ticket.box.util.constant.StatusOrderEnum;
import com.ticket.box.util.error.IdInvalidException;
import com.ticket.box.util.error.OrderNotFoundException;

@Service
public class OrderService {
  private OrderRepository orderRepository;
  private UserService userService;

  public OrderService(OrderRepository orderRepository, UserService userService) {
    this.orderRepository = orderRepository;
    this.userService = userService;
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

    return this.orderRepository.save(newOrder);
  }

  public Order handleGetAOrder(long id) throws OrderNotFoundException {
    return this.orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
  }

  public void handleDeleteOrder(Order order) {
    this.orderRepository.delete(order);
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

    return orderDTO;
  }
}
