package com.sparta.spring_deep._delivery.domain.menu;

import lombok.Data;

@Data
public class MenuAiResponseDto {

    private String menuId;
    private String description;

    public MenuAiResponseDto(String menuId, String description) {
        this.menuId = menuId;
        this.description = description;
    }

}
