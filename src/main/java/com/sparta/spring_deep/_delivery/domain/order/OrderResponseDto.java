package com.sparta.spring_deep._delivery.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDto {

    private String id;
    private String customerId;
    private String restaurantId;
    private String addressId;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private String request;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;


    public OrderResponseDto(Order order) {
        this.id = order.getId().toString();
        this.customerId = order.getCustomer().getUsername();
        this.restaurantId = order.getRestaurant().getId().toString();
        this.addressId = order.getAddress().getId().toString();
        this.status = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.request = order.getRequest();
        this.createdAt = order.getCreatedAt();
        this.createdBy = order.getCreatedBy();
        this.updatedAt = order.getUpdatedAt();
        this.updatedBy = order.getUpdatedBy();
    }
}
