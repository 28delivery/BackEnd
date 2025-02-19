package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.domain.user.User;
import jakarta.persistence.EntityExistsException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 작성
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, User user) {
        Review review = reviewRepository.save(new Review(requestDto, user));
        return new ReviewResponseDto(review);
    }

    // 특정 음식점 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(UUID restaurantId, int page, int size,
        String sortBy, boolean isAsc) {
        
        Sort sort = Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewList = reviewRepository.findAllByRestaurantIdAndIsDeletedFalse(
            restaurantId, pageable);

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

        if (review.getDeletedBy() != null) {
            throw new EntityExistsException("review already deleted");
        }
        review.delete(user);

        return ResponseEntity.ok("Success deleted");
    }
}
