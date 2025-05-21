package com.sparta.spring_deep._delivery.domain.order.repository;


import com.sparta.spring_deep._delivery.domain.order.model.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findAllByOrderId(UUID id);
}
