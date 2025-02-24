package com.sparta.spring_deep._delivery.domain.restaurant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {

    Page<RestaurantResponseDto> searchByOptionAndIsDeletedFalse(
        RestaurantSearchDto restaurantSearchDto, Pageable pageable);
}
