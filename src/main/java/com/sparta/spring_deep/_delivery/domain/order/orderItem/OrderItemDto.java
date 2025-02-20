package com.sparta.spring_deep._delivery.domain.order.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    public UUID getMenuId() {
        return UUID.fromString(menuId);
    }
}
