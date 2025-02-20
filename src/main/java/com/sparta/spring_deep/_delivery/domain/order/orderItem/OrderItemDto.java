package com.sparta.spring_deep._delivery.domain.order.orderItem;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    @NotNull
    private String menuId;

    @NotNull
    @Min(1)
    private int quantity;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    public UUID getMenuId() {
        return UUID.fromString(menuId);
    }
}
