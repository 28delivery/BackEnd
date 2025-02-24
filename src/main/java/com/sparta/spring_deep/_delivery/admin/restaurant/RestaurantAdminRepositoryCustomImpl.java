package com.sparta.spring_deep._delivery.admin.restaurant;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.spring_deep._delivery.domain.restaurant.QRestaurant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j(topic = "RestaurantAdminRepositoryQueryDSL")
public class RestaurantAdminRepositoryCustomImpl implements RestaurantAdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RestaurantAdminResponseDto> searchByOption(
        RestaurantAdminSearchDto searchDto, Pageable pageable) {
        log.info("searchByOption");

        QRestaurant restaurant = QRestaurant.restaurant;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // ** Admin 용 조건 **
        // 생성 날짜 범위 검색
        builder.and(
            dateSearch(restaurant.createdAt, searchDto.getCreatedFrom(), searchDto.getCreatedTo()));
        // 수정 날짜 범위 검색
        builder.and(
            dateSearch(restaurant.updatedAt, searchDto.getUpdatedFrom(), searchDto.getUpdatedTo()));
        // 삭제 날짜 범위 검색
        builder.and(
            dateSearch(restaurant.deletedAt, searchDto.getDeletedFrom(), searchDto.getDeletedTo()));
        // 삭제 여부 조회 (기본값 false)
        if (searchDto.getIsDeleted() == null || !searchDto.getIsDeleted()) {
            builder.and(restaurant.isDeleted.eq(false));
        }

        // 음식점 id 검색
        if (searchDto.getId() != null) {
            builder.and(restaurant.id.eq(searchDto.getId()));
        }

        // 음식점 소유주 id 검색
        if (searchDto.getOwnerId() != null && !searchDto.getOwnerId().isEmpty()) {
            builder.and(restaurant.owner.username.eq(searchDto.getOwnerId()));
        }

        // 이름 조건 (부분 일치, 대소문자 무시)
        if (searchDto.getName() != null && !searchDto.getName().isEmpty()) {
            builder.and(restaurant.name.containsIgnoreCase(searchDto.getName()));
        }

        // 카테고리 조건
        if (searchDto.getCategory() != null) {
            builder.and(restaurant.category.eq(searchDto.getCategory()));
        }

        // 도로명 조건 (부분 일치)
        if (searchDto.getRoadAddr() != null && !searchDto.getRoadAddr().isEmpty()) {
            builder.and(
                restaurant.restaurantAddress.roadAddr.containsIgnoreCase(searchDto.getRoadAddr()));
        }

        // 지번주소 조건 (부분 일치)
        if (searchDto.getJibunAddr() != null && !searchDto.getJibunAddr().isEmpty()) {
            builder.and(restaurant.restaurantAddress.jibunAddr.containsIgnoreCase(
                searchDto.getJibunAddr()));
        }

        // 상세주소 조건 (부분 일치)
        if (searchDto.getDetailAddr() != null && !searchDto.getDetailAddr().isEmpty()) {
            builder.and(restaurant.restaurantAddress.detailAddr.containsIgnoreCase(
                searchDto.getDetailAddr()));
        }

        // 영문주소 조건 (부분 일치)
        if (searchDto.getEngAddr() != null && !searchDto.getEngAddr().isEmpty()) {
            builder.and(
                restaurant.restaurantAddress.engAddr.containsIgnoreCase(searchDto.getEngAddr()));
        }

        // 전화번호 조건 (부분 일치)
        if (searchDto.getPhone() != null && !searchDto.getPhone().isEmpty()) {
            builder.and(restaurant.phone.contains(searchDto.getPhone()));
        }

        // 음식점 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<RestaurantAdminResponseDto> content = queryFactory
            .select(Projections.constructor(
                RestaurantAdminResponseDto.class,
                restaurant.id,
                restaurant.owner.username,
                restaurant.category,
                restaurant.name,
                restaurant.restaurantAddress.roadAddr,
                restaurant.restaurantAddress.jibunAddr,
                restaurant.restaurantAddress.detailAddr,
                restaurant.restaurantAddress.engAddr,
                restaurant.phone,
                restaurant.createdAt,
                restaurant.createdBy,
                restaurant.updatedAt,
                restaurant.updatedBy,
                restaurant.isDeleted,
                restaurant.deletedAt,
                restaurant.deletedBy
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
