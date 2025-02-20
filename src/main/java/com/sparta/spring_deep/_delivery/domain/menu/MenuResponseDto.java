package com.sparta.spring_deep._delivery.domain.menu;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class MenuResponseDto {

    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isHidden;

    public MenuResponseDto(Menu menu) {
        this.id = menu.getId();
        this.restaurantId = menu.getRestaurantId().getId();
        this.name = menu.getName();
        this.description = menu.getDescription();
        this.price = menu.getPrice();
        this.isHidden = menu.getIsHidden();
    }
}
