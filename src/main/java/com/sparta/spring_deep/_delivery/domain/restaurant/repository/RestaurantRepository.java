package com.sparta.spring_deep._delivery.domain.restaurant.repository;

import com.sparta.spring_deep._delivery.domain.restaurant.entity.Restaurant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

}
