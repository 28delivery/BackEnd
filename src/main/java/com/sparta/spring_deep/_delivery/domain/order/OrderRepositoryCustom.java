package com.sparta.spring_deep._delivery.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> searchOrders(String username, Pageable pageable, String menu, String restaurant);

}
