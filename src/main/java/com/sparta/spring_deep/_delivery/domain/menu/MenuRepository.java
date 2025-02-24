package com.sparta.spring_deep._delivery.domain.menu;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID>, MenuRepositoryCustom {

    Optional<Menu> findByIdAndIsDeletedFalse(UUID menuId);

    boolean existsByRestaurantAndName(Restaurant restaurant, String name);
}
