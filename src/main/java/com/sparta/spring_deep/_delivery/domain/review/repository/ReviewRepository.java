package com.sparta.spring_deep._delivery.domain.review.repository;

import com.sparta.spring_deep._delivery.domain.review.model.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

    Optional<Review> findByIdAndIsDeletedFalse(UUID reviewId);

    List<Review> findAllByOrderId(UUID orderId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.isDeleted = false")
    double findAverageRating();
}
