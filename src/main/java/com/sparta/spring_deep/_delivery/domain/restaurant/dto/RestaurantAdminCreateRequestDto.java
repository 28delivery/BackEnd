package com.sparta.spring_deep._delivery.domain.restaurant.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestaurantAdminCreateRequestDto {

    private String ownerId;
    private String name;
    private UUID categoryId;
    private String address;
    private String phone;
}
