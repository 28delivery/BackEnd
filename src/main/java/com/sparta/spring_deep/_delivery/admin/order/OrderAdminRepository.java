package com.sparta.spring_deep._delivery.admin.order;

import com.sparta.spring_deep._delivery.domain.order.Order;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAdminRepository extends JpaRepository<Order, UUID>,
    OrderAdminRepositoryCustom {

    // /admin/orders
    Page<Order> findAllByCustomerUsername(String username, Pageable pageable);

}
