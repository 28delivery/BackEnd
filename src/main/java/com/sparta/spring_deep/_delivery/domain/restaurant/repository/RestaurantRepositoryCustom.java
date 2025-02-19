package com.sparta.spring_deep._delivery.domain.restaurant.repository;

import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {

    Page<Restaurant> searchByOption(Pageable pageable, UUID id, String restaurantName,
        Category category);
}
