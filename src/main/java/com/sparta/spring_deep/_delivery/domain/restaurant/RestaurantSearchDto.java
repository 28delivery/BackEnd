package com.sparta.spring_deep._delivery.domain.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RestaurantSearchDto {

    private String name;
    private CategoryEnum category;
    private String phone;
}
