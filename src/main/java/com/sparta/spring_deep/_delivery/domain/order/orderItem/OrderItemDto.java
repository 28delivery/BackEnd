package com.sparta.spring_deep._delivery.domain.order.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
