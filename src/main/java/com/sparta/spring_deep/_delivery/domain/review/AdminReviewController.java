package com.sparta.spring_deep._delivery.domain.review;



import com.sparta.spring_deep._delivery.domain.user.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    // 음식점 내 모든 리뷰 조회
    @GetMapping("/reviews/{restaurantId}/search")
    public ResponseEntity<Page<ReviewResponseDto>> searchReview(
        @AuthenticationPrincipal User owner,
        @PathVariable String restaurantId,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "false") boolean isAsc) {

        Page<ReviewResponseDto> responseDtos = adminReviewService.getReviews(
            owner,
            UUID.fromString(restaurantId),
            pageable.getPageNumber(),
            pageable.getPageSize(), sortBy,
            isAsc);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

}
