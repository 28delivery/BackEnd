package com.sparta.spring_deep._delivery.domain.review;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    Review findByIdAndIsDeletedFalse(UUID reviewId);

    Page<Review> findByOrderIdInAndIsDeletedFalse(List<UUID> orderIds, Pageable pageable);
}
