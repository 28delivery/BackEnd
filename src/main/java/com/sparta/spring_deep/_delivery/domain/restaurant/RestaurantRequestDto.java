package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantRequestDto {

    String name;
    CategoryEnum category;
    String roadAddr;
    String detailAddr;
    String phone;
}
