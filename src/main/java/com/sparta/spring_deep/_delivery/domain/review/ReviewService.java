package com.sparta.spring_deep._delivery.domain.review;

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
    public ReviewResponseDto createReview(ReviewRequestDto requestDto, String userId) {
        Review review = reviewRepository.save(new Review(requestDto, userId));
        return new ReviewResponseDto(review);
    }

    // 특정 음식점 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewList(UUID restaurantId, int page, int size,
        String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Direction.ASC : Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewList = reviewRepository.findAllByRestaurantId(restaurantId, pageable);

        return reviewList.map(ReviewResponseDto::new);
    }

    // 리뷰 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityExistsException("review not found"));

        return new ReviewResponseDto(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, String comment, int rating) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new EntityExistsException("review not found"));

        review.setComment(comment);
        review.setRating(rating);

        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    public ResponseEntity<String> deleteReview(UUID reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityExistsException("review not found");
        }
        reviewRepository.deleteById(reviewId);

        if (reviewRepository.existsById(reviewId)) {
            throw new EntityExistsException("review not deleted");
        }
        return ResponseEntity.ok("Success");
    }
}
