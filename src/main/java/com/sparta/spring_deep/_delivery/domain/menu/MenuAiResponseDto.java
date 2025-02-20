package com.sparta.spring_deep._delivery.domain.menu;

import java.util.UUID;
import lombok.Data;

@Data
public class MenuAiResponseDto {

    private UUID menuId;
    private String description;

    public MenuAiResponseDto(UUID menuId, String description) {
        this.menuId = menuId;
        this.description = description;
    }

}
