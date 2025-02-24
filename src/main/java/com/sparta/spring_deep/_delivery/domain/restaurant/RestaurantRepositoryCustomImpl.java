package com.sparta.spring_deep._delivery.domain.restaurant;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "RestaurantRepositoryQueryDSL")
public class RestaurantRepositoryCustomImpl implements RestaurantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RestaurantResponseDto> searchByOptionAndIsDeletedFalse(
        RestaurantSearchDto restaurantSearchDto, Pageable pageable) {
        log.info("searchByOptionAndIsDeletedFalse");

        QRestaurant restaurant = QRestaurant.restaurant;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        // 삭제되지 않은 음식점만 조회
        builder.and(restaurant.isDeleted.eq(false));

        // 이름 조건 (부분 일치, 대소문자 무시)
        if (restaurantSearchDto.getName() != null && !restaurantSearchDto.getName().isEmpty()) {
            builder.and(restaurant.name.containsIgnoreCase(restaurantSearchDto.getName()));
        }

        // 카테고리 조건
        if (restaurantSearchDto.getCategory() != null) {
            builder.and(restaurant.category.eq(restaurantSearchDto.getCategory()));
        }

        // 전화번호 조건 (부분 일치)
        if (restaurantSearchDto.getPhone() != null && !restaurantSearchDto.getPhone().isEmpty()) {
            builder.and(restaurant.phone.contains(restaurantSearchDto.getPhone()));
        }

        // 음식점 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<RestaurantResponseDto> content = queryFactory
            .select(Projections.constructor(
                RestaurantResponseDto.class,
                restaurant.id,
                restaurant.owner.username,
                restaurant.category,
                restaurant.name,
                restaurant.phone,
                restaurant.restaurantAddress.roadAddr,
                restaurant.restaurantAddress.jibunAddr,
                restaurant.restaurantAddress.detailAddr,
                restaurant.restaurantAddress.engAddr
                // 필요한 다른 필드 추가 가능
            ))
            .from(restaurant)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(restaurant.id)
            .from(restaurant)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
