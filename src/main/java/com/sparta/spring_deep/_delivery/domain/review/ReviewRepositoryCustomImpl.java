package com.sparta.spring_deep._delivery.domain.review;

import static com.sparta.spring_deep._delivery.domain.order.QOrder.order;
import static com.sparta.spring_deep._delivery.domain.restaurant.QRestaurant.restaurant;
import static com.sparta.spring_deep._delivery.domain.review.QReview.review;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Slf4j(topic = "ReviewRepositoryQueryDSL")
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> searchReviews(UUID restaurantId, Pageable pageable) {
        log.info("searchReviews");
        
        // pageable의 정렬 조건 처리 (기본은 createdAt 내림차순)
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        pageable.getSort().forEach(sort -> {
            if ("createdAt".equalsIgnoreCase(sort.getProperty())) {
                orderSpecifiers.add(sort.isAscending()
                    ? review.createdAt.asc()
                    : review.createdAt.desc());
            } else if ("rating".equalsIgnoreCase(sort.getProperty())) {
                orderSpecifiers.add(sort.isAscending()
                    ? review.rating.asc()
                    : review.rating.desc());
            }
        });
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(review.createdAt.desc());
        }

        // review → order → restaurant 조인을 통해 단일 restaurantId 조건과 isDeleted=false 필터 적용
        List<Review> reviews = queryFactory
            .selectFrom(review)
            .join(review.order, order)
            .join(order.restaurant, restaurant)
            .where(
                restaurant.id.eq(restaurantId)
                    .and(review.isDeleted.eq(false))
            )
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 전체 건수 조회
        Long total = queryFactory
            .select(review.count())
            .from(review)
            .join(review.order, order)
            .join(order.restaurant, restaurant)
            .where(
                restaurant.id.eq(restaurantId)
                    .and(review.isDeleted.eq(false))
            )
            .fetchOne();

        return new PageImpl<>(reviews, pageable, total != null ? total : 0);
    }
}
