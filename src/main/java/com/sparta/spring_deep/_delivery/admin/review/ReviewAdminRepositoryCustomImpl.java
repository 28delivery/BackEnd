package com.sparta.spring_deep._delivery.admin.review;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.review.QReview;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "ReviewAdminRepositoryQueryDSL")
public class ReviewAdminRepositoryCustomImpl implements ReviewAdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewAdminResponseDto> searchByOption(ReviewAdminSearchDto searchDto,
        Pageable pageable) {
        log.info("searchByOption");

        QReview review = QReview.review;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // ** Admin 용 조건 **
        // 생성 날짜 범위 검색
        builder.and(
            dateSearch(review.createdAt, searchDto.getCreatedFrom(), searchDto.getCreatedTo()));
        // 수정 날짜 범위 검색
        builder.and(
            dateSearch(review.updatedAt, searchDto.getUpdatedFrom(), searchDto.getUpdatedTo()));
        // 삭제 날짜 범위 검색
        builder.and(
            dateSearch(review.deletedAt, searchDto.getDeletedFrom(), searchDto.getDeletedTo()));
        // 삭제 여부 조회 (기본값 false)
        if (searchDto.getIsDeleted() == null || !searchDto.getIsDeleted()) {
            builder.and(review.isDeleted.eq(false));
        }

        // 리뷰 id 검색
        if (searchDto.getId() != null) {
            builder.and(review.id.eq(searchDto.getId()));
        }

        // 음식점 id 검색
        if (searchDto.getRestaurantId() != null) {
            builder.and(review.order.restaurant.id.eq(searchDto.getRestaurantId()));
        }

        // 주문 id 검색
        if (searchDto.getOrderId() != null) {
            builder.and(review.order.id.eq(searchDto.getOrderId()));
        }

        // 사용자 검색 (완전 일치)
        if (searchDto.getUsername() != null && !searchDto.getUsername().isEmpty()) {
            builder.and(review.user.username.eq(searchDto.getUsername()));
        }

        // 평점 검색
        if (searchDto.getRating() != null) {
            builder.and(review.rating.eq(searchDto.getRating()));
        }

        // 리뷰 내용 검색
        if (searchDto.getComment() != null && !searchDto.getComment().isEmpty()) {
            builder.and(review.comment.containsIgnoreCase(searchDto.getComment()));
        }

        // 리뷰 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<ReviewAdminResponseDto> content = queryFactory
            .select(Projections.constructor(
                ReviewAdminResponseDto.class,
                review.id,
                review.order.id,
                review.order.restaurant.id,
                review.user.username,
                review.comment,
                review.rating,
                review.createdAt,
                review.createdBy,
                review.updatedAt,
                review.updatedBy,
                review.isDeleted,
                review.deletedAt,
                review.deletedBy
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

    private BooleanBuilder dateSearch(DateTimePath<LocalDateTime> dateTime,
        LocalDateTime dateFrom, LocalDateTime dateTo) {

        BooleanBuilder builder = new BooleanBuilder();

        if (dateFrom != null && dateTo != null) {
            builder.and(dateTime.between(dateFrom, dateTo));
        } else if (dateFrom != null) {
            builder.and(dateTime.goe(dateFrom));
        } else if (dateTo != null) {
            builder.and(dateTime.loe(dateTo));
        }
        return builder;
    }
}
