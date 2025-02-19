package com.sparta.spring_deep._delivery.domain.order;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {

    @NotNull
    private String customerId;

    @NotNull
    private String restaurantId;

    @NotNull
    private String addressId;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalPrice;

    @Size(max = 50)
    private String request;

    public UUID getRestaurantId() {
        return UUID.fromString(restaurantId);
    }

    public UUID getAddressId() {
        return UUID.fromString(addressId);
    }


}
