package com.sparta.spring_deep._delivery.admin.review;

import com.sparta.spring_deep._delivery.domain.review.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAdminRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByOrderIdInAndIsDeletedFalse(List<UUID> orderIds, Pageable pageable);
}
