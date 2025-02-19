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
    private String categoryName;
    private String name;
    private String address;
    private String phone;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.ownerId = restaurant.getOwner().getUsername();
        this.categoryName = restaurant.getCategory().getName();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
    }

    public RestaurantResponseDto(UUID id, String ownerId, String categoryName, String name,
        String address, String phone) {
        this.id = id;
        this.ownerId = ownerId;
        this.categoryName = categoryName;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

}
