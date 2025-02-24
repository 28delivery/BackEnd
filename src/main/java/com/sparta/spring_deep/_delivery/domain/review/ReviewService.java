package com.sparta.spring_deep._delivery.domain.review;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.DeletedDataAccessException;
import com.sparta.spring_deep._delivery.exception.OperationNotAllowedException;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ReviewService")
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    // 리뷰 작성
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, User user) {
        log.info("리뷰 작성");

        Order order = orderRepository.findByIdAndIsDeletedFalse(requestDto.getOrderId())
            .orElseThrow(ResourceNotFoundException::new);

        ownerCheck(order.getCustomer(), user);

        if (!order.getStatus().equals(OrderStatusEnum.DELIVERED)) {
            log.error("배송 완료만 리뷰작성 가능");
            throw new OperationNotAllowedException();
        }

        Review review = reviewRepository.save(
            new Review(order, user, requestDto.getRating(), requestDto.getComment()));

        return new ReviewResponseDto(review);
    }

    // 특정 음식점 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(UUID restaurantId, Pageable pageable) {
        log.info("특정 음식점 리뷰 조회");

        restaurantRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(ResourceNotFoundException::new);

        Page<Review> reviews = reviewRepository.searchReviews(restaurantId, pageable);

        return reviews.map(ReviewResponseDto::new);
    }

    // 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(UUID reviewId) {
        log.info("리뷰 조회");

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId);

        Order order = review.getOrder();
        if (order.getIsDeleted()) {
            throw new DeletedDataAccessException();
        }

        return new ReviewResponseDto(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, String comment, int rating, User user) {
        log.info("리뷰 수정");

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId);

        if (review == null) {
            log.error("존재하지 않는 리뷰");
            throw new ResourceNotFoundException();
        }

        ownerCheck(user, review.getUser());

        review.updateReview(comment, rating, user);

        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public ResponseEntity<String> deleteReview(UUID reviewId, User user) {
        log.info("리뷰 삭제");

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId);

        if (review == null) {
            log.error("존재하지 않는 리뷰");
            throw new ResourceNotFoundException();
        }

        ownerCheck(user, review.getUser());

        review.delete(user.getUsername());

        return ResponseEntity.ok("Success deleted");
    }
}
