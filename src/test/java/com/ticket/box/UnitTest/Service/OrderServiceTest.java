package com.ticket.box.UnitTest.Service;

import com.ticket.box.domain.Order;
import com.ticket.box.domain.OrderDetail;
import com.ticket.box.domain.User;
import com.ticket.box.domain.require.ReqOrderDetailDto;
import com.ticket.box.domain.require.ReqOrderDto;
import com.ticket.box.domain.response.ResUserOrder;
import com.ticket.box.repository.OrderDetailRepository;
import com.ticket.box.repository.OrderRepository;
import com.ticket.box.repository.UserRepository;
import com.ticket.box.service.OrderDetailService;
import com.ticket.box.service.OrderService;
import com.ticket.box.service.UserService;
import com.ticket.box.util.constant.StatusOrderEnum;
import com.ticket.box.util.error.IdInvalidException;
import com.ticket.box.util.error.OrderNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderDetailRepository orderDetailRepository;

  @Mock
  private OrderDetailService orderDetailService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private OrderService orderService;

  private User currentUser;
  private Order order;

  @BeforeEach
  public void setUp(){
    currentUser = new User();
    currentUser.setId(1L);
    currentUser.setName("ducsieunhan");
    currentUser.setEmail("ducsieunhan@gmail.com");

    order = new Order();
    order.setId(1L);
    order.setUser(currentUser);
    order.setTotalPrice(2000);
    order.setReceiverName("ducsieunhan");
    order.setOrderDetails(List.of());
  }

  @Test
  public void shouldReturnOrderWhenOrderIdExists(){
    Random rd = new Random();
    Long number = rd.nextLong();
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(mock(Order.class)));

    Order order = this.orderService.handleGetAOrder(number);

    assertNotNull(order);
    verify(orderRepository).findById(any(Long.class));
  }

  @Test
  public void shouldThrowExceptionWhenOrderIdNotExists(){
    Random rd = new Random();
    Long number = rd.nextLong();
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.empty());

    OrderNotFoundException e = assertThrows(
            OrderNotFoundException.class,
            ()-> orderService.handleGetAOrder(number));

    assertEquals(String.format("Order not found with ID: %s", number), e.getMessage());

    verify(orderRepository).findById(any(Long.class));
  }

  @Test
  public void shouldCreateNewOrderByUser() throws IdInvalidException {
    when(userService.handleGetUserByUsername(any(String.class))).thenReturn(currentUser);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(orderDetailService.handleCreateNewItems(any(ReqOrderDetailDto.class), any(Order.class))).thenReturn(mock(OrderDetail.class));

    ReqOrderDto dto = new ReqOrderDto();
    dto.setReceiverName(currentUser.getName());
    ReqOrderDetailDto detailDto = new ReqOrderDetailDto();
    dto.setItems(List.of(detailDto));

    Order order1 = this.orderService.handleCreateNewOrder(dto);

    assertNotNull(order1);
    assertEquals(order1.getReceiverName(), order.getReceiverName());

    verify(userService).handleGetUserByUsername(any(String.class));
    verify(orderRepository, times(2)).save(any(Order.class));
    verify(orderDetailService).handleCreateNewItems(any(ReqOrderDetailDto.class), any(Order.class));

  }

  @Test
  public void shouldReturnOrdersOfUser() throws IdInvalidException {
    when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(currentUser));
    when(orderRepository.getByUserId(any(Long.class))).thenReturn(List.of(order));

    List<ResUserOrder> res = this.orderService.handleGetOrdersOfUser(currentUser.getId());

    assertNotNull(res);

    verify(userRepository).findById(any(Long.class));
    verify(orderRepository).getByUserId(any(Long.class));
  }

  @Test
  public void shouldUpdateOrderAfterPayment(){
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(any(Order.class));

    this.orderService.handleAfterPayment(order.getId());

    assertEquals(order.getStatus(), StatusOrderEnum.CONFIRMED);
    verify(orderRepository).findById(any(Long.class));
  }

  @Test
  public void shouldReturnErrorAfterPayment(){
    when(orderRepository.findById(any(Long.class))).thenReturn(Optional.empty());

    OrderNotFoundException e = assertThrows(
            OrderNotFoundException.class,
            () -> this.orderService.handleAfterPayment(order.getId())
    );
    assertEquals(String.format("Order not found with ID: %s", order.getId()),e.getMessage() );

    verify(orderRepository).findById(any(Long.class));
  }

}
