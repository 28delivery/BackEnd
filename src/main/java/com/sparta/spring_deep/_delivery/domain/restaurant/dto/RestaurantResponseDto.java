package com.sparta.spring_deep._delivery.domain.restaurant.dto;

import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RestaurantResponseDto {

    private UUID id;
    private String ownerId;
    private UUID categoryId;
    private String name;
    private String address;
    private String phone;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.ownerId = restaurant.getOwner().getUsername();
        this.categoryId = restaurant.getCategory().getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
    }

}
