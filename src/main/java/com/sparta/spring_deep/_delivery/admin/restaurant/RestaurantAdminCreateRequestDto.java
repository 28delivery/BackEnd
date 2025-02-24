package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RestaurantAdminCreateRequestDto {

    private String ownerId;
    private String name;
    private CategoryEnum category;
    private String roadAddr;
    private String detailAddr;
    private String phone;
}
