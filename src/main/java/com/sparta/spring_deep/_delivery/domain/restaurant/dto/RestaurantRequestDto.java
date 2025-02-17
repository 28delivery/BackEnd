package com.sparta.spring_deep._delivery.domain.restaurant.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class RestaurantRequestDto {

    String name;
    UUID categoryId;
    String address;
    String phone;
}
