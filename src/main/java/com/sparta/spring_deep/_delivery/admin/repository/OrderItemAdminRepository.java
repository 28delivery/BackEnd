package com.sparta.spring_deep._delivery.admin.repository;


import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemAdminRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findAllByOrderId(UUID id);
}
