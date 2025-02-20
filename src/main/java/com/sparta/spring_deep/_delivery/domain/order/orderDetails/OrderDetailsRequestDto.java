package com.sparta.spring_deep._delivery.domain.order.orderDetails;

import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItemDto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OrderDetailsRequestDto {

    @NotNull
    private String customerId;

    @NotNull
    private String restaurantId;

    @NotNull
    private String addressId;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Size(max = 50)
    private String request;

    @NotNull
    private List<OrderItemDto> orderItemDtos;

    public UUID getRestaurantId() {
        return UUID.fromString(restaurantId);
    }

    public UUID getAddressId() {
        return UUID.fromString(addressId);
    }
}
