package com.sparta.spring_deep._delivery.domain.restaurant;

import static com.sparta.spring_deep._delivery.domain.restaurant.QRestaurant.restaurant;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.category.Category;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Restaurant> searchByOption(Pageable pageable, UUID id,
        String restaurantName, Category category) {
//        List<RestaurantResponseDto> restaurants = queryFactory
//            .select(Projections.constructor(RestaurantResponseDto.class,
//                restaurant.id,
//                restaurant.owner.username,
//                restaurant.category.name,
//                restaurant.name,
//                restaurant.address,
//                restaurant.phone))
//            .from(restaurant)
//            .leftJoin(restaurant.category)
//            .leftJoin(restaurant.owner)
//            .where(eqId(id),
//                eqRestaurantName(restaurantName),
//                eqCategory(category),
//                restaurant.isDeleted.eq(false))
//            .offset(pageable.getOffset()) // 몇 번째 페이지부터 시작할 것인지.
//            .limit(pageable.getPageSize()) // 페이지당 몇개의 데이터를 보여줄건지.
//            .fetch();
        List<Restaurant> restaurants = queryFactory
            .select(restaurant)
            .from(restaurant)
            .leftJoin(restaurant.category)
            .leftJoin(restaurant.owner)
            .where(eqId(id),
                eqRestaurantName(restaurantName),
                restaurant.isDeleted.eq(false))
            .offset(pageable.getOffset()) // 몇 번째 페이지부터 시작할 것인지.
            .limit(pageable.getPageSize()) // 페이지당 몇개의 데이터를 보여줄건지.
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(restaurant.count())
            .from(restaurant)
            .where(eqId(id),
                eqRestaurantName(restaurantName),
                eqCategory(category),
                restaurant.isDeleted.eq(false));

        return PageableExecutionUtils.getPage(restaurants, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqId(UUID id) {
        return id == null ? null : restaurant.id.eq(id);
    }

    private BooleanExpression eqRestaurantName(String restaurantName) {
        return restaurantName == null ? null : restaurant.name.containsIgnoreCase(restaurantName);
    }

    private BooleanExpression eqCategory(Category category) {
        return category == null ? null : restaurant.category.id.eq(category.getId());
    }
}
