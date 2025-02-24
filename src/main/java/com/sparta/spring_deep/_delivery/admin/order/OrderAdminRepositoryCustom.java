package com.sparta.spring_deep._delivery.admin.order;

import com.sparta.spring_deep._delivery.domain.order.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderAdminRepositoryCustom {

    Page<OrderResponseDto> searchByOption(OrderAdminSearchDto searchDto, Pageable pageable);
}
