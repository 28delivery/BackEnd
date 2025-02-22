package com.sparta.spring_deep._delivery.domain.order;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<List<Order>> findAllByRestaurantId(UUID restaurantId);

    Page<Order> findAllByCustomerUsernameAndIsDeletedFalse(String username, Pageable pageable);

    Page<Order> findByCustomerUsernameAndUpdatedAtAfterAndStatusIn(String customerId,
        String restaurantName, String menuName, String status,
        Pageable pageable);

    List<Order> findAllByRestaurantIdAndIsDeletedFalse(UUID restaurantId);

    Optional<Order> findByIdAndIsDeletedFalse(UUID orderId);

    Page<Order> findByCustomerUsernameAndIsDeletedFalseAndUpdatedAtAfterAndStatusIn(String username,
        LocalDateTime lastCheckedTime,
        List<OrderStatusEnum> pending, Pageable pageable);
}
