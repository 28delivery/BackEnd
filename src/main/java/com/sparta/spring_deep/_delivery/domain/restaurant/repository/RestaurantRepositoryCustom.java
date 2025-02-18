package com.sparta.spring_deep._delivery.domain.restaurant.repository;

import com.sparta.spring_deep._delivery.domain.address.Address;
import com.sparta.spring_deep._delivery.domain.category.Category;
import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {
  Page<Restaurant> findBySearchOption(Pageable pageable, UUID id, String restaurantName, Category category);
  Page<Restaurant> findAllMember();
}
