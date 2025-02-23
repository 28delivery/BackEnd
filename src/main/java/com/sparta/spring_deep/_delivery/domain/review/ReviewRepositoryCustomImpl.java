package com.sparta.spring_deep._delivery.domain.review;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewResponseDto> searchByOptionAndIsDeletedFalse(UUID restaurantId,
        ReviewSearchDto searchDto, Pageable pageable) {
        log.info("searchByOptionAndIsDeletedFalse");

        QReview review = QReview.review;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        // 삭제되지 않은 리뷰만 조회
        builder.and(review.isDeleted.eq(false));
        // 가게 번호
        builder.and(review.order.restaurant.id.eq(restaurantId));

        // 사용자 이름 (완전 일치)
        if (searchDto.getUsername() != null && !searchDto.getUsername().isEmpty()) {
            builder.and(review.user.username.eq(searchDto.getUsername()));
        }

        // 평점 (완전 일치)
        if (searchDto.getRating() != null && searchDto.getRating() > 0) {
            builder.and(review.rating.eq(searchDto.getRating()));
        }

        // 리뷰 내용 (부분 일치)
        if (searchDto.getComment() != null && !searchDto.getComment().isEmpty()) {
            builder.and(review.comment.contains(searchDto.getComment()));
        }

        // 리뷰 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<ReviewResponseDto> content = queryFactory
            .select(Projections.constructor(
                ReviewResponseDto.class,
                review.id,
                review.order.id,
                review.user.username,
                review.comment,
                review.rating,
                review.updatedAt,
                review.updatedBy
            ))
            .from(review)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(review.id)
            .from(review)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
