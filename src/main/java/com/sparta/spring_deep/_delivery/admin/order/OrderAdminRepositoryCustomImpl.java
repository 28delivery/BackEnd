package com.sparta.spring_deep._delivery.admin.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.order.OrderResponseDto;
import com.sparta.spring_deep._delivery.domain.order.QOrder;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "OrderAdminRepositoryQueryDSL")
public class OrderAdminRepositoryCustomImpl implements OrderAdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderResponseDto> searchByOption(OrderAdminSearchDto searchDto, Pageable pageable) {
        log.info("searchByOption");

        QOrder order = QOrder.order;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // ** Admin 용 조건 **
        // 생성 날짜 범위 검색
        builder.and(
            dateSearch(order.createdAt, searchDto.getCreatedFrom(), searchDto.getCreatedTo()));
        // 수정 날짜 범위 검색
        builder.and(
            dateSearch(order.updatedAt, searchDto.getUpdatedFrom(), searchDto.getUpdatedTo()));
        // 삭제 날짜 범위 검색
        builder.and(
            dateSearch(order.deletedAt, searchDto.getDeletedFrom(), searchDto.getDeletedTo()));
        // 삭제 여부 조회 (기본값 false)
        if (searchDto.getIsDeleted() == null || !searchDto.getIsDeleted()) {
            builder.and(order.isDeleted.eq(false));
        }

        // 주문 ID
        if (searchDto.getId() != null) {
            builder.and(order.id.eq(searchDto.getId()));
        }

        // 주문 고객 ID
        if (searchDto.getCustomerId() != null && !searchDto.getCustomerId().isEmpty()) {
            builder.and(order.customer.username.eq(searchDto.getCustomerId()));
        }

        // 음식점 ID
        if (searchDto.getRestaurantId() != null) {
            builder.and(order.restaurant.id.eq(searchDto.getRestaurantId()));
        }

        // 음식점 이름 (부분 일치, 대소문자 무시)
        if (searchDto.getRestaurantName() != null && !searchDto.getRestaurantName().isEmpty()) {
            builder.and(order.restaurant.name.eq(searchDto.getRestaurantName()));
        }

        // 주문 상태
        if (searchDto.getStatus() != null) {
            builder.and(order.status.eq(searchDto.getStatus()));
        }

        // 주문 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<OrderResponseDto> content = queryFactory
            .select(Projections.constructor(
                OrderResponseDto.class,
                order.id,
                order.customer.username,
                order.restaurant.id,
                order.restaurant.name,
                order.address.id,
                order.status,
                order.totalPrice,
                order.request,
                order.createdAt,
                order.createdBy,
                order.updatedAt,
                order.updatedBy,
                order.isDeleted,
                order.deletedAt,
                order.deletedBy
            ))
            .from(order)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(order.id)
            .from(order)
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
