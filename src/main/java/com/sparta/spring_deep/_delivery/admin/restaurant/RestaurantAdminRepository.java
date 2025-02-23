package com.sparta.spring_deep._delivery.admin.restaurant;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantAdminRepository extends JpaRepository<Restaurant, UUID>,
    RestaurantAdminRepositoryCustom {

    Optional<Restaurant> findByIdAndIsDeletedFalse(UUID id);

}
