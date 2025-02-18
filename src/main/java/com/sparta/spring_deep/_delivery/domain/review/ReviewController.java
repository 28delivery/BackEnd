package com.sparta.spring_deep._delivery.domain.review;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping
    public ReviewResponseDto createReview(@RequestBody ReviewRequestDto requestDto,
        @RequestParam String userId) {
        // userDetalis 커스텀 클래스 생성 완료시 -> "CUSTOMER" 알때만 실행가능하도록 수정
//        if () {
//            throw new RuntimeException("사용자만 리뷰를 남길 수 있습니다.");
//        }

        return reviewService.createReview(requestDto, userId);
    }

    // 특정 음식점 리뷰 조회
    @GetMapping("/{restaurantId}/search")
    public Page<ReviewResponseDto> searchReview(
        @PathVariable String restaurantId,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "false") boolean isAsc) {

        return reviewService.getReviewList(UUID.fromString(restaurantId), pageable.getPageNumber(),
            pageable.getPageSize(), sortBy,
            isAsc);
    }

    // 리뷰 조회
    @GetMapping("/{reviewId}")
    public ReviewResponseDto getReview(@PathVariable String reviewId) {
        return reviewService.getReview(UUID.fromString(reviewId));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ReviewResponseDto updateReview(
        @PathVariable String reviewId,
        @RequestParam("comment") String comment,
        @RequestParam("rating") int rating) {

        return reviewService.updateReview(UUID.fromString(reviewId), comment, rating);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId) {
        // 사용자 인증 예정
        return reviewService.deleteReview(UUID.fromString(reviewId));
    }
}
