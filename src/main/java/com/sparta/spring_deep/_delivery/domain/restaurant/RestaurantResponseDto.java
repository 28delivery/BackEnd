package com.sparta.spring_deep._delivery.domain.restaurant;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RestaurantResponseDto {

    private UUID id;
    private String ownerId;
    private String category;
    private String name;
    private String phone;
    private String roadAddr;
    private String jibunAddr;
    private String detailAddr;
    private String engAddr;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.ownerId = restaurant.getOwner().getUsername();
        this.category = restaurant.getCategory().getLabel();
        this.name = restaurant.getName();
        this.phone = restaurant.getPhone();
        this.roadAddr = restaurant.getRestaurantAddress().getRoadAddr();
        this.jibunAddr = restaurant.getRestaurantAddress().getJibunAddr();
        this.detailAddr = restaurant.getRestaurantAddress().getDetailAddr();
        this.engAddr = restaurant.getRestaurantAddress().getEngAddr();
    }

}
