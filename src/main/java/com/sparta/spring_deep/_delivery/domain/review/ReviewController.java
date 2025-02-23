package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody ReviewRequestDto requestDto) {

        log.info("Create Review : {}", requestDto);

        ReviewResponseDto responseDto = reviewService.createReview(requestDto,
            userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 특정 음식점 리뷰 조회
    @GetMapping("/reviews/{restaurantId}/search")
    public ResponseEntity<Page<ReviewResponseDto>> searchReview(
        @PathVariable UUID restaurantId,
        @ModelAttribute ReviewSearchDto searchDto,
        @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("특정 음식점 리뷰 조회 - restaurantId :{}", restaurantId);

        Page<ReviewResponseDto> responseDtos = reviewService.searchReviews(
            restaurantId, searchDto, pageable);

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
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable String reviewId,
        @RequestBody ReviewRequestDto requestDto) {
        log.info("Update Review - reviewId :{}", reviewId);

        ReviewResponseDto responseDto = reviewService.updateReview(UUID.fromString(reviewId),
            requestDto.getComment(), requestDto.getRating(), userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable String reviewId) {
        log.info("Delete Review - reviewId :{}", reviewId);

        return reviewService.deleteReview(UUID.fromString(reviewId), userDetails.getUser());
    }
}
