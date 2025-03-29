package com.sparta.spring_deep._delivery.domain.review.service;

import static com.sparta.spring_deep._delivery.util.AuthTools.ownerCheck;

import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.order.OrderStatusEnum;
import com.sparta.spring_deep._delivery.domain.restaurant.RestaurantRepository;
import com.sparta.spring_deep._delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.spring_deep._delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.spring_deep._delivery.domain.review.dto.ReviewRestaurantResponseDto;
import com.sparta.spring_deep._delivery.domain.review.model.Review;
import com.sparta.spring_deep._delivery.domain.review.repository.ReviewRepository;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.DeletedDataAccessException;
import com.sparta.spring_deep._delivery.exception.OperationNotAllowedException;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.List;
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
        log.info("createReview");

        Order order = orderRepository.findByIdAndIsDeletedFalse(requestDto.getOrderId())
            .orElseThrow(ResourceNotFoundException::new);

        ownerCheck(order.getCustomer(), user);

        if (!order.getStatus().equals(OrderStatusEnum.DELIVERED)) {
            log.error("배송 완료만 리뷰작성 가능합니다.");
            throw new OperationNotAllowedException();
        }

        Review review = new Review(order, user, requestDto.getRating(), requestDto.getComment());
        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    // 특정 음식점 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewRestaurantResponseDto getReviews(UUID restaurantId, Pageable pageable) {
        log.info("getReviews");

        restaurantRepository.findByIdAndIsDeletedFalse(restaurantId)
            .orElseThrow(ResourceNotFoundException::new);

        List<ReviewResponseDto> reviews = reviewRepository.searchReviews(restaurantId, pageable)
            .map(ReviewResponseDto::new)
            .stream().toList();
        // 리뷰 평점 조회
        double rating = reviewRepository.findAverageRating();

        return new ReviewRestaurantResponseDto(reviews, rating, restaurantId);
    }

    // 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(UUID reviewId) {
        log.info("리뷰 조회");

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
            .orElseThrow(ResourceNotFoundException::new);

        if(review.getOrder().getIsDeleted()){
            throw new DeletedDataAccessException();
        }

        return new ReviewResponseDto(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, String comment, int rating, User user) {
        log.info("리뷰 수정");

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
            .orElseThrow(ResourceNotFoundException::new);

        ownerCheck(user, review.getUser());

        review.updateReview(comment, rating, user);

        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public ResponseEntity<String> deleteReview(UUID reviewId, User user) {
        log.info("리뷰 삭제");

        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
            .orElseThrow(ResourceNotFoundException::new);

        ownerCheck(user, review.getUser());

        review.delete(user.getUsername());

        return ResponseEntity.ok("Success deleted");
    }
}
