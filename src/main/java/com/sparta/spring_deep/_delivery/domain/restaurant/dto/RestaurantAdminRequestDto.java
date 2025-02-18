package com.sparta.spring_deep._delivery.domain.restaurant.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestaurantAdminRequestDto {

    String name;
    UUID categoryId;
    String address;
    String phone;

}
