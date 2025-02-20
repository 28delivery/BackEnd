package com.sparta.spring_deep._delivery.domain.order;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<UUID> findOrderIdsByRestaurantId(UUID restaurantId);

    Page<Order> findAllByCustomerUsernameAndIsDeletedFalse(String username, Pageable pageable);

    List<Order> findByCustomerUsernameAndUpdatedAtAfterAndStatusIn(String customerId,
        LocalDateTime lastCheckedTime, List<OrderStatusEnum> statusEnumList);

}
