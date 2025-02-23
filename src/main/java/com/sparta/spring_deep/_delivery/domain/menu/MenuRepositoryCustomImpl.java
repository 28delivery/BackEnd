package com.sparta.spring_deep._delivery.domain.menu;

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
@Slf4j(topic = "MenuRepositoryQueryDSL")
public class MenuRepositoryCustomImpl implements MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MenuResponseDto> searchByOptionAndIsDeletedFalse(UUID restaurantId,
        MenuSearchDto searchDto, Pageable pageable) {
        log.info("searchByOptionAndIsDeletedFalse");

        QMenu menu = QMenu.menu;

        // 동적 조건 생성
        BooleanBuilder builder = new BooleanBuilder();
        // 삭제되지 않은 메뉴만 조회
        builder.and(menu.isDeleted.eq(false));
        // 레스토랑 기준 메뉴 조회
        builder.and(menu.restaurant.id.eq(restaurantId));
        // 숨겨지지 않은 메뉴만 조회
        builder.and(menu.isHidden.eq(false));

        // 이름 조건 (부분 일치, 대소문자 무시)
        if (searchDto.getName() != null && !searchDto.getName().isEmpty()) {
            builder.and(menu.name.contains(searchDto.getName()));
        }

        // 메뉴 정보 DTO로 매핑해서 페이징 처리된 결과 조회
        List<MenuResponseDto> content = queryFactory
            .select(Projections.constructor(
                MenuResponseDto.class,
                menu.id,
                menu.restaurant.id,
                menu.name,
                menu.description,
                menu.price,
                menu.isHidden
            ))
            .from(menu)
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 총 결과 수 조회
        long total = queryFactory
            .select(menu.id)
            .from(menu)
            .where(builder)
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}
