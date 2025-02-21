package com.sparta.spring_deep._delivery.domain.menu;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {

    Page<Menu> findAllByRestaurantId(Restaurant restaurant, Pageable pageable);

    Page<Menu> findAllByRestaurantIdAndIsDeletedFalse(Restaurant restaurantId, Pageable pageable);

    Optional<Menu> findByIdAndIsDeletedFalse(UUID menuId);
}
