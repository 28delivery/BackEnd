package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class RestaurantResponseDto {

    private UUID id;
    private String ownerId;
    private CategoryEnum category;
    private String name;
    private String phone;
    private String roadAddr;
    private String jibunAddr;
    private String detailAddr;
    private String engAddr;

    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.category = restaurant.getCategory();
        this.name = restaurant.getName();
        this.phone = restaurant.getPhone();
        this.roadAddr = restaurant.getRestaurantAddress().getRoadAddr();
        this.jibunAddr = restaurant.getRestaurantAddress().getJibunAddr();
        this.detailAddr = restaurant.getRestaurantAddress().getDetailAddr();
        this.engAddr = restaurant.getRestaurantAddress().getEngAddr();
    }

    public RestaurantResponseDto(UUID id, CategoryEnum category, String name, String phone,
        String roadAddr, String jibunAddr, String detailAddr, String engAddr) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.phone = phone;
        this.roadAddr = roadAddr;
        this.jibunAddr = jibunAddr;
        this.detailAddr = detailAddr;
        this.engAddr = engAddr;
    }

}
