package com.sparta.spring_deep._delivery.domain.order.orderDetails;

import com.sparta.spring_deep._delivery.domain.order.orderItem.OrderItemDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrderDetailsRequestDto {

    @NotNull
    private String restaurantId;

    @NotNull
    private String addressId;

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
