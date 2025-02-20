package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.domain.user.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(
        @AuthenticationPrincipal User user,
        @RequestBody ReviewRequestDto requestDto) {
        
        log.info("Create Review : {}", requestDto);

        ReviewResponseDto responseDto = reviewService.createReview(requestDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 특정 음식점 리뷰 조회
    @GetMapping("/reviews/{restaurantId}/search")
    public ResponseEntity<Page<ReviewResponseDto>> searchReview(
        @PathVariable String restaurantId,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "false") boolean isAsc) {

        log.info("특정 음식점 리뷰 조회 - restaurantId :{}", restaurantId);

        Page<ReviewResponseDto> responseDtos = reviewService.getReviews(
            UUID.fromString(restaurantId), pageable.getPageNumber(),
            pageable.getPageSize(), sortBy,
            isAsc);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    // 리뷰 조회
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable String reviewId) {
        log.info("리뷰 조회 - reviewId :{}", reviewId);

        ReviewResponseDto responseDto = reviewService.getReview(UUID.fromString(reviewId));

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
        @AuthenticationPrincipal User user,
        @PathVariable String reviewId,
        @RequestBody ReviewRequestDto requestDto) {
        log.info("Update Review - reviewId :{}", reviewId);

        ReviewResponseDto responseDto = reviewService.updateReview(UUID.fromString(reviewId),
            requestDto.getComment(), requestDto.getRating(), user);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 리뷰 삭제
    @PutMapping("/reviews/{reviewId}/delete")
    public ResponseEntity<String> deleteReview(
        @AuthenticationPrincipal User user,
        @PathVariable String reviewId) {
        log.info("Delete Review - reviewId :{}", reviewId);

        return reviewService.deleteReview(UUID.fromString(reviewId), user);
    }
}
