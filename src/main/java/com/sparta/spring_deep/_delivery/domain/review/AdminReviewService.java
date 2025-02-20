package com.sparta.spring_deep._delivery.domain.review;

import com.sparta.spring_deep._delivery.domain.order.OrderRepository;

import com.sparta.spring_deep._delivery.domain.user.entity.User;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    // 음식점 내 모든 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(
        User owner,
        UUID restaurantId, int page, int size,
        String sortBy, boolean isAsc) {

        if (owner.getRole().equals(UserRole.OWNER)) {
            throw new AccessDeniedException("Only Owner can access");
        }

        Pageable pageable = PageRequest.of(page, size,
            Sort.by(isAsc ? Direction.ASC : Direction.DESC, sortBy));

        List<UUID> orderIds = orderRepository.findOrderIdsByRestaurantId(restaurantId);

        Page<Review> reviewList = reviewRepository.findByOrderIdInAndIsDeletedFalse(orderIds,
            pageable);

        return reviewList.map(ReviewResponseDto::new);
    }

}
