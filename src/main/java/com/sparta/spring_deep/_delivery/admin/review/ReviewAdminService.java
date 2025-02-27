package com.sparta.spring_deep._delivery.admin.review;

import com.sparta.spring_deep._delivery.domain.order.Order;
import com.sparta.spring_deep._delivery.domain.order.OrderRepository;
import com.sparta.spring_deep._delivery.domain.review.Review;
import com.sparta.spring_deep._delivery.domain.review.ReviewResponseDto;
import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ReviewAdminService")
public class ReviewAdminService {

    private final ReviewAdminRepository reviewAdminRepository;
    private final OrderRepository orderRepository;

    // 음식점 내 모든 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(
        User admin,
        UUID restaurantId, int page, int size,
        String sortBy, boolean isAsc) {
        log.info("getReviews");

        Pageable pageable = PageRequest.of(page, size,
            Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy));

        List<Order> orders = orderRepository.findAllByRestaurantId(restaurantId).orElseThrow(
            () -> new ResourceNotFoundException("해당 레스토랑의 리뷰가 존재하지 않습니다.")
        );

        List<UUID> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        Page<Review> reviewList = reviewAdminRepository.findByOrderIdInAndIsDeletedFalse(orderIds,
            pageable);

        return reviewList.map(ReviewResponseDto::new);
    }

    public Page<ReviewAdminResponseDto> searchReviews(ReviewAdminSearchDto searchDto,
        Pageable pageable) {
        log.info("searchReviews");

        Page<ReviewAdminResponseDto> responseDtos = reviewAdminRepository.searchByOption(searchDto,
            pageable);

        // 리뷰 검색 및 조회 값이 비어있다면 Exception 발생
        if (responseDtos.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return responseDtos;
    }

//    // 리뷰 삭제
//    @Transactional
//    public ResponseEntity<String> deleteReview(UUID reviewId, User user) {
//        if (!user.getRole().equals(UserRole.ADMIN)) {
//            throw new AccessDeniedException("Only Admin can deleted review");
//        }
//
//        Review review = reviewAdminRepository.findById(reviewId)
//            .orElseThrow(() -> new EntityExistsException("review not found"));
//
//        if (review.getDeletedBy() != null) {
//            throw new EntityExistsException("review already deleted");
//        }
//        review.delete(user.getUsername());
//
//        return ResponseEntity.ok("Success deleted");
//    }
}
