package com.sparta.spring_deep._delivery.admin.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantAdminRepositoryCustom {

    Page<RestaurantAdminResponseDto> searchByOption(
        RestaurantAdminSearchDto restaurantAdminSearchDto, Pageable pageable);
}
