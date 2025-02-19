package com.sparta.spring_deep._delivery.domain.review;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findAllByRestaurantIdAndIsDeletedFalse(UUID restaurantId, Pageable pageable);

    Review findByIdAndIsDeletedFalse(UUID reviewId);

}
