package com.sparta.spring_deep._delivery.domain.order;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.order.orderItem.QOrderItem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "OrderRepositoryQueryDSL")
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderResponseDto> searchMyOrdersByOptionAndIsDeletedFalse(String username,
        OrderSearchDto searchDto, Pageable pageable) {
        log.info("searchMyOrdersByOptionAndIsDeletedFalse");

        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        // 삭제되지 않은 주문만 조회
        builder.and(order.isDeleted.eq(false));
        // 내 주문만 조회
        builder.and(order.customer.username.eq(username));

        // 음식점 이름 (부분 일치, 대소문자 무시)
        if (searchDto.getRestaurantName() != null && !searchDto.getRestaurantName().isEmpty()) {
            builder.and(order.restaurant.name.eq(searchDto.getRestaurantName()));
        }

        // 메뉴 이름 (부분 일치, 대소문자 무시)
        if (searchDto.getMenuName() != null && !searchDto.getMenuName().isEmpty()) {
            builder.and(orderItem.menu.name.eq(searchDto.getMenuName()));
        }

        // 주문 상태별 조회
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
                order.address.id,
                order.status,
                order.totalPrice,
                order.request,
                order.createdAt,
                order.updatedAt
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
}
