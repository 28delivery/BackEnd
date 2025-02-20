package com.sparta.spring_deep._delivery.admin.service;

import com.sparta.spring_deep._delivery.admin.repository.ReviewAdminRepository;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.review.Review;
import com.sparta.spring_deep._delivery.domain.review.ReviewResponseDto;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import jakarta.persistence.EntityExistsException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewAdminService {

    private final ReviewAdminRepository reviewAdminRepository;
    private final OrderRepository orderRepository;

    // 음식점 내 모든 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(
        User admin,
        UUID restaurantId, int page, int size,
        String sortBy, boolean isAsc) {

        if (admin.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only Admin can access");
        }

        Pageable pageable = PageRequest.of(page, size,
            Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy));

        List<UUID> orderIds = orderRepository.findOrderIdsByRestaurantId(restaurantId);

        Page<Review> reviewList = reviewAdminRepository.findByOrderIdInAndIsDeletedFalse(orderIds,
            pageable);

        return reviewList.map(ReviewResponseDto::new);
    }

    // 리뷰 삭제
    @Transactional
    public ResponseEntity<String> deleteReview(UUID reviewId, User user) {
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Only Admin can deleted review");
        }

        Review review = reviewAdminRepository.findById(reviewId)
            .orElseThrow(() -> new EntityExistsException("review not found"));

        if (review.getDeletedBy() != null) {
            throw new EntityExistsException("review already deleted");
        }
        review.delete(user.getUsername());

        return ResponseEntity.ok("Success deleted");
    }
}
