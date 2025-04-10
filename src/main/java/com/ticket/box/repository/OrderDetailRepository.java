package com.ticket.box.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ticket.box.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, JpaSpecificationExecutor<OrderDetail> {

  List<OrderDetail> findByOrderId(long id);
}
