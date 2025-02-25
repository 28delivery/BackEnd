package com.sparta.spring_deep._delivery.domain.order.orderDetails;

import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponseDto {

    private String id;
    private String customerId;
    private String restaurantId;
    private String addressId;
    private OrderStatusEnum status;
    private BigDecimal totalPrice;
    private String request;
    private List<OrderItem> orderItems;

    public OrderDetailsResponseDto(Order order, List<OrderItem> orderItems) {
        this.id = order.getId().toString();
        this.customerId = order.getCustomer().getUsername();
        this.restaurantId = order.getRestaurant().getId().toString();
        this.addressId = order.getAddress().getId().toString();
        this.status = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.request = order.getRequest();
        this.orderItems = orderItems;
    }

}
