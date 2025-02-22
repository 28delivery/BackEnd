package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant.CategoryEnum;
import java.util.UUID;

public class RestaurantAdminSearchDto {

    private UUID id;
    private String name;
    private CategoryEnum category;
    private String address;
    private String phone;

}
