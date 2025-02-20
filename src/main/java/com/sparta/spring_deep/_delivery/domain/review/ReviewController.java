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
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto requestDto,
        @RequestParam String userId) {
//         userDetalis 커스텀 클래스 생성 완료시 -> "CUSTOMER" 알때만 실행가능하도록 수정
//        if () {
//            throw new RuntimeException("사용자만 리뷰를 남길 수 있습니다.");
//        }

        log.info("Create Review : {}", requestDto);
        User user = new User(); // 임시
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
        @PathVariable String reviewId,
        @RequestParam("comment") String comment,
        @RequestParam("rating") int rating) {
        log.info("Update Review - reviewId :{}", reviewId);

        // 사용자 인증 예정 : 그 전까지만 User 객체 선언하여 사용
        User user = new User();
        ReviewResponseDto responseDto = reviewService.updateReview(UUID.fromString(reviewId),
            comment, rating, user);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 리뷰 삭제
    @PutMapping("/reviews/{reviewId}/delete")
    public ResponseEntity<String> deleteReview(@PathVariable String reviewId) {
        log.info("Delete Review - reviewId :{}", reviewId);

        // 사용자 인증 예정 : 그 전까지만 임의의 User 객체 사용 예정
        User user = new User();
        return reviewService.deleteReview(UUID.fromString(reviewId), user);
    }
}
