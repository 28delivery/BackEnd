package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    // 리뷰 작성
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, User user) {

        Order order = orderRepository.findById(requestDto.getOrderId())
            .orElseThrow(() -> new EntityExistsException("주문을 찾을 수 없습니다."));

        if (!order.getCustomer().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException("Unauthorized access to create review");
        }

        Review review = reviewRepository.save(
            new Review(order, user, requestDto.getRating(), requestDto.getComment()));

        return new ReviewResponseDto(review);
    }

    // 특정 음식점 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(UUID restaurantId, int page, int size,
        String sortBy, boolean isAsc) {

        Sort sort = Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<UUID> orderIds = orderRepository.findOrderIdsByRestaurantId(restaurantId);

        Page<Review> reviewList = reviewRepository.findByOrderIdInAndIsDeletedFalse(orderIds,
            pageable);

        return reviewList.map(ReviewResponseDto::new);
    }

    // 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(UUID reviewId) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId);
        if (review == null) {
            throw new EntityExistsException("review is not found");
        }

        return new ReviewResponseDto(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, String comment, int rating, User user) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityExistsException("review not found"));

        if (!review.getUser().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException("Unauthorized access to update review");
        }

        if (review.getIsDeleted()) {
            throw new EntityExistsException("review is deleted");
        }

        review.updateReview(comment, rating, user);

        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public ResponseEntity<String> deleteReview(UUID reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityExistsException("review not found"));

        if (!review.getUser().getUsername().equals(user.getUsername())) {
            throw new AccessDeniedException("Unauthorized access to delete review");
        }

        if (review.getDeletedBy() != null) {
            throw new EntityExistsException("review already deleted");
        }
        review.delete(user.getUsername());

        return ResponseEntity.ok("Success deleted");
    }
}
