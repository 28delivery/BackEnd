package com.sparta.spring_deep._delivery.domain.order.repository;


import com.sparta.spring_deep._delivery.domain.order.model.Order;
import com.sparta.spring_deep._delivery.domain.order.model.Order.OrderStatusEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

    Optional<List<Order>> findAllByRestaurantId(UUID restaurantId);

    Optional<Order> findByIdAndIsDeletedFalse(UUID orderId);

    Page<Order> findByCustomerUsernameAndIsDeletedFalseAndUpdatedAtAfterAndStatusIn(String username,
        LocalDateTime lastCheckedTime,
        List<OrderStatusEnum> pending, Pageable pageable);
}
