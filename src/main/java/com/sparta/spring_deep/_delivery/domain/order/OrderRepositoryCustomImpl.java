package com.sparta.spring_deep._delivery.domain.order;

import static com.sparta.spring_deep._delivery.domain.menu.QMenu.menu;
import static com.sparta.spring_deep._delivery.domain.order.QOrder.order;
import static com.sparta.spring_deep._delivery.domain.order.orderItem.QOrderItem.orderItem;
import static com.sparta.spring_deep._delivery.domain.restaurant.QRestaurant.restaurant;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Order> searchOrders(String username,
        Pageable pageable, String menuName, String restaurantName) {

        // 기본 : 사용자명과 isDeleted = false 만 필터링
        BooleanExpression baseCondition = customerUsernameEq(username).and(order.isDeleted)
            .eq(false);

        // QueryBuilder 생성
        JPAQuery<Order> query = queryFactory
            .selectFrom(order)
            .join(order.orderItems, orderItem)
            .join(orderItem.menu, menu)
            .join(order.restaurant, restaurant)
            .where(baseCondition);

        // 메뉴 이름이 들어온 경우 menu name 필터링 추가
        if (menuName != null) {
            query.where(menu.name.eq(menuName));
        }

        if (restaurantName != null) {
            query.where(restaurant.name.eq(restaurantName));
        }

        //

        // 주문 내역 조회 쿼리
        List<Order> orders = query
            .distinct()
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 전체 건수 조회
        Long total = queryFactory
            .select(order.id.countDistinct())
            .from(order)
            .join(order.orderItems, orderItem)
            .join(orderItem.menu, menu)
            .join(order.restaurant, restaurant)
            .where(baseCondition)
            .fetchOne();

        return new PageImpl<>(orders, pageable, total != null ? total : 0);
    }

    private BooleanExpression customerUsernameEq(String username) {
        return username != null ? order.customer.username.eq(username) : null;
    }
}
