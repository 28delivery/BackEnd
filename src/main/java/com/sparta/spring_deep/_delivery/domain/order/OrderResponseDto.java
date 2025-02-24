package com.sparta.spring_deep._delivery.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDto {

    private UUID id;
    private String customerId;
    private UUID restaurantId;
    private UUID addressId;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private String request;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.customerId = order.getCustomer().getUsername();
        this.restaurantId = order.getRestaurant().getId();
        this.addressId = order.getAddress().getId();
        this.status = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.request = order.getRequest();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
    }
}
