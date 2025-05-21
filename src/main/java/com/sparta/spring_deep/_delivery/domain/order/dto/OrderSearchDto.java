package com.sparta.spring_deep._delivery.domain.order.dto;

import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OrderSearchDto {

    private String restaurantName;
    private String menuName;
    private OrderStatusEnum status;

}
