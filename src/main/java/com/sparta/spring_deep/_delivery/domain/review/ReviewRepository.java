package com.sparta.spring_deep._delivery.domain.review;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

    Review findByIdAndIsDeletedFalse(UUID reviewId);
    
    int countByOrderId(UUID orderId);

    List<Review> findAllByOrderId(UUID orderId);
}
