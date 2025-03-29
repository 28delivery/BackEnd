package com.sparta.spring_deep._delivery.domain.order.repository;

import com.sparta.spring_deep._delivery.domain.order.dto.OrderSearchDto;
import com.sparta.spring_deep._delivery.domain.order.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<OrderResponseDto> searchMyOrdersByOptionAndIsDeletedFalse(String username,
        OrderSearchDto searchDto, Pageable pageable);
}
