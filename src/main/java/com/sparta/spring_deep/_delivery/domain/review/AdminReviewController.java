package com.sparta.spring_deep._delivery.domain.review;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    // 음식점 내 모든 리뷰 조회
    @GetMapping("/{restaurantId}/search")
    public Page<ReviewResponseDto> searchReview(
        @PathVariable String restaurantId,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "false") boolean isAsc) {

        // admin 인증 처리 예정

        return adminReviewService.getReviews(UUID.fromString(restaurantId),
            pageable.getPageNumber(),
            pageable.getPageSize(), sortBy,
            isAsc);
    }

}
