package com.sparta.spring_deep._delivery.admin.order;

import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderAdminResponseDto {

    private UUID id;
    private String customerId;
    private UUID restaurantId;
    private String restaurantName;
    private UUID addressId;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private String request;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
    private String deletedBy;

}
